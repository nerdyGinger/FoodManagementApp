package apps.nerdyginger.pocketpantry.view_models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.models.UserSchedule;

public class ScheduleViewModel extends AndroidViewModel {
    private UserCustomDatabase database;
    private final LiveData<List<UserSchedule>> dataList;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        database = UserCustomDatabase.getDatabase(this.getApplication());
        dataList = database.getUserScheduleDao().getAllScheduleItemsAsLiveData();
    }

    public LiveData<List<UserSchedule>> getScheduleList() {
        return dataList;
    }

    public void deleteItem(UserSchedule item) {
        new deleteAsyncTask(database).execute(item);
    }

    private static class deleteAsyncTask extends AsyncTask<UserSchedule, Void, Void> {
        private UserCustomDatabase db;

        deleteAsyncTask(UserCustomDatabase database) {
            db = database;
        }

        @Override
        protected Void doInBackground(final UserSchedule... params) {
            db.getUserScheduleDao().delete(params[0]);
            return null;
        }
    }
}
