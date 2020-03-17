package apps.nerdyginger.pocketpantry.models;

import android.graphics.Bitmap;

// Class to hold recipe items for sorting on the browse recipes page
// Very similar to UserRecipeBoxItem, but with the addition of ingredientsMissing
// and the exclusion of an ID field, plus, this makes the use of UserRecipeBoxItem
// much clearer.
// NOT connected to any db table
// Last edited: 2/20/2020
public class BrowseRecipeItem implements Comparable<BrowseRecipeItem>{
    private boolean userAdded;
    private int recipeId;
    private String recipeName;
    private String category;
    private String servings;
    private int ingredientsMissing;
    private int recipeBoxId; // for the home screen usage
    private Bitmap image;

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

    public int getIngredientsMissing() {
        return ingredientsMissing;
    }

    public void setIngredientsMissing(int ingredientsMissing) {
        this.ingredientsMissing = ingredientsMissing;
    }

    public int getRecipeBoxId() {
        return recipeBoxId;
    }

    public void setRecipeBoxId(int recipeBoxId) {
        this.recipeBoxId = recipeBoxId;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int compareTo(BrowseRecipeItem compareItem) {
        int compareQuantity  = compareItem.getIngredientsMissing();

        return compareQuantity - ingredientsMissing;
    }
}
