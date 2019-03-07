package amal.souheil.savemytrip.todolist;

import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import amal.souheil.savemytrip.R;
import amal.souheil.savemytrip.models.Item;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Souheil Amal on 2019-02-28
 */

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//public class ItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.activity_todo_list_item_text) TextView textView;
    @BindView(R.id.activity_todo_list_item_image) ImageView imageViewIcon;
    @BindView(R.id.activity_todo_list_item_photo) ImageView imageViewPicture;
    @BindView(R.id.activity_todo_list_item_share) ImageButton imageButtonShare;
    @BindView(R.id.activity_todo_list_item_remove) ImageButton imageButtonDelete;

    // FOR DATA
    private WeakReference<ItemAdapter.Listener> callbackWeakRef;

    public ItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithItem(Item item, ItemAdapter.Listener callback){
        this.callbackWeakRef = new WeakReference<ItemAdapter.Listener>(callback);
        this.textView.setText(item.getText());
        this.imageButtonDelete.setOnClickListener(this);
        this.imageButtonShare.setOnClickListener(this);

        if (item.getPicturePath() == null) {
            imageViewPicture.setVisibility(View.GONE);
            imageButtonShare.setVisibility(View.GONE);
        } else {
            Uri uri = Uri.parse(item.getPicturePath());
            this.imageViewPicture.setImageURI(uri);

        }

        switch (item.getCategory()){
            case 0: // TO VISIT
                this.imageViewIcon.setBackgroundResource(R.drawable.ic_room_black_24px);
                break;
            case 1: // IDEAS
                this.imageViewIcon.setBackgroundResource(R.drawable.ic_lightbulb_outline_black_24px);
                break;
            case 2: // RESTAURANTS
                this.imageViewIcon.setBackgroundResource(R.drawable.ic_local_cafe_black_24px);
                break;
        }
        if (item.getSelected()){
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_todo_list_item_remove:
                ItemAdapter.Listener callback = callbackWeakRef.get();
                if (callback != null) callback.onClickDeleteButton(getAdapterPosition());
                break;
            case R.id.activity_todo_list_item_share:
                ItemAdapter.Listener callback2 = callbackWeakRef.get();
                if (callback2 != null) callback2.onClickShareButton(getAdapterPosition());
                break;
        }



    }
}