package amal.souheil.savemytrip.injections;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import amal.souheil.savemytrip.repositories.ItemDataRepository;
import amal.souheil.savemytrip.repositories.UserDataRepository;
import amal.souheil.savemytrip.todolist.ItemViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final ItemDataRepository itemDataSource;
    private final UserDataRepository userDataSource;
    private final Executor executor;

    public ViewModelFactory(ItemDataRepository itemDataSource, UserDataRepository userDataSource, Executor executor) {
        this.itemDataSource = itemDataSource;
        this.userDataSource = userDataSource;
        this.executor = executor;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            return (T) new ItemViewModel(itemDataSource, userDataSource, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}