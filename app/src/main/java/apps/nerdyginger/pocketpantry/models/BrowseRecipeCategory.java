package apps.nerdyginger.pocketpantry.models;

import java.util.List;

public class BrowseRecipeCategory {
    private String categoryName;
    private List<UserRecipeBoxItem> recipeCards;

    public BrowseRecipeCategory() {
        //empty constructor
    }

    public BrowseRecipeCategory(String name, List<UserRecipeBoxItem> children) {
        this.categoryName = name;
        this.recipeCards = children;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<UserRecipeBoxItem> getRecipeCards() {
        return recipeCards;
    }

    public void setRecipeCards(List<UserRecipeBoxItem> recipeCards) {
        this.recipeCards = recipeCards;
    }
}
