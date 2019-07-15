package apps.nerdyginger.cleanplateclub.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.dao.ItemDao;

public class Recipe {
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

    public int get_ID() {
        return _ID;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public String getDescription() {
        return description;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getRecipeYield() {
        return recipeYield;
    }

    public String getRecipeCategory() {
        return recipeCategory;
    }

    public String getRecipeCuisine() {
        return recipeCuisine;
    }

    public int getNutritionId() {
        return nutritionId;
    }

    public String getRecipeIngredient() {
        return recipeIngredient;
    }

    public String getRecipeInstructions() {
        return recipeInstructions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setRecipeYield(String recipeYield) {
        this.recipeYield = recipeYield;
    }

    public void setRecipeCategory(String recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    public void setRecipeCuisine(String recipeCuisine) {
        this.recipeCuisine = recipeCuisine;
    }

    public void setNutritionId(int nutritionId) {
        this.nutritionId = nutritionId;
    }

    public void setRecipeIngredients(String recipeIngredient) {
        this.recipeIngredient = recipeIngredient;
    }

    public void setRecipeInstructions(String recipeInstructions) {
        this.recipeInstructions = recipeInstructions;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public Recipe(int _ID, String name, String author, String datePublished, String description,
                  String totalTime, String keywords, String recipeYield, String recipeCategory,
                  String recipeCuisine, int nutritionId, String recipeIngredient,
                  String recipeInstructions) {
        this._ID = _ID;
        this.name = name;
        this.author = author;
        this.datePublished = datePublished;
        this.description = description;
        this.totalTime = totalTime;
        this.keywords = keywords;
        this.recipeYield = recipeYield;
        this.recipeCategory = recipeCategory;
        this.recipeCuisine = recipeCuisine;
        this.nutritionId = nutritionId;
        this.recipeIngredient = recipeIngredient;
        this.recipeInstructions = recipeInstructions;

    }

    public String[] getKeywordsList() {
        String stripped = keywords.substring(1, keywords.length() - 1);
        return stripped.split(",");
    }

    public List<String> getIngredientsList(Context context) {
        String stripped = recipeIngredient.substring(1, recipeIngredient.length() - 1);
        String[] split = stripped.split(";;;");
        List<String> parsed = new ArrayList<>();
        for (int i=0; i<split.length-1; i++) {
            String[] possiblePair = split[i].split("\\^-\\^");
            if (possiblePair.length == 1) { //this ingredient is a singlet
                parsed.add(possiblePair[0]);
            } else { //this ingredient is a quantity/item pair
                String actualPair = possiblePair[0];
                ItemDao iDao = new ItemDao(context);
                String fixedId = possiblePair[1].replace("[(", "").replace(",)]", "");
                actualPair += " " + iDao.getItemName(fixedId);
                parsed.add(actualPair);
            }
        }
        return parsed;
    }

    public String[] getInstructionsList() {
        String stripped = recipeInstructions.substring(1, recipeInstructions.length() - 1);
        String[] split = stripped.split("', '");
        int len = split.length;
        split[0] = split[0].substring(1);              //trim off trailing quotes
        split[len-1] = split[len-1].substring(0, split[len-1].length() - 1);
        return split;
    }
}
