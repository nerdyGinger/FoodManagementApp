package apps.nerdyginger.pocketpantry.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.RecipeBookActivity;
import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.dao.CategoryDao;
import apps.nerdyginger.pocketpantry.dao.CuisineDao;
import apps.nerdyginger.pocketpantry.dao.ItemDao;
import apps.nerdyginger.pocketpantry.dao.RecipeBookDao;
import apps.nerdyginger.pocketpantry.dao.RecipeDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeBoxDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeItemJoinDao;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.RecipeBook;
import apps.nerdyginger.pocketpantry.models.UserRecipe;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;
import apps.nerdyginger.pocketpantry.models.UserRecipeItemJoin;

/*
 * The recipe dialog is so monstrous that it merits its own helper class just to make
 * it a little more readable.
 * Last edited: 3/19/20
 */
public class RecipeDialogHelper {
    private Context context;

    public RecipeDialogHelper(Context context) {
        this.context = context;
    }

    // Sets page 1 of recipe dialog with recipe data using the VIEW page layout
    // For READ-ONLY recipes only
    public void setPage1ViewMode(View view, Recipe recipe) {
        //get all of the views
        TextView recipeBookName = view.findViewById(R.id.bookContainerName);
        TextView recipeBookAuthor = view.findViewById(R.id.bookContainerAuthor);
        TextView recipeBookDate = view.findViewById(R.id.bookContainerDate);
        TextView recipeName = view.findViewById(R.id.viewRecipeName);
        TextView recipeServings = view.findViewById(R.id.viewRecipeServings);
        TextView recipeTime = view.findViewById(R.id.viewRecipeTime);
        TextView recipeCategory = view.findViewById(R.id.viewRecipeCategory);
        TextView recipeCuisine = view.findViewById(R.id.viewRecipeCuisine);
        TextView recipeUrl = view.findViewById(R.id.viewRecipeUrl);
        ImageView recipeImage = view.findViewById(R.id.viewRecipeImage);
        TextInputEditText recipeDescription = view.findViewById(R.id.viewRecipeDescription);

        //set the easy ones
        recipeName.setText(recipe.getName());
        recipeServings.setText(recipe.getRecipeYield());
        recipeTime.setText(recipe.getTotalTime());
        recipeCategory.setText(getCategory(recipe.getRecipeCategory()));
        recipeCuisine.setText(getCuisine(recipe.getRecipeCuisine()));
        recipeUrl.setText(recipe.getUrl());
        recipeDescription.setText(recipe.getDescription());
        recipeDescription.setEnabled(false);

        //set the ones in the book header
        final RecipeBook book = getRecipeBook(recipe);
        recipeBookName.setText(book.getName());
        recipeBookAuthor.setText(recipe.getAuthor());
        recipeBookDate.setText(recipe.getDatePublished());

        //set the image, with some help from the image helper
        ImageHelper imageHelper = new ImageHelper(context);
        recipeImage.setImageBitmap(imageHelper.retrieveImage(imageHelper.getFilename(book, recipe)));

        //set header click to open recipe book page
        RelativeLayout header = view.findViewById(R.id.viewRecipeHeader);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), RecipeBookActivity.class);
                intent.putExtra("RecipeBookId", book.get_ID());
                context.startActivity(intent);
            }
        });
    }

    public String getCuisine(String cuisineId) {
        CuisineDao dao = new CuisineDao(context);
        return dao.getCuisineName(cuisineId);
    }

    public String getCategory(String categoryId) {
        CategoryDao dao = new CategoryDao(context);
        return dao.getCategoryName(categoryId);
    }

    public UserRecipe getCustomItem(final UserRecipeBoxItem existingBoxItem) {
        final UserRecipe[] recipe = {new UserRecipe()};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(context);
                    UserRecipeDao dao = db.getUserRecipeDao();
                    recipe[0] = dao.getUserRecipeById(existingBoxItem.getRecipeId());
                } catch (Exception e) {
                    Log.e("Database Error", "Error accessing custom recipe item: " + e.toString());
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
        return recipe[0];
    }

    public Recipe getReadOnlyItem(final UserRecipeBoxItem existingBoxItem) {
        final Recipe[] recipe = {null};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RecipeDao dao = new RecipeDao(context);
                    recipe[0] = dao.buildRecipeFromId(String.valueOf(existingBoxItem.getRecipeId()));
                } catch (Exception e) {
                    Log.e("Database Error", "Error accessing read-only recipe item: " + e.toString() + Arrays.toString(e.getStackTrace()));
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
        return recipe[0];
    }

    public RecipeBook getRecipeBook(final Recipe recipe) {
        final RecipeBook[] recipeBook = {null};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RecipeBookDao bookDao = new RecipeBookDao(context);
                    recipeBook[0] = bookDao.getRecipeBookById(recipe.getRecipeBookId());
                } catch (Exception e) {
                    Log.e("Database Error", "Error accessing recipe book: " + e.toString());
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
        return recipeBook[0];
    }

    // Checks a recipe to see if it is a read-only that is already in the recipe box
    public boolean recipeInBox(final String recipeName) {
        final boolean[] present = {false};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserRecipeBoxDao boxDao = UserCustomDatabase.getDatabase(context).getUserRecipeBoxDao();
                int itemId = boxDao.getRecipeIdByName(recipeName);
                if (itemId != 0) {
                    present[0] = true;
                }
            }
        });
        t.start();
        try {
            t.join();
            return present[0];
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
            return present[0];
        }
    }

    public void addToRecipeBox(final UserRecipeBoxItem boxItem) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserRecipeBoxDao boxDao = UserCustomDatabase.getDatabase(context).getUserRecipeBoxDao();
                if ( ! boxItem.isUserAdded() && ! recipeInBox(boxItem.getRecipeName())) { //just double-checking
                    boxDao.insert(boxItem);
                }
            }
        }).start();
    }

    // Updates db for custom recipe, returns boolean indicating success of operation
    public boolean updateCustomRecipe(final UserRecipe newRecipe, final UserRecipeBoxItem boxItem) {
        final boolean[] successful = {false};
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(context);
                    UserRecipeDao dao = db.getUserRecipeDao();
                    UserRecipeBoxDao recipeBoxDao = db.getUserRecipeBoxDao();
                    if (boxItem.isUserAdded()) { //double check that it's really a custom recipe
                        newRecipe.set_ID(boxItem.getRecipeId());
                        dao.update(newRecipe);
                        boxItem.setRecipeName(newRecipe.getName());
                        boxItem.setCategory(newRecipe.getRecipeCategory());
                        boxItem.setServings(newRecipe.getRecipeYield());
                        recipeBoxDao.update(boxItem);
                        successful[0] = true;
                    }
                }
            });
            t.start();
        } catch (Exception e) {
            Toast.makeText(context, "An error occurred - data may not have been saved", Toast.LENGTH_SHORT).show();
            Log.e("UserRecipe DB Error", e.toString());
        }
        return successful[0];
    }

    public void updateIngredients(final ArrayList<UserRecipeItemJoin> ingredientsList,
                                  final int recipeId) {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(context);
                    UserRecipeItemJoinDao joinDao = db.getUserRecipeItemJoinDao();
                    List<UserRecipeItemJoin> oldIngredients = joinDao.getJoinItemsInRecipe(recipeId);

                    //delete old ingredient join items
                    for (int i=0; i<oldIngredients.size(); i++) {
                        joinDao.delete(oldIngredients.get(i));
                    }

                    //add new ingredient join items
                    for (int i=0; i<ingredientsList.size(); i++) {
                        if (ingredientsList.get(i).itemId == -1) {
                            ingredientsList.get(i).itemId = 0;
                        }
                        ingredientsList.get(i).recipeId = recipeId;
                        joinDao.insert(ingredientsList.get(i));
                    }
                }
            });
            t.start();
        } catch (Exception e) {
            Toast.makeText(context, "An error occurred - data may not have been saved", Toast.LENGTH_SHORT).show();
            Log.e("Ingredient DB Error", e.toString());
        }
    }

    public void performInsertOperations(final UserRecipe newRecipe, final List<UserRecipeItemJoin> ingredientsList){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //insert UserRecipe
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(context);
                    UserRecipeDao dao = db.getUserRecipeDao();
                    UserRecipeItemJoinDao itemsDao = db.getUserRecipeItemJoinDao();
                    UserRecipeBoxDao recipeBoxDao = db.getUserRecipeBoxDao();
                    long[] id = new long[1];
                    try {
                        newRecipe.set_ID(dao.getAllUserRecipes().size() + 1);
                        id = dao.insert(newRecipe);
                    } catch (Exception e){
                        Log.e("Nope! Didn't work!", "Recipe _ID: " + newRecipe.get_ID() + " exists for Recipe: " + dao.getUserRecipeById(newRecipe.get_ID()).getName());
                        newRecipe.set_ID(newRecipe.get_ID() + 1);
                        id[0] = (long) newRecipe.get_ID();
                    }

                    //insert UserRecipeBoxItem
                    UserRecipeBoxItem boxItem = new UserRecipeBoxItem();
                    boxItem.setUserAdded(true);
                    boxItem.setRecipeId((int) id[0]);
                    boxItem.setRecipeName(newRecipe.getName());
                    boxItem.setCategory(newRecipe.getRecipeCategory());
                    boxItem.setServings(newRecipe.getRecipeYield());
                    recipeBoxDao.insert(boxItem);

                    //add ingredients to join table
                    for (int i = 0; i < ingredientsList.size(); i++) {
                        if (ingredientsList.get(i).itemId == -1) {
                            ingredientsList.get(i).itemId = 0;
                        }
                        ingredientsList.get(i).recipeId = (int) id[0];
                        itemsDao.insert(ingredientsList.get(i));
                    }
                } catch (Exception e) {
                    Toast.makeText(context, context.getString(R.string.database_unknown_error), Toast.LENGTH_SHORT).show();
                    Log.e("Database Error", e.toString());
                }
            }
        }).start();
    }

    public int getItemId(final String name) {
        final int[] id = new int[1];
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(context);
                    UserInventoryItemDao dao = db.getUserInventoryDao();
                    id[0] = dao.getInventoryItemIdByName(name);
                    if (id[0] == 0) {
                        //not in inventory, check read-only db
                        Log.e("INVENTORY", "'" + name + "' was not found in inventory, searching read-only db");
                        try {
                            ItemDao itemDao = new ItemDao(context);
                            String stringId =  itemDao.getItemId(name);
                            id[0] = Integer.parseInt(stringId);
                        } catch (Exception e2) {
                            //item doesn't exist in either db, set id to -1
                            Log.e("INVENTORY", "'" + name + "' was not found in read-only db, setting item id to -1");
                            id[0] = -1;
                        }
                    } else {
                        Log.e("INVENTORY", "'" + name + "' was found in inventory! Item id = " + id[0]);
                    }
                } catch (Exception e) {
                    Log.e("Database error", e.toString());
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
        return id[0];
    }
}
