package apps.nerdyginger.cleanplateclub.view_models;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import apps.nerdyginger.cleanplateclub.UserCustomDatabase;
import apps.nerdyginger.cleanplateclub.models.UserRecipeBoxItem;

public class RecipeViewModel extends AndroidViewModel {
    private UserCustomDatabase database;
    private final LiveData<List<UserRecipeBoxItem>> dataList;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        database = UserCustomDatabase.getDatabase(this.getApplication());
        dataList = database.getUserRecipeBoxDao().getAllRecipesAsLiveData();
    }

    public LiveData<List<UserRecipeBoxItem>> getRecipeList() { return dataList; }

    public void deleteRecipe(UserRecipeBoxItem recipe) {
        new deleteAsyncTask(database).execute(recipe);
    }

    private static class deleteAsyncTask extends AsyncTask<UserRecipeBoxItem, Void, Void> {
        private UserCustomDatabase db;

        deleteAsyncTask(UserCustomDatabase database) {
            db = database;
        }

        @Override
        protected Void doInBackground(final UserRecipeBoxItem... params) {
            db.getUserRecipeBoxDao().delete(params[0]);
            return null;
        }
    }
}
