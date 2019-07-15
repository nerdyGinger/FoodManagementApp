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

    public List<String> getIngredientsList(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, UserCustomDatabase.class, "userCustomDb")
                    .allowMainThreadQueries()
                    .build();
        }
        String stripped = recipeIngredient.substring(1, recipeIngredient.length() - 1);
        String[] split = stripped.split(";;;");
        List<String> parsed = new ArrayList<>();
        try {
            for (int i = 0; i < split.length - 1; i++) {
                String[] possiblePair = split[i].split("\\^-\\^");
                Log.e("INSIDE CLASS", possiblePair[0]);
                if (possiblePair.length == 1) { //this ingredient is a singlet
                    parsed.add(possiblePair[0]);
                } else { //this ingredient is a quantity/item pair
                    String actualPair = possiblePair[0];
                    UserItemDao iDao = database.getUserItemDao();
                    actualPair += " " + iDao.getItemById(Integer.parseInt(possiblePair[1]));
                    parsed.add(actualPair);
                }
            }
        } catch (Exception e) {
            Log.e("Item parsing error", e.toString());
        }
        return parsed;
    }

    public String[] getInstructionsList() {
        String stripped = recipeInstructions.substring(1, recipeInstructions.length() - 1);
        String[] split = stripped.split("', '");
        int len = split.length;
        try {
            split[0] = split[0].substring(1); //trim off trailing quotes
            split[len - 1] = split[len - 1].substring(0, split[len - 1].length() - 1);
        } catch (Exception e) {
            Log.e("Instruction parse error", e.toString());
        }
        return split;
    }
}
