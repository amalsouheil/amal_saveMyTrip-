package amal.souheil.savemytrip.injections;

import android.content.Context;



import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import amal.souheil.savemytrip.database.dao.SaveMyTripDatabase;
import amal.souheil.savemytrip.repositories.ItemDataRepository;
import amal.souheil.savemytrip.repositories.UserDataRepository;

/**
 * Created by Souheil Amal on 2019-02-28
 */

public class Injection {

    public static ItemDataRepository provideItemDataSource(Context context) {
        SaveMyTripDatabase database = SaveMyTripDatabase.getInstance(context);
        return new ItemDataRepository(database.itemDao());
    }

    public static UserDataRepository provideUserDataSource(Context context) {
        SaveMyTripDatabase database = SaveMyTripDatabase.getInstance(context);
        return new UserDataRepository(database.userDao());
    }

    public static Executor provideExecutor(){ return Executors.newSingleThreadExecutor(); }


    public static ViewModelFactory provideViewModelFactory(Context context) {
        ItemDataRepository dataSourceItem = provideItemDataSource(context);
        UserDataRepository dataSourceUser = provideUserDataSource(context);
        Executor executor = provideExecutor();
        return new ViewModelFactory(dataSourceItem, dataSourceUser, executor);
    }
}
