package apps.nerdyginger.pocketpantry.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

//
// Used to store recipes that are in the user's recipe box (<- user recipe box dao)
// ALSO used to display recipes for the browse page (<- NOT related to user recipe box dao)
// Last edited: 2/19/2020
@Entity(tableName = "recipeBox")
public class UserRecipeBoxItem {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    private boolean userAdded;

    private int recipeId;

    private String recipeName;

    private String category;

    private String servings;

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

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }
}
