package amal.souheil.savemytrip.todolist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import amal.souheil.savemytrip.R;
import amal.souheil.savemytrip.base.BaseActivity;
import amal.souheil.savemytrip.injections.Injection;
import amal.souheil.savemytrip.injections.ViewModelFactory;
import amal.souheil.savemytrip.models.Item;
import amal.souheil.savemytrip.models.User;
import amal.souheil.savemytrip.utils.AndroidHelpers;
import amal.souheil.savemytrip.utils.ItemClickSupport;
import butterknife.BindView;
import butterknife.OnClick;
public class TodoListActivity extends BaseActivity implements ItemAdapter.Listener {

    // FOR DESIGN
    @BindView(R.id.todo_list_activity_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.todo_list_activity_spinner)
    Spinner spinner;
    @BindView(R.id.todo_list_activity_edit_text)
    EditText editText;
    @BindView(R.id.todo_list_activity_header_profile_image)
    ImageView profileImage;
    @BindView(R.id.todo_list_activity_header_profile_text)
    TextView profileText;

    // FOR DATA
    private ItemViewModel itemViewModel;
    private ItemAdapter adapter;
    private static int USER_ID = 1;
    private Uri uri;

    @Override
    public int getLayoutContentViewID() {
        return R.layout.activity_todo_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.configureSpinner();

        this.configureRecyclerView();
        this.configureViewModel();

        this.getCurrentUser(USER_ID);
        this.getItems(USER_ID);
    }

    // -------------------
    // ACTIONS
    // -------------------

    @OnClick(R.id.todo_list_activity_button_add)
    public void onClickAddButton() {
        this.createItem();
    }

    @Override
    public void onClickDeleteButton(int position) {
        this.deleteItem(this.adapter.getItem(position));
    }

    // -------------------
    // DATA
    // -------------------

    private void configureViewModel() {
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(this);
        this.itemViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ItemViewModel.class);
        this.itemViewModel.init(USER_ID);
    }

    // ---

    private void getCurrentUser(int userId) {
        this.itemViewModel.getUser(userId).observe(this, this::updateHeader);
    }

    // ---

    private void getItems(int userId) {
        this.itemViewModel.getItems(userId).observe(this, this::updateItemsList);
    }

    private void createItem() {
        // Before creating the item, we need to check if a picture has been selected
        // in order to have different behaviors depending of this case.

        Item item;
        if (!isPictureSelected) {
            item = new Item(this.editText.getText().toString(), this.spinner.getSelectedItemPosition(), USER_ID);
        } else {
            item = new Item(this.editText.getText().toString(), this.spinner.getSelectedItemPosition(), USER_ID, String.valueOf(uri));
        }

        this.editText.setText("");
        this.itemViewModel.createItem(item);

        pictureButton.setBackgroundResource(R.drawable.ic_image_orange_light_36dp);
        isPictureSelected = false;
        AndroidHelpers.hideKeyboard(this);
    }

    private void deleteItem(Item item) {
        this.itemViewModel.deleteItem(item.getId());
    }

    private void updateItem(Item item) {
        item.setSelected(!item.getSelected());
        this.itemViewModel.updateItem(item);
    }

    // -------------------
    // UI
    // -------------------

    private void configureSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void configureRecyclerView() {
        this.adapter = new ItemAdapter(this);
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemClickSupport.addTo(recyclerView, R.layout.activity_todo_list_item)
                .setOnItemClickListener((recyclerView1, position, v) -> this.updateItem(this.adapter.getItem(position)));
    }

    private void updateHeader(User user) {
        this.profileText.setText(user.getUsername());
        Glide.with(this).load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(this.profileImage);
    }

    private void updateItemsList(List<Item> items) {
        this.adapter.updateData(items);
    }




    // -------------------
    // Gallery
    //      Functions and variables to open user's gallery in order to pick
    //      an image for the item to add
    // -------------------

    private static final int PICK_IMAGE = 2;
    private String TAG = "ImageSelection";

    @BindView(R.id.todo_list_activity_picture_button)
    ImageButton pictureButton;

    boolean isPictureSelected;

    /*
     This OnClick method just launch a specific intent with startActivityForResult
     Attending a onActivityResult
     */
    @OnClick(R.id.todo_list_activity_picture_button)
    public void onClickChoosePictureInGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    /*
     If the previous intent has been correctly launched, and an image has been chosen,
     then, we can process to an image treatment.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_IMAGE){
            if (resultCode == RESULT_OK) {
                try {
                    this.uri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(uri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), selectedImage);
                    pictureButton.setBackground(bitmapDrawable);

                    isPictureSelected = true;
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "onActivityResult: Getting the bitmap or setting it instead of icon failed", e);
                    e.printStackTrace();
                    Toast.makeText(this, "Un problème est survenu. Réessayez", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Vous n'avez pas sélectionné d'image", Toast.LENGTH_SHORT).show();
            }
        } else {
            this.uri = null;
        }

    }

    // -------------------
    // Sharing button
    // -------------------

    @Override
    public void onClickShareButton(int position) {
        Item item = this.adapter.getItem(position);
        Uri uri = Uri.parse(item.getPicturePath());
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Partagez l'image"));
    }

}
