package apps.nerdyginger.pocketpantry.view_models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.models.UserListItem;

public class ListItemViewModel extends AndroidViewModel {
    private final LiveData<List<UserListItem>> dataList;
    private UserCustomDatabase database;

    public ListItemViewModel(Application application) {
        super(application);
        database = UserCustomDatabase.getDatabase(this.getApplication());
        dataList = database.getUserListItemDao().getAllListItemsAsLiveData();
    }

    public LiveData<List<UserListItem>> getListItemList() {
        return dataList;
    }

    public void deleteItem(UserListItem item) {
        new deleteAsyncTask(database).execute(item);
    }

    private static class deleteAsyncTask extends AsyncTask<UserListItem, Void, Void> {
        private UserCustomDatabase db;

        deleteAsyncTask(UserCustomDatabase database) {
            db = database;
        }

        @Override
        protected Void doInBackground(final UserListItem... params) {
            db.getUserListItemDao().delete(params[0]);
            return null;
        }
    }
}
