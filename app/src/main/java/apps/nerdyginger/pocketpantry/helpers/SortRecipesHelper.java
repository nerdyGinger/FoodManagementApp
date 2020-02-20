package apps.nerdyginger.pocketpantry.helpers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.dao.RecipeDao;
import apps.nerdyginger.pocketpantry.dao.RecipeItemJoinDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeCategory;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.RecipeItemJoin;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;

// Helper methods for sorting recipes for viewing on the browse page
// Last edited: 2/20/2020
public class SortRecipesHelper {
    private Context context;
    private RecipeDao recipeDao;
    private RecipeItemJoinDao recipeItemJoinDao;
    private UserInventoryItemDao inventoryDao;

    public SortRecipesHelper(Context context) {
        this.context = context;
        recipeDao = new RecipeDao(context);
        recipeItemJoinDao = new RecipeItemJoinDao(context);
        inventoryDao = UserCustomDatabase.getDatabase(context).getUserInventoryDao();
    }

    // Returns a list of the top 20 recipes that the user has all or most of the ingredients
    // for in a displayable format (UserRecipeBoxItem)
    public List<UserRecipeBoxItem> getRecommendedRecipes() {
        List<UserRecipeBoxItem> recommendedItems = new ArrayList<>();

        //get inventory items
        List<UserInventoryItem> inventoryItems = inventoryDao.getAllInventoryItems();

        //foreach itemID, check recipeItemJoin and compare quantities of same items
        //   if valid, add to a list of available ingredients
        for (int i=0; i<inventoryItems.size(); i++) {
            Integer itemID = inventoryItems.get(i).getItemId();
            List<RecipeItemJoin> joinItems = recipeItemJoinDao.getJoinItemsByItem(itemID.toString());
            for (int j=0; j<joinItems.size(); j++) {
                //if (inventoryItems.get(i).getQuantity()) //TODO: create item quantity helper! (some code found in home fragment)
            }

        }

        //foreach recipeID in the list of available ingredients, check if all items in
        //   recipe are available, add to recommended list;
        //   add recipes with one or more missing to appropriate lists until 20 is reached

        return recommendedItems;
    }


}
