package amal.souheil.savemytrip.repositories;

import android.arch.lifecycle.LiveData;

import amal.souheil.savemytrip.database.dao.ItemDao;
import amal.souheil.savemytrip.models.Item;

import java.util.List;

/**
 * Created by Souheil Amal on 2019-02-28
 */

public class ItemDataRepository {

    private final ItemDao itemDao;

    public ItemDataRepository(ItemDao itemDao) { this.itemDao = itemDao; }

    // --- GET ---

    public LiveData<List<Item>> getItems(long userId){ return this.itemDao.getItems(userId); }

    // --- CREATE ---

    public void createItem(Item item){ itemDao.insertItem(item); }

    // --- DELETE ---
    public void deleteItem(long itemId){ itemDao.deleteItem(itemId); }

    // --- UPDATE ---
    public void updateItem(Item item){ itemDao.updateItem(item); }

}
