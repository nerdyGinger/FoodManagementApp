package apps.nerdyginger.cleanplateclub.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipeBox")
public class UserRecipeBox {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    private boolean userAdded;

    private int recipeId;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public boolean isUserAdded() {
        return userAdded;
    }

    public void setUserAdded(boolean userAdded) {
        this.userAdded = userAdded;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}
