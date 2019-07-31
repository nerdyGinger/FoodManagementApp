package apps.nerdyginger.cleanplateclub;

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

import apps.nerdyginger.cleanplateclub.dao.AllergyDao;
import apps.nerdyginger.cleanplateclub.dao.CategoryDao;
import apps.nerdyginger.cleanplateclub.dao.FlavorDao;
import apps.nerdyginger.cleanplateclub.dao.InventoryHelperDao;
import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.NutritionDao;
import apps.nerdyginger.cleanplateclub.dao.RecipeDao;
import apps.nerdyginger.cleanplateclub.dao.UserInventoryDao;
import apps.nerdyginger.cleanplateclub.dao.UserItemDao;
import apps.nerdyginger.cleanplateclub.dao.UserNutritionDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeDao;
import apps.nerdyginger.cleanplateclub.models.Item;
import apps.nerdyginger.cleanplateclub.models.Recipe;
import apps.nerdyginger.cleanplateclub.models.UserInventory;
import apps.nerdyginger.cleanplateclub.models.UserItem;
import apps.nerdyginger.cleanplateclub.models.UserNutrition;
import apps.nerdyginger.cleanplateclub.models.UserRecipe;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private SQLiteDatabase db;
    private RecipeDao recipeDao;
    private ItemDao itemDao;
    private NutritionDao nutritionDao;
    private FlavorDao flavorDao;
    private CategoryDao categoryDao;
    private AllergyDao allergyDao;
    private UserItemDao userItemDao;
    private UserRecipeDao userRecipeDao;
    private UserNutritionDao userNutritionDao;
    private UserInventoryDao userInventoryDao;

    private Context context;

    @Before
    public void createDb() {
        //food database
        context = ApplicationProvider.getApplicationContext();
        db = new DatabaseHelper(context).getReadableDatabase();
        recipeDao = new RecipeDao(context);
        itemDao = new ItemDao(context);
        nutritionDao = new NutritionDao(context);
        flavorDao = new FlavorDao(context);
        categoryDao = new CategoryDao(context);
        allergyDao = new AllergyDao(context);
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
        Cursor cursor = db.rawQuery("SELECT * FROM Allergy WHERE _ID = ?", new String[] {"1"});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        String output = cursor.getString(cursor.getColumnIndex("name"));
        cursor.close();
        Assert.assertEquals("Peanuts", output);
    }

    @Test
    public void subTablesTest() throws Exception {
        String flavorName = flavorDao.getFlavorName("1");
        String categoryName = categoryDao.getCategoryName("1");
        String allergyName = allergyDao.getAllergyName("1");
        String nutritionName = nutritionDao.getNutritionName("1");
        Assert.assertEquals("Sweet", flavorName);
        Assert.assertEquals("Dairy", categoryName);
        Assert.assertEquals("Peanuts", allergyName);
        Assert.assertEquals("Pioneer Woman's Glazed Donuts", nutritionName);
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
        UserInventory invItem1 = new UserInventory();
        invItem1.setItemId(i1.get_ID());
        invItem1.setItemName(i1.getName());
        userInventoryDao.insert(invItem1);
        List<Item> readonlyDbItems = itemDao.getAllItems();
        for (int i=0; i<readonlyDbItems.size(); i++) {
            UserInventory temp = new UserInventory();
            temp.setItemId(readonlyDbItems.get(i).get_ID());
            temp.setItemName(readonlyDbItems.get(i).getName());
            userInventoryDao.insert(temp);
        }
        List<UserInventory> inventoryList = userInventoryDao.getAllInventoryItems();
        InventoryHelperDao helperDao = new InventoryHelperDao(context);
        List<String> names = helperDao.getAllItemNames();
        for (int i=0; i<names.size(); i++) {
            Log.e("Item", names.get(i));
        }
        Assert.assertEquals(readonlyDbItems.size() + 1, inventoryList.size());
    }
}
