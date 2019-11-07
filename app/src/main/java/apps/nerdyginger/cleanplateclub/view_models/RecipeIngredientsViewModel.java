package apps.nerdyginger.cleanplateclub.view_models;

import apps.nerdyginger.cleanplateclub.adapters.RecipeIngredientsAdapter;

public class RecipeIngredientsViewModel {
    private String itemName;
    private String detail;
    private String amount;
    private String unit;
    private boolean isExpanded;

    public RecipeIngredientsViewModel() {
        //empty constructor
    }

    public RecipeIngredientsViewModel(boolean setExpanded, String itemName, String detail, String amount, String unit) {
        isExpanded = setExpanded;
        this.itemName = itemName;
        this.detail = detail;
        this.amount = amount;
        this.unit = unit;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
