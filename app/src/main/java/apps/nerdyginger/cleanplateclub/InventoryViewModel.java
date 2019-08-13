package apps.nerdyginger.cleanplateclub;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserInventory;

public class InventoryViewModel extends AndroidViewModel {
    private final LiveData<List<UserInventory>> dataList;
    private UserCustomDatabase database;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        database = UserCustomDatabase.getDatabase(this.getApplication());
        dataList = database.getUserInventoryDao().getAllInventoryItemsAsLiveData();
    }

    public LiveData<List<UserInventory>> getInventoryList() {
        return dataList;
    }

    public void deleteItem(UserInventory item) {
        new deleteAsyncTask(database).execute(item);
    }

    private static class deleteAsyncTask extends AsyncTask<UserInventory, Void, Void> {
        private UserCustomDatabase db;

        deleteAsyncTask(UserCustomDatabase database) {
            db = database;
        }

        @Override
        protected Void doInBackground(final UserInventory... params) {
            db.getUserInventoryDao().delete(params[0]);
            return null;
        }
    }
}
