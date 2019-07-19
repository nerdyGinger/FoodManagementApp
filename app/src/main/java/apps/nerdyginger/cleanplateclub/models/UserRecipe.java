package apps.nerdyginger.cleanplateclub.models;

import android.content.Context;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.UserCustomDatabase;
import apps.nerdyginger.cleanplateclub.dao.UserItemDao;

@Entity(tableName = "userRecipes")
public class UserRecipe {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    private String name;
    private String author;
    private String datePublished;
    private String description;
    private String totalTime;
    private String keywords;
    private String recipeYield;
    private String recipeCategory;
    private String recipeCuisine;
    private int nutritionId;
    private String recipeIngredient;
    private String recipeInstructions;

    @Ignore
    private UserCustomDatabase database;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int id) {
        this._ID = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getRecipeYield() {
        return recipeYield;
    }

    public void setRecipeYield(String recipeYield) {
        this.recipeYield = recipeYield;
    }

    public String getRecipeCategory() {
        return recipeCategory;
    }

    public void setRecipeCategory(String recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    public String getRecipeCuisine() {
        return recipeCuisine;
    }

    public void setRecipeCuisine(String recipeCuisine) {
        this.recipeCuisine = recipeCuisine;
    }

    public int getNutritionId() {
        return nutritionId;
    }

    public void setNutritionId(int nutritionId) {
        this.nutritionId = nutritionId;
    }

    public String getRecipeIngredient() {
        return recipeIngredient;
    }

    public void setRecipeIngredient(String recipeIngredient) {
        this.recipeIngredient = recipeIngredient;
    }

    public String getRecipeInstructions() {
        return recipeInstructions;
    }

    public void setRecipeInstructions(String recipeInstructions) {
        this.recipeInstructions = recipeInstructions;
    }

    public String[] getKeywordsList() {
        String stripped = keywords.substring(1, keywords.length() - 1);
        return stripped.split(",");
    }
}
