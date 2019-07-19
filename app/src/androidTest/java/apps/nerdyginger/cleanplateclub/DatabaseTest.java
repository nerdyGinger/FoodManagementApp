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
import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.NutritionDao;
import apps.nerdyginger.cleanplateclub.dao.RecipeDao;
import apps.nerdyginger.cleanplateclub.dao.UserItemDao;
import apps.nerdyginger.cleanplateclub.dao.UserNutritionDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeDao;
import apps.nerdyginger.cleanplateclub.models.Recipe;
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
    public void superTablesTest() throws Exception {
        String recipeName = recipeDao.getRecipeName("1");
        String recipeId = recipeDao.getRecipeId(recipeName);
        String itemName = itemDao.getItemName("1");
        String itemId = itemDao.getItemId(itemName);
        Assert.assertEquals("Whole Milk", itemName);
        Assert.assertEquals("1", itemId);
        Assert.assertEquals("Pioneer Woman's Glazed Donuts", recipeName);
        Assert.assertEquals("1", recipeId);

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
        newRecipe.setRecipeIngredient("[1 cup^-^1;;;another ingredient]");
        userRecipeDao.insert(newRecipe);
        UserRecipe r1 = userRecipeDao.getUserRecipeById(1);
        Assert.assertEquals("Poke Sallet", r1.getName());
        List<String> ingredients = r1.getIngredientsList(context);
        Assert.assertEquals(1, ingredients.size());
    }

    @Test
    public void customNutritionTest() throws Exception {
        UserNutrition newNut = new UserNutrition();
        newNut.setRecipeName("Poke Sallet");
        userNutritionDao.insert(newNut);
        Assert.assertEquals("Poke Sallet", userNutritionDao.getNutritionRecipeNameById(1));
    }
}
