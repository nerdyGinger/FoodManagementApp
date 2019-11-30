package apps.nerdyginger.pocketpantry.view_models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;

public class InventoryViewModel extends AndroidViewModel {
    private final LiveData<List<UserInventoryItem>> dataList;
    private UserCustomDatabase database;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        database = UserCustomDatabase.getDatabase(this.getApplication());
        dataList = database.getUserInventoryDao().getAllInventoryItemsAsLiveData();
    }

    public LiveData<List<UserInventoryItem>> getInventoryList() {
        return dataList;
    }

    public void deleteItem(UserInventoryItem item) {
        new deleteAsyncTask(database).execute(item);
    }

    private static class deleteAsyncTask extends AsyncTask<UserInventoryItem, Void, Void> {
        private UserCustomDatabase db;

        deleteAsyncTask(UserCustomDatabase database) {
            db = database;
        }

        @Override
        protected Void doInBackground(final UserInventoryItem... params) {
            db.getUserInventoryDao().delete(params[0]);
            return null;
        }
    }
}
