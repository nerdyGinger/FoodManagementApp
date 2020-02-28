package apps.nerdyginger.pocketpantry.helpers;

import android.content.Context;
import android.util.Log;

import apps.nerdyginger.pocketpantry.Fraction;
import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.dao.UnitConversionDao;
import apps.nerdyginger.pocketpantry.dao.UnitDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.dao.UserItemDao;
import apps.nerdyginger.pocketpantry.models.RecipeItemJoin;
import apps.nerdyginger.pocketpantry.models.Unit;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserItem;
import apps.nerdyginger.pocketpantry.models.UserListItem;
import apps.nerdyginger.pocketpantry.models.UserRecipeItemJoin;

// Helper class with methods for comparing and doing math on item quantities, which
// use fractions and units, making simple math quite complicated. *sigh*
// Last edit: 2/20/2020
public class ItemQuantityHelper {
    private Context context;
    private UnitDao unitDao;
    private UnitConversionDao conversionDao;

    public ItemQuantityHelper(Context context) {
        this.context = context;
        unitDao = new UnitDao(context);
        conversionDao = new UnitConversionDao(context);
    }

    // Get difference of two fraction with the same unit type
    private Fraction getDifference(Fraction a, Unit aUnit, Fraction b, Unit bUnit) {
        if (aUnit == null && bUnit == null ||
                aUnit.getType() == null && bUnit.getType() == null) {
            //both null, but can't check equality of nulls
            return a.subtract(b);
        }
        if ( ! aUnit.getFullName().equals(bUnit.getFullName())) {
            //different units, but same type: convert and subtract
            conversionDao.convertUnitQuantity(
                    /* convert 'a' quantity...*/ a,
                    /*   ...from 'a' unit...  */ String.valueOf(aUnit.get_ID()),
                    /*    ...to 'b' unit...   */ String.valueOf(bUnit.get_ID()));
            return a.subtract(b);
        } else {
            //assuming that the passed in amounts don't have different types
            return a.subtract(b);
        }
    }

    // Get sum of two fractions with the same unit type
    private Fraction getSum(Fraction a, Unit aUnit, Fraction b, Unit bUnit) {
        if (aUnit == null && bUnit == null ||
                aUnit.getType() == null && bUnit.getType() == null) {
            //both null, but can't check equality of nulls
            return a.add(b);
        }
        if ( ! aUnit.getFullName().equals(bUnit.getFullName())) {
            //different units, but same type: convert and add
            conversionDao.convertUnitQuantity(
                    /* convert 'a' quantity...*/ a,
                    /*   ...from 'a' unit...  */ String.valueOf(aUnit.get_ID()),
                    /*    ...to 'b' unit...   */ String.valueOf(bUnit.get_ID()));
            return a.add(b);
        } else {
            //assuming that the passed in amounts don't have different types
            return a.add(b);
        }
    }

    // List dialog sets the list userAdded to appropriate value, and adds new UserItem
    // if item does not exist in either db; this method adds to inventory
    public UserInventoryItem addNewInventoryFromList(UserListItem listItem) {
        UserInventoryItem inventoryItem = new UserInventoryItem();
        inventoryItem.setItemId(listItem.getItemID());
        inventoryItem.setUserAdded(listItem.isUserAdded());
        inventoryItem.setItemName(listItem.getItemName());
        inventoryItem.setQuantity(listItem.getQuantity());
        return inventoryItem;
    }

    public UserInventoryItem addListToInventory(UserListItem listItem, UserInventoryItem inventoryItem) {
        Fraction list = new Fraction().fromString(listItem.getQuantity());
        Fraction inventory = new Fraction().fromString(inventoryItem.getQuantity());
        //TODO: list item doesn't have unit! Keep that way???

        if (inventoryItem.getUnit().equals("")) {
            //no unit, only way we can math with it
            inventoryItem.setQuantity(getSum(inventory, null, list, null).toString());
        }
        return inventoryItem;
    }

    public UserInventoryItem subtractListFromInventory(UserListItem listItem, UserInventoryItem inventoryItem) {
        Fraction list = new Fraction().fromString(listItem.getQuantity());
        Fraction inventory = new Fraction().fromString(inventoryItem.getQuantity());
        //TODO: list item doesn't have unit! Keep that way???

        if (inventoryItem.getUnit().equals("")) {
            //no unit, only way we can math with it
            inventoryItem.setQuantity(getDifference(inventory, null, list, null).toString());
        }
        return inventoryItem;
    }

    public UserInventoryItem subtractUserRecipeIngredient(UserRecipeItemJoin joinItem, UserInventoryItem inventoryItem) {
        Fraction ingredient = new Fraction().fromString(joinItem.quantity);
        Fraction inventory = new Fraction().fromString(inventoryItem.getQuantity());
        Unit ingredientUnit = unitDao.getUnitByAbbrev(joinItem.unit);
        Unit inventoryUnit = unitDao.getUnitByAbbrev(inventoryItem.getUnit());

        if (ingredientUnit.getType() == null && inventoryUnit.getType() == null) {
            //both null, but can't check for equality on nulls
            inventoryItem.setQuantity(getDifference(inventory, inventoryUnit, ingredient, ingredientUnit).toString());
            return inventoryItem;
        } else if ( ! ingredientUnit.getType().equals(inventoryUnit.getType())) {
            //can't subtract different types, so return original value
            return inventoryItem;
        } else {
            inventoryItem.setQuantity(getDifference(inventory, inventoryUnit, ingredient, ingredientUnit).toString());
            return inventoryItem;
        }

    }

    public UserInventoryItem subtractIngredient(RecipeItemJoin joinItem, UserInventoryItem inventoryItem) {
        Fraction ingredient = new Fraction().fromString(joinItem.getQuantity());
        Fraction inventory = new Fraction().fromString(inventoryItem.getQuantity());
        Unit ingredientUnit = unitDao.getUnitByAbbrev(joinItem.getUnit());
        Unit inventoryUnit = unitDao.getUnitByAbbrev(inventoryItem.getUnit());

        if ( ! ingredientUnit.getType().equals(inventoryUnit.getType())) {
            //can't subtract different types, so return original value
            return inventoryItem;
        } else {
            inventoryItem.setQuantity(getDifference(inventory, inventoryUnit, ingredient, ingredientUnit).toString());
            return inventoryItem;
        }

    }

    // Check to see if a string exists as an item name in the inventory.
    // This works because names must be unique, even across the two dbs, and
    // here we don't care what db they're in.
    public boolean itemNameInInventory(final String name) {
        final boolean[] inInventory = {false};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(context);
                UserInventoryItemDao dao = db.getUserInventoryDao();
                int id = dao.getInventoryItemIdByName(name);
                if (id == 0) {
                    inInventory[0] = false;
                } else {
                    inInventory[0] = true;
                }
            } catch (Exception e) {
                //item not found
                Log.e("Database Error", e.toString());
                inInventory[0] = false;
            }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
        return inInventory[0];
    }

    // Checks if the recipe ingredient item quantity <= inventory item quantity.
    // Only accepts READ-ONLY recipe ingredient items because (so far) I'm only planning
    // on sorting the read-only recipes.
    public boolean enoughInventoryForIngredient(RecipeItemJoin joinItem, UserInventoryItem inventoryItem) {
        Fraction ingredient = new Fraction().fromString(joinItem.getQuantity());
        Fraction inventory = new Fraction().fromString(inventoryItem.getQuantity());
        Unit ingredientUnit = unitDao.getUnitByAbbrev(joinItem.getUnit());
        Unit inventoryUnit = unitDao.getUnitByAbbrev(inventoryItem.getUnit());

        if ( ! ingredientUnit.getType().equals(inventoryUnit.getType())) {
            //can't compare amounts of different types, so assume not
            return false;
        } else if (ingredientUnit.getFullName().equals(inventoryUnit.getFullName())) {
            //same unit (or both have no unit)
            return inventory.isGreaterThanOrEqualTo(ingredient);
        } else {
            //different units, same type
            conversionDao.convertUnitQuantity(
                    /* convert 'inventory' quantity...*/ inventory,
                    /*   ...from 'inventory' unit...  */ String.valueOf(inventoryUnit.get_ID()),
                    /*    ...to 'ingredient' unit...   */ String.valueOf(ingredientUnit.get_ID()));
            return inventory.isGreaterThanOrEqualTo(ingredient);
        }
    }

}
