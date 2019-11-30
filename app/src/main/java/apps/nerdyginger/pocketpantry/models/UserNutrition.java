package apps.nerdyginger.pocketpantry.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userNutrition")
public class UserNutrition {
    @PrimaryKey(autoGenerate = true)
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

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getSugarContent() {
        return sugarContent;
    }

    public void setSugarContent(String sugarContent) {
        this.sugarContent = sugarContent;
    }

    public String getSodiumContent() {
        return sodiumContent;
    }

    public void setSodiumContent(String sodiumContent) {
        this.sodiumContent = sodiumContent;
    }

    public String getFatContent() {
        return fatContent;
    }

    public void setFatContent(String fatContent) {
        this.fatContent = fatContent;
    }

    public String getSaturatedFatContent() {
        return saturatedFatContent;
    }

    public void setSaturatedFatContent(String saturatedFatContent) {
        this.saturatedFatContent = saturatedFatContent;
    }

    public String getTransFatContent() {
        return transFatContent;
    }

    public void setTransFatContent(String transFatContent) {
        this.transFatContent = transFatContent;
    }

    public String getCarbohydrateContent() {
        return carbohydrateContent;
    }

    public void setCarbohydrateContent(String carbohydrateContent) {
        this.carbohydrateContent = carbohydrateContent;
    }

    public String getFiberContent() {
        return fiberContent;
    }

    public void setFiberContent(String fiberContent) {
        this.fiberContent = fiberContent;
    }

    public String getProteinContent() {
        return proteinContent;
    }

    public void setProteinContent(String proteinContent) {
        this.proteinContent = proteinContent;
    }

    public String getCholesterolContent() {
        return cholesterolContent;
    }

    public void setCholesterolContent(String cholesterolContent) {
        this.cholesterolContent = cholesterolContent;
    }
}
