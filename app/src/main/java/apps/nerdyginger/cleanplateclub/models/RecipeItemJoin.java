package apps.nerdyginger.cleanplateclub.models;

public class RecipeItemJoin {
    private int recipeId;
    private int itemId;

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public RecipeItemJoin(int recipeId, int itemId) {
        this.recipeId = recipeId;
        this.itemId = itemId;
    }
}
