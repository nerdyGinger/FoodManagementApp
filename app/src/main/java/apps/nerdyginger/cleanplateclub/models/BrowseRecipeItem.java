package apps.nerdyginger.cleanplateclub.models;

import android.media.Image;

public class BrowseRecipeItem {
    private String recipeName;
    private Image image;
    private String recipeCategory;

    public BrowseRecipeItem() {
        //empty constructor
    }

    // Partial constructor
    public BrowseRecipeItem(String name, String category) {
        this.recipeName = name;
        this.recipeCategory = category;
    }

    // Full constructor
    public BrowseRecipeItem(String name, Image image, String category) {
        this.recipeName = name;
        this.image = image;
        this.recipeCategory = category;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getRecipeCategory() {
        return recipeCategory;
    }

    public void setRecipeCategory(String recipeCategory) {
        this.recipeCategory = recipeCategory;
    }
}
