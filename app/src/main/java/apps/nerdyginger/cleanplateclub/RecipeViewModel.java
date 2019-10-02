package apps.nerdyginger.cleanplateclub;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class RecipeViewModel extends AndroidViewModel {
    private UserCustomDatabase database;
    //private final LiveData<List<DATA_TYPE_HERE>> dataList;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        database = UserCustomDatabase.getDatabase(this.getApplication());
        //dataList = GET_DATA_HERE

    }

    //public LiveData<List<DATA_TYPE_HERE>> getRecipeList() {

    //public void deleteRecipe(Recipe_DATA_TYPE recipe) {

    //async operation for delete task
}
