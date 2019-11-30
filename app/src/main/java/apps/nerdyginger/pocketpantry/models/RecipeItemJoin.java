package apps.nerdyginger.pocketpantry.models;

public class RecipeItemJoin {
    private int recipeId;
    private int itemId;
    private String quantity;
    private String unit;
    private String detail;

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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public RecipeItemJoin(int recipeId, int itemId, String quantity, String unit, String detail) {
        this.recipeId = recipeId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unit = unit;
        this.detail = detail;
    }
}
