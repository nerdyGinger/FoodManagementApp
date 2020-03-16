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
import java.util.ArrayList;
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
import apps.nerdyginger.pocketpantry.helpers.DatabaseHelper;
import apps.nerdyginger.pocketpantry.models.Item;
import apps.nerdyginger.pocketpantry.models.Recipe;
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
        new ItemDao(context).getAllItems();
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();

        String sql = "Select rowid,* from Recipes where recipeCategory = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {"18"}); //category=Desserts
        String name, author, datePublished, description, totalTime, recipeYield, recipeCuisine;
        String url, recipeBookId, imageUrl;
        ArrayList<String> keywords = new ArrayList<>();
        ArrayList<String> recipeInstructions = new ArrayList<>();
        int nutritionId, id;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        while ( ! cursor.isAfterLast()) {
            id = cursor.getInt(cursor.getColumnIndex("rowid"));
            name = cursor.getString(cursor.getColumnIndex("name"));
            author = cursor.getString(cursor.getColumnIndex("author"));
            url = cursor.getString(cursor.getColumnIndex("url"));
            recipeBookId = cursor.getString(cursor.getColumnIndex("recipeBookId"));
            datePublished = cursor.getString(cursor.getColumnIndex("datePublished"));
            description = cursor.getString(cursor.getColumnIndex("description"));
            totalTime = cursor.getString(cursor.getColumnIndex("totalTime"));
            //Log.e("DEBUG", "[" + '"' + cursor.getString(cursor.getColumnIndex("keywords")) + '"' + "]");
            keywords = Converters.fromString("[" + '"' + cursor.getString(cursor.getColumnIndex("keywords")) + '"' + "]");
            recipeYield = cursor.getString(cursor.getColumnIndex("recipeYield"));
            recipeCuisine = cursor.getString(cursor.getColumnIndex("recipeCuisine"));
            nutritionId = cursor.getInt(cursor.getColumnIndex("nutrition"));
            imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
            Log.e("DEBEG", cursor.getString(cursor.getColumnIndex("recipeInstructions")));
            recipeInstructions = Converters.fromString(cursor.getString(cursor.getColumnIndex("recipeInstructions")));
            //recipes.add(new Recipe(id, name, author, url, recipeBookId, datePublished, description, totalTime, keywords, recipeYield, categoryId,
            //        recipeCuisine, imageUrl, nutritionId, recipeInstructions));
            cursor.moveToNext();
        }

        /*
        Cursor cursor = db.rawQuery("SELECT * FROM Item WHERE rowid = ?", new String[] {"1"});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        String output = cursor.getString(cursor.getColumnIndex("name"));
        cursor.close();
        Assert.assertEquals("Pillsbury Golden Layer Buttermilk Biscuits", output);

         */
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
