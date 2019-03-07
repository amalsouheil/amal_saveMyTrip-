package amal.souheil.savemytrip.repositories;

import android.arch.lifecycle.LiveData;



import amal.souheil.savemytrip.database.dao.UserDao;
import amal.souheil.savemytrip.models.User;

/**
 * Created by Souheil Amal on 2019-02-28
 */
public class UserDataRepository {

    private final UserDao userDao;

    public UserDataRepository(UserDao userDao) { this.userDao = userDao; }

    // --- GET USER ---
    public LiveData<User> getUser(long userId) { return this.userDao.getUser(userId); }
}
