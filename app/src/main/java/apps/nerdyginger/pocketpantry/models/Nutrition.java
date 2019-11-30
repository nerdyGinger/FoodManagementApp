package apps.nerdyginger.pocketpantry.models;

public class Nutrition {
    private int _ID;
    private String recipeName;
    private String servingSize;
    private String calories;
    private String sugarContent;
    private String sodiumContent;
    private String fatContent;
    private String saturatedFatContent;
    private String transFatContent;
    private String carbohydrateContent;
    private String fiberContent;
    private String proteinContent;
    private String cholesterolContent;

    public int get_ID() {
        return _ID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getServingSize() {
        return servingSize;
    }

    public String getCalories() {
        return calories;
    }

    public String getSugarContent() {
        return sugarContent;
    }

    public String getSodiumContent() {
        return sodiumContent;
    }

    public String getFatContent() {
        return fatContent;
    }

    public String getSaturatedFatContent() {
        return saturatedFatContent;
    }

    public String getTransFatContent() {
        return transFatContent;
    }

    public String getCarbohydrateContent() {
        return carbohydrateContent;
    }

    public String getFiberContent() {
        return fiberContent;
    }

    public String getProteinContent() {
        return proteinContent;
    }

    public String getCholesterolContent() {
        return cholesterolContent;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public void setSugarContent(String sugarContent) {
        this.sugarContent = sugarContent;
    }

    public void setSodiumContent(String sodiumContent) {
        this.sodiumContent = sodiumContent;
    }

    public void setFatContent(String fatContent) {
        this.fatContent = fatContent;
    }

    public void setSaturatedFatContent(String saturatedFatContent) {
        this.saturatedFatContent = saturatedFatContent;
    }

    public void setTransFatContent(String transFatContent) {
        this.transFatContent = transFatContent;
    }

    public void setCarbohydrateContent(String carbohydrateContent) {
        this.carbohydrateContent = carbohydrateContent;
    }

    public void setFiberContent(String fiberContent) {
        this.fiberContent = fiberContent;
    }

    public void setProteinContent(String proteinContent) {
        this.proteinContent = proteinContent;
    }

    public void setCholesterolContent(String cholesterolContent) {
        this.cholesterolContent = cholesterolContent;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public Nutrition(int _ID, String recipeName, String servingSize, String calories, String sugarContent,
                     String sodiumContent, String fatContent, String saturatedFatContent,
                     String transFatContent, String carbohydrateContent, String fiberContent,
                     String proteinContent, String cholesterolContent) {
        this._ID = _ID;
        this.recipeName = recipeName;
        this.calories = calories;
        this.servingSize = servingSize;
        this.sugarContent = sugarContent;
        this.sodiumContent = sodiumContent;
        this.fatContent = fatContent;
        this.saturatedFatContent = saturatedFatContent;
        this.transFatContent = transFatContent;
        this.carbohydrateContent = carbohydrateContent;
        this.fiberContent = fiberContent;
        this.proteinContent = proteinContent;
        this.cholesterolContent = cholesterolContent;
    }
}
