package apps.nerdyginger.pocketpantry.models;

import java.util.List;

public class BrowseRecipeCategory {
    private String categoryName;
    private List<BrowseRecipeItem> recipeCards;

    public BrowseRecipeCategory() {
        //empty constructor
    }

    public BrowseRecipeCategory(String name, List<BrowseRecipeItem> children) {
        this.categoryName = name;
        this.recipeCards = children;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<BrowseRecipeItem> getRecipeCards() {
        return recipeCards;
    }

    public void setRecipeCards(List<BrowseRecipeItem> recipeCards) {
        this.recipeCards = recipeCards;
    }
}
