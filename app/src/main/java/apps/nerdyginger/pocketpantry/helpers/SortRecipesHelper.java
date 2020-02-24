package apps.nerdyginger.pocketpantry.helpers;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.dao.CategoryDao;
import apps.nerdyginger.pocketpantry.dao.CuisineDao;
import apps.nerdyginger.pocketpantry.dao.ItemDao;
import apps.nerdyginger.pocketpantry.dao.RecipeDao;
import apps.nerdyginger.pocketpantry.dao.RecipeItemJoinDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeCategory;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeItem;
import apps.nerdyginger.pocketpantry.models.Category;
import apps.nerdyginger.pocketpantry.models.Cuisine;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.RecipeItemJoin;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;

// Helper methods for sorting recipes for viewing on the browse page
// Last edited: 2/21/2020
public class SortRecipesHelper {
    private Context context;
    private ItemQuantityHelper quantityHelper;
    private ItemDao itemDao;
    private RecipeDao recipeDao;
    private RecipeItemJoinDao recipeItemJoinDao;
    private UserInventoryItemDao inventoryDao;

    public SortRecipesHelper(Context context) {
        this.context = context;
        quantityHelper = new ItemQuantityHelper(context);
        itemDao = new ItemDao(context);
        recipeDao = new RecipeDao(context);
        recipeItemJoinDao = new RecipeItemJoinDao(context);
        inventoryDao = UserCustomDatabase.getDatabase(context).getUserInventoryDao();
    }

    public List<BrowseRecipeCategory> getRecipesByCuisine() {
        List<BrowseRecipeCategory> recipeCuisines = new ArrayList<>();
        CuisineDao cuisineDao = new CuisineDao(context);
        List<Cuisine> cuisines = cuisineDao.buildCategoryListFromDb();

        for (int i=0; i<cuisines.size(); i++) {
            List<BrowseRecipeItem> browseItems = new ArrayList<>();
            BrowseRecipeCategory cuisineItem = new BrowseRecipeCategory();
            cuisineItem.setCategoryName(cuisines.get(i).getName());
            BrowseRecipeItem item = new BrowseRecipeItem();
            List<Recipe> recipes = recipeDao.getAllRecipesByCuisine(String.valueOf(cuisines.get(i).get_ID()));
            for (int j=0; j<recipes.size(); j++) {
                Recipe recipe = recipes.get(j);
                item.setCategory(cuisines.get(i).getName());
                item.setRecipeId(recipe.get_ID());
                item.setRecipeName(recipe.getName());
                item.setServings(recipe.getRecipeYield());
                item.setUserAdded(false);
                browseItems.add(item);
            }
            if (browseItems.size() > 0) {
                //only add if there is something there
                cuisineItem.setRecipeCards(browseItems);
                recipeCuisines.add(cuisineItem);
            }
        }
        Collections.shuffle(recipeCuisines); //shuffle the list so it's in a different order every time
        return recipeCuisines;
    }

    public List<BrowseRecipeCategory> getRecipesByCategory() {
        List<BrowseRecipeCategory> recipeCategories = new ArrayList<>();
        CategoryDao categoryDao = new CategoryDao(context);
        List<Category> categories = categoryDao.buildCategoryListFromDb();

        for (int i=0; i<categories.size(); i++) {
            List<BrowseRecipeItem> browseItems = new ArrayList<>();
            BrowseRecipeCategory categoryItem = new BrowseRecipeCategory();
            categoryItem.setCategoryName(categories.get(i).getName());
            BrowseRecipeItem item = new BrowseRecipeItem();
            List<Recipe> recipes = recipeDao.getAllRecipesByCategory(String.valueOf(categories.get(i).get_ID()));
            for (int j=0; j<recipes.size(); j++) {
                Recipe recipe = recipes.get(j);
                item.setCategory(categories.get(i).getName());
                item.setRecipeId(recipe.get_ID());
                item.setRecipeName(recipe.getName());
                item.setServings(recipe.getRecipeYield());
                item.setIngredientsMissing(getIngredientsMissing(item));
                item.setUserAdded(false);
                browseItems.add(item);
            }
            if (browseItems.size() > 0) {
                //only add if there is something there
                Collections.sort(browseItems); //sort by missing ingredients
                categoryItem.setRecipeCards(browseItems);
                recipeCategories.add(categoryItem);
            }
        }
        Collections.shuffle(recipeCategories);
        return recipeCategories;
    }

    // Gets the number of missing ingredients for a BrowseRecipeItem
    // Assumes that the recipe is READ-ONLY
    private int getIngredientsMissing(BrowseRecipeItem item) {
        List<RecipeItemJoin> ingredients = recipeItemJoinDao.getJoinItemsInRecipe(String.valueOf(item.getRecipeId()));
        int missing = 0;
        for (int i=0; i<ingredients.size(); i++) {
            RecipeItemJoin ingredient = ingredients.get(i);
            String itemName = itemDao.getItemName(String.valueOf(ingredient.getItemId()));
            if (quantityHelper.itemNameInInventory(itemName)) {
                UserInventoryItem inventoryItem = inventoryDao.getInventoryItemByName(itemName);
                if ( ! quantityHelper.enoughInventoryForIngredient(ingredient, inventoryItem)) {
                    missing++;
                }
            }
        }
        return missing;
    }

    // Returns a list of the top 20 recipes that the user has all or most of the ingredients
    // for in a displayable format
    public List<BrowseRecipeItem> getRecommendedRecipes(List<BrowseRecipeCategory> categories) {
        //recurse through the list of categories until 20 items are in the recommended list
        List<BrowseRecipeItem> recommended = new ArrayList<>();
        int maxMissing = 0;

        while(recommended.size() < 20) {
            for (int i=0; i<categories.size(); i++) {
                List<BrowseRecipeItem> recipes = categories.get(i).getRecipeCards();
                for (int j=0; j<recipes.size(); j++) {
                    if (recipes.get(j).getIngredientsMissing() == maxMissing) {
                        recommended.add(recipes.get(j));
                    }
                    if (recommended.size() >= 20) {
                        break;
                    }
                }
                if (recommended.size() >= 20) {
                    break;
                }
            }
            maxMissing++;
        }
        return recommended;
    }
}
