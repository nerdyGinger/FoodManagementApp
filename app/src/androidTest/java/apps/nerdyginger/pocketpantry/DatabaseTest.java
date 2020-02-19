package apps.nerdyginger.pocketpantry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import apps.nerdyginger.pocketpantry.dao.CategoryDao;
import apps.nerdyginger.pocketpantry.dao.InventoryHelperDao;
import apps.nerdyginger.pocketpantry.dao.ItemDao;
import apps.nerdyginger.pocketpantry.dao.NutritionDao;
import apps.nerdyginger.pocketpantry.dao.RecipeDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.dao.UserItemDao;
import apps.nerdyginger.pocketpantry.dao.UserNutritionDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeDao;
import apps.nerdyginger.pocketpantry.models.Item;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserItem;
import apps.nerdyginger.pocketpantry.models.UserNutrition;
import apps.nerdyginger.pocketpantry.models.UserRecipe;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private SQLiteDatabase db;
    private RecipeDao recipeDao;
    private ItemDao itemDao;
    private NutritionDao nutritionDao;
    private CategoryDao categoryDao;
    private UserItemDao userItemDao;
    private UserRecipeDao userRecipeDao;
    private UserNutritionDao userNutritionDao;
    private UserInventoryItemDao userInventoryDao;

    private Context context;

    @Before
    public void createDb() {
        //food database
        context = ApplicationProvider.getApplicationContext();
        db = new DatabaseHelper(context).getReadableDatabase();
        recipeDao = new RecipeDao(context);
        itemDao = new ItemDao(context);
        nutritionDao = new NutritionDao(context);
        categoryDao = new CategoryDao(context);
        //custom database
        UserCustomDatabase customDb = Room.inMemoryDatabaseBuilder(context, UserCustomDatabase.class).build();
        userItemDao = customDb.getUserItemDao();
        userRecipeDao = customDb.getUserRecipeDao();
        userNutritionDao = customDb.getUserNutritionDao();
        userInventoryDao = customDb.getUserInventoryDao();
    }

    @After
    public void closeDb() throws IOException {
        try {
            db.close();
        } catch (Exception e) {
            Log.e("Something went wrong!", e.toString());
        }
    }

    @Test
    public void rawSQLiteTest() throws Exception {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Item WHERE rowid = ?", new String[] {"1"});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        String output = cursor.getString(cursor.getColumnIndex("name"));
        cursor.close();
        Assert.assertEquals("Pillsbury Golden Layer Buttermilk Biscuits", output);
    }

    @Test
    public void subTablesTest() throws Exception {
        String categoryName = categoryDao.getCategoryName("1");
        String nutritionName = nutritionDao.getNutritionName("1");
        Assert.assertEquals("Dairy", categoryName);
        Assert.assertEquals("", nutritionName);
    }

    @Test
    public void itemDbTest() throws Exception {
        Item item = itemDao.getItemFromId("189");
        String itemName = item.getName();
        String itemId = itemDao.getItemId(itemName);
        Assert.assertEquals("Unsweetened Applesauce", itemName);
        Assert.assertEquals("189", itemId);

    }

    @Test
    public void customItemTest() throws Exception {
        UserItem newItem = new UserItem();
        newItem.setName("Pokeweed");
        userItemDao.insert(newItem);
        UserItem i1 = userItemDao.getItemById(1);
        Assert.assertEquals("Pokeweed", i1.getName());
    }

    @Test
    public void customRecipeTest() throws Exception {
        UserRecipe newRecipe = new UserRecipe();
        newRecipe.setName("Poke Sallet");
        userRecipeDao.insert(newRecipe);
        UserRecipe r1 = userRecipeDao.getUserRecipeById(1);
        Assert.assertEquals("Poke Sallet", r1.getName());
    }

    @Test
    public void customNutritionTest() throws Exception {
        UserNutrition newNut = new UserNutrition();
        newNut.setRecipeName("Poke Sallet");
        userNutritionDao.insert(newNut);
        Assert.assertEquals("Poke Sallet", userNutritionDao.getNutritionRecipeNameById(1));
    }

    @Test
    public void inventoryTest() throws Exception {
        UserItem i1 = new UserItem();
        i1.setName("flour");
        userItemDao.insert(i1);
        UserInventoryItem invItem1 = new UserInventoryItem();
        invItem1.setItemId(i1.get_ID());
        invItem1.setItemName(i1.getName());
        userInventoryDao.insert(invItem1);
        List<Item> readonlyDbItems = itemDao.getAllItems();
        for (int i=0; i<readonlyDbItems.size(); i++) {
            UserInventoryItem temp = new UserInventoryItem();
            temp.setItemId(readonlyDbItems.get(i).get_ID());
            temp.setItemName(readonlyDbItems.get(i).getName());
            userInventoryDao.insert(temp);
        }
        List<UserInventoryItem> inventoryList = userInventoryDao.getAllInventoryItems();
        InventoryHelperDao helperDao = new InventoryHelperDao(context);
        List<String> names = helperDao.getAllItemNames();
        for (int i=0; i<names.size(); i++) {
            Log.e("Item", names.get(i));
        }
        Assert.assertEquals(readonlyDbItems.size() + 1, inventoryList.size());
    }
}
