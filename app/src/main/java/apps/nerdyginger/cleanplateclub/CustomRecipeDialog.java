package apps.nerdyginger.cleanplateclub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import apps.nerdyginger.cleanplateclub.adapters.RecipeIngredientsAdapter;
import apps.nerdyginger.cleanplateclub.adapters.RecipeInstructionsAdapter;
import apps.nerdyginger.cleanplateclub.dao.CategoryDao;
import apps.nerdyginger.cleanplateclub.dao.CuisineDao;
import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.RecipeDao;
import apps.nerdyginger.cleanplateclub.dao.RecipeItemJoinDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.dao.UserInventoryItemDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeBoxDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeItemJoinDao;
import apps.nerdyginger.cleanplateclub.models.Recipe;
import apps.nerdyginger.cleanplateclub.models.RecipeItemJoin;
import apps.nerdyginger.cleanplateclub.models.UserInventoryItem;
import apps.nerdyginger.cleanplateclub.models.UserRecipe;
import apps.nerdyginger.cleanplateclub.models.UserRecipeBoxItem;
import apps.nerdyginger.cleanplateclub.models.UserRecipeItemJoin;
import apps.nerdyginger.cleanplateclub.view_models.RecipeIngredientsViewModel;
import apps.nerdyginger.cleanplateclub.view_models.RecipeInstructionsViewModel;

/*
 * This is the dialog for custom recipe input. It must be beautiful, elegant, and absolutely dreamy.
 *
 * ... Okay, so the _dialog_ has to be beautiful, elegant, and absolutely dreamy. Apparently I'm willing
 * to sacrifice the beauty of this class for that cause, because no 900+ LoC class can be called dreamy.
 *
 * Last Edited: 11/12/19
 */
public class CustomRecipeDialog extends DialogFragment {
    private static final int pages = 3; // Slide-able pages for basic info, ingredients, and instructions
    private ViewPager pager;
    private Button nextBtn;
    private static UserRecipe newRecipe = new UserRecipe();
    private static final ArrayList<UserRecipeItemJoin> ingredientsList = new ArrayList<>();
    private static final ArrayList<String> keywordsList = new ArrayList<>();
    private static RecipeInstructionsAdapter instructionsAdapter = new RecipeInstructionsAdapter();
    private static RecipeIngredientsAdapter ingredientsAdapter = new RecipeIngredientsAdapter();

    //enable reuse for viewing/editing recipes
    //   "create"   ---   new custom recipe
    //   "view"     ---   view existing recipe (not editable)
    //   "edit"     ---   edit existing recipe
    private String MODE;
    private static UserRecipeBoxItem existingBoxItem;

    //for edit mode on read-only recipe
    private static Recipe readOnlyItem;

    //for edit mode on custom recipe
    private static UserRecipe existingCustomRecipeItem;

    //empty constructor, default "create" mode
    public CustomRecipeDialog() {
        MODE = "create";
    }

    //constructor to set mode, pass in existing item
    CustomRecipeDialog(String mode, UserRecipeBoxItem item) {
        MODE = mode;
        existingBoxItem = item;
        newRecipe = new UserRecipe();
        if ( ! MODE.equals("create") && ! existingBoxItem.isUserAdded()) {
            //view/edit mode, read-only item; get from read-only db
            getReadOnlyItem();
        } else if ( ! MODE.equals("create")) {
            //view/edit mode, is custom; get from custom db, set newRecipe value to existing values
            getCustomItem();
            newRecipe.setName(existingCustomRecipeItem.getName());
            newRecipe.setAuthor(existingCustomRecipeItem.getAuthor());
            newRecipe.setDescription(existingCustomRecipeItem.getDescription());
            newRecipe.setTotalTime(existingCustomRecipeItem.getTotalTime());
            newRecipe.setRecipeYield(existingCustomRecipeItem.getRecipeYield());
            newRecipe.setKeywords(existingCustomRecipeItem.getKeywords());
            newRecipe.setRecipeCategory(existingCustomRecipeItem.getRecipeCategory());
            newRecipe.setRecipeCuisine(existingCustomRecipeItem.getRecipeCuisine());
            newRecipe.setRecipeInstructions(existingCustomRecipeItem.getRecipeInstructions());
        }
    }

    private void getCustomItem() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                    UserRecipeDao dao = db.getUserRecipeDao();
                    Log.e("DEBUG", "Trying to get recipe _ID: " + existingBoxItem.getRecipeId());
                    existingCustomRecipeItem = dao.getUserRecipeById(existingBoxItem.getRecipeId());
                } catch (Exception e) {
                    Log.e("Database Error", "Error accessing read-only recipe item: " + e.toString());
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
    }

    private void getReadOnlyItem() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RecipeDao dao = new RecipeDao(getContext());
                    readOnlyItem = dao.buildRecipeFromId(String.valueOf(existingBoxItem.getRecipeId()));
                } catch (Exception e) {
                    Log.e("Database Error", "Error accessing read-only recipe item: " + e.toString());
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.recipe_dialog, container, false);

        //find views
        TextView title = parentView.findViewById(R.id.customRecipeTitle);
        Button editBtn = parentView.findViewById(R.id.customRecipeEditBtn);
        pager = parentView.findViewById(R.id.customRecipeViewPager);
        TabLayout tabs = parentView.findViewById(R.id.customRecipePagerDots);

        //set parent views according to mode
        if (MODE.equals("view")) {
            title.setText("View Recipe");
            editBtn.setVisibility(View.VISIBLE);
            if ( ! existingBoxItem.isUserAdded()) {
                editBtn.setEnabled(false);
            }
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                    CustomRecipeDialog dialog = new CustomRecipeDialog("edit", existingBoxItem);
                    dialog.show(Objects.requireNonNull(getFragmentManager()), "input a recipe!");
                }
            });
        } else if (MODE.equals("edit")) {
            title.setText("Edit Recipe");
        }

        //set up pages and pager dots
        pager.setOffscreenPageLimit(2);
        tabs.setupWithViewPager(pager, true);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(), MODE);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 2) {
                    nextBtn.setText("Save");
                } else {
                    nextBtn.setText("Next");
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    nextBtn.setText("Save");
                } else {
                    nextBtn.setText("Next");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {  }
        });

        addDialogBtnClicks(parentView);

        return parentView;
    }

    private int getItemId(final String name) {
        final int[] id = new int[1];
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                    UserInventoryItemDao dao = db.getUserInventoryDao();
                    id[0] = dao.getInventoryItemIdByName(name);
                    if (id[0] == 0) {
                        //not in inventory, check read-only db
                        Log.e("INVENTORY", "'" + name + "' was not found in inventory, searching read-only db");
                        try {
                            ItemDao itemDao = new ItemDao(getContext());
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

    private boolean inInventory(final String name) {
        final boolean[] inInventory = {false};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                    UserInventoryItemDao dao = db.getUserInventoryDao();
                    int id = dao.getInventoryItemIdByName(name);
                    if (id == 0) {
                        Log.e("INVENTORY_DEBUG", "Setting '" + name + "' to not in inventory!");
                        inInventory[0] = false;
                    }
                    inInventory[0] = true;
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

    private void addDialogBtnClicks(View view) {
        final Button backBtn = view.findViewById(R.id.customRecipeBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() - 1, true);
            }
        });

        Button cancelBtn = view.findViewById(R.id.customRecipeCancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        nextBtn = view.findViewById(R.id.customRecipeSaveBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() == 2) {
                    //get instructions
                    ArrayList<String> instructions = new ArrayList<>();
                    for (int i = 0; i< instructionsAdapter.getItemCount(); i++) {
                        if ( ! instructionsAdapter.getItemAtPosition(i).getInstructionText().equals("")) {
                            instructions.add(instructionsAdapter.getItemAtPosition(i).getInstructionText());
                        }
                    }
                    //get ingredients
                    ingredientsList.clear(); //make sure this list is empty
                    for (int i=0; i<ingredientsAdapter.getItemCount(); i++) {
                        if ( ! ingredientsAdapter.getItemAtPosition(i).getItemName().equals("")) {
                            UserRecipeItemJoin tempItem = new UserRecipeItemJoin();
                            tempItem.recipeId = ! MODE.equals("create") && existingBoxItem.isUserAdded() ? existingBoxItem.getRecipeId() : -1;
                            tempItem.itemId = getItemId(ingredientsAdapter.getItemAtPosition(i).getItemName());
                            tempItem.inInventory = inInventory(ingredientsAdapter.getItemAtPosition(i).getItemName());
                            tempItem.itemName = ingredientsAdapter.getItemAtPosition(i).getItemName();
                            tempItem.detail = ingredientsAdapter.getItemAtPosition(i).getDetail();
                            tempItem.quantity = ingredientsAdapter.getItemAtPosition(i).getAmount();
                            tempItem.unit = ingredientsAdapter.getItemAtPosition(i).getUnit().equals("Unit") ? "" :
                                    ingredientsAdapter.getItemAtPosition(i).getUnit();
                            ingredientsList.add(tempItem);
                        }
                    }
                    newRecipe.setRecipeInstructions(instructions);
                    newRecipe.setKeywords(keywordsList);
                    if (MODE.equals("edit") && existingBoxItem.isUserAdded()) {
                        performUpdateOperations();
                    } else if (MODE.equals("edit") | MODE.equals("create")) {
                        performInsertOperations();
                    }
                    dismiss();
                } else {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                }
            }
        });
    }

    private void performUpdateOperations() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                UserRecipeDao dao = db.getUserRecipeDao();
                UserRecipeItemJoinDao joinDao = db.getUserRecipeItemJoinDao();
                UserRecipeBoxDao recipeBoxDao = db.getUserRecipeBoxDao();

                if (existingBoxItem.isUserAdded()) {
                    //double check that we're editing an existing custom recipe
                    newRecipe.set_ID(existingBoxItem.getRecipeId());
                    dao.update(newRecipe);
                    existingBoxItem.setRecipeName(newRecipe.getName());
                    existingBoxItem.setCategory(newRecipe.getRecipeCategory());
                    existingBoxItem.setServings(newRecipe.getRecipeYield());
                    recipeBoxDao.update(existingBoxItem);

                    //delete old ingredient join items
                    List<UserRecipeItemJoin> oldIngredients = joinDao.getJoinItemsInRecipe(existingBoxItem.getRecipeId());
                    for (int i=0; i<oldIngredients.size(); i++) {
                        joinDao.delete(oldIngredients.get(i));
                    }

                    //add new ingredient join items
                    for (int i=0; i<ingredientsList.size(); i++) {
                        if (ingredientsList.get(i).itemId == -1) {
                            ingredientsList.get(i).itemId = 0;
                        }
                        ingredientsList.get(i).recipeId = existingBoxItem.getRecipeId();
                        joinDao.insert(ingredientsList.get(i));
                    }
                }
            }
        }).start();
        /*
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                    UserRecipeDao dao = db.getUserRecipeDao();
                    UserRecipeItemJoinDao joinDao = db.getUserRecipeItemJoinDao();
                    UserRecipeBoxDao recipeBoxDao = db.getUserRecipeBoxDao();

                    if (existingBoxItem.isUserAdded()) {
                        //double check that we're editing an existing custom recipe
                        newRecipe.set_ID(existingBoxItem.getRecipeId());
                        dao.update(newRecipe);
                        existingBoxItem.setRecipeName(newRecipe.getName());
                        existingBoxItem.setCategory(newRecipe.getRecipeCategory());
                        existingBoxItem.setServings(newRecipe.getRecipeYield());
                        recipeBoxDao.update(existingBoxItem);

                        //delete old ingredient join items
                        List<UserRecipeItemJoin> oldIngredients = joinDao.getJoinItemsInRecipe(existingBoxItem.getRecipeId());
                        for (int i=0; i<oldIngredients.size(); i++) {
                            joinDao.delete(oldIngredients.get(i));
                        }

                        //add new ingredient join items
                        for (int i=0; i<ingredientsList.size(); i++) {
                            if (ingredientsList.get(i).itemId == -1) {
                                ingredientsList.get(i).itemId = 0;
                            }
                            ingredientsList.get(i).recipeId = existingBoxItem.getRecipeId();
                            joinDao.insert(ingredientsList.get(i));
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        }*/
    }

    private void performInsertOperations(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //insert UserRecipe
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
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
                    Log.e("Database Error", e.toString());
                }
            }
        }).start();
    }

    /*
     * Inner instructionsAdapter class for our lovely pager in the custom recipe dialog
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private String parentMode;

        ScreenSlidePagerAdapter (FragmentManager manager, String mode) {
            super(manager);
            parentMode = mode;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                //nextBtn.setText("Next");
                return new FirstPageFragment(parentMode);
            } else if (position == 1) {
                //nextBtn.setText("Next");
                return new SecondPageFragment(parentMode);
            } else {
                //defaults to third page
                //nextBtn.setText("Save");
                return new ThirdPageFragment(parentMode);
            }
        }

        @Override
        public int getCount() {
            return pages;
        }

    }

    public static class FirstPageFragment extends Fragment {
        //private UserRecipe existingCustomItem;
        //private Recipe readOnlyItem;
        private String MODE;
        private Bitmap bitmap;
        private ImageButton imageBtn;

        FirstPageFragment(String mode) {
            MODE = mode;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.custom_recipe_page_1, container, false);

            //find all views in page
            Spinner categorySpinner = view.findViewById(R.id.customRecipeCategory);
            final Spinner cuisineSpinner = view.findViewById(R.id.customRecipeCuisine);
            final SearchView keywordSearch = view.findViewById(R.id.customRecipeKeywordsSearch);
            final ChipGroup keywordChipGroup = view.findViewById(R.id.customRecipeKeywords);
            imageBtn = view.findViewById(R.id.customRecipeImage);
            final EditText nameBox = view.findViewById(R.id.customRecipeName);
            final EditText authorBox = view.findViewById(R.id.customRecipeAuthor);
            final TextInputEditText descriptionBox = view.findViewById(R.id.customRecipeDescription);
            final EditText timeBox = view.findViewById(R.id.customRecipeTotalTime);
            final EditText yieldBox = view.findViewById(R.id.customRecipeYield);
            List<EditText> entryBoxes = new ArrayList<>(Arrays.asList(nameBox, authorBox, descriptionBox, timeBox, yieldBox));

            //select dialog mode
            switch (MODE) {
                case "create":
                    // set up empty page, set newRecipe values to empty strings (because they seem to hang on to old ones???)
                    newRecipe.setName("");
                    newRecipe.setAuthor("");
                    newRecipe.setDescription("");
                    newRecipe.setTotalTime("");
                    newRecipe.setRecipeYield("");
                    newRecipe.setKeywords(new ArrayList<String>());
                    break;
                case "view":
                    // set up disabled page filled with data
                    //set image
                    imageBtn.setEnabled(false);
                    categorySpinner.setEnabled(false);
                    cuisineSpinner.setEnabled(false);
                    keywordSearch.setEnabled(false);
                    keywordSearch.setVisibility(View.GONE);
                    getExistingKeywords(keywordChipGroup);
                    setEntryBoxValues(entryBoxes);
                    for (int i = 0; i < entryBoxes.size(); i++) {
                        entryBoxes.get(i).setEnabled(false);
                    }
                    break;
                case "edit":
                    // set up page filled with data, prepare to overwrite existing values
                    //set image
                    getExistingKeywords(keywordChipGroup);
                    setEntryBoxValues(entryBoxes);
                    if (!existingBoxItem.isUserAdded()) {
                        //if editing read-only recipe, don't allow same name to be used (we're performing a pseudo save-as function!)
                        nameBox.setTextColor(Color.RED);
                    }
                    break;
                default:
                    Log.e("Recipe Dialog Error", "Invalid mode selected: " + MODE);
                    break;
            }

            //add categories to spinner
            final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, getCategories());
            categorySpinner.setAdapter(categoryAdapter);
            if (MODE.equals("edit")) { categorySpinner.setSelection(categoryAdapter.getPosition(existingBoxItem.getCategory())); }
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if ( !Objects.equals(categoryAdapter.getItem(position), "Category") && ! MODE.equals("view")) {
                        newRecipe.setRecipeCategory(categoryAdapter.getItem(position));
                    } else if (Objects.equals(categoryAdapter.getItem(position), "Category")) {
                        newRecipe.setRecipeCategory("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //newRecipe.setRecipeCategory("");
                }
            });

            //add cuisines to spinner
            final ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getCuisines());
            cuisineSpinner.setAdapter(cuisineAdapter);
            if (MODE.equals("edit")) {
                String cuisine = existingBoxItem.isUserAdded() ? existingCustomRecipeItem.getRecipeCuisine() : readOnlyItem.getRecipeCuisine();
                cuisineSpinner.setSelection(cuisineAdapter.getPosition(cuisine));
            }
            cuisineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if ( !Objects.equals(cuisineAdapter.getItem(position), "Cuisine") && ! MODE.equals("view")) {
                        newRecipe.setRecipeCuisine(cuisineAdapter.getItem(position));
                    } else if (Objects.equals(cuisineAdapter.getItem(position), "Cuisine")) {
                        newRecipe.setRecipeCuisine("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //newRecipe.setRecipeCuisine("");
                }
            });

            //add functionality to keyword chips
            keywordSearch.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    addChip(keywordChipGroup, keywordSearch.getQuery().toString());
                    keywordsList.add(keywordSearch.getQuery().toString());
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    addChip(keywordChipGroup, keywordSearch.getQuery().toString());
                    keywordsList.add(keywordSearch.getQuery().toString());
                    return false;
                }
            });
            keywordSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    addChip(keywordChipGroup, keywordSearch.getQuery().toString());
                    keywordsList.add(keywordSearch.getQuery().toString());
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            if ( ! MODE.equals("view")) {
                //add functionality to image button
                imageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        //imageIntent.setType("image/*");
                        //imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                        //imageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(imageIntent, 1);
                    }
                });

                //prep entry boxes for saving data
                nameBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        newRecipe.setName(nameBox.getText().toString());
                    }
                });

                authorBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        newRecipe.setAuthor(authorBox.getText().toString());
                    }
                });

                descriptionBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        newRecipe.setDescription(Objects.requireNonNull(descriptionBox.getText()).toString());
                    }
                });

                timeBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        newRecipe.setTotalTime(timeBox.getText().toString());
                    }
                });

                yieldBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        newRecipe.setRecipeYield(yieldBox.getText().toString());
                    }
                });
            }

            return view;
        }

        //Set recipe image on image button
        //Thanks to: http://blog.vogella.com/2011/09/13/android-how-to-get-an-image-via-an-intent/
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                try {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    InputStream stream = Objects.requireNonNull(getContext()).getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                    bitmap = BitmapFactory.decodeStream(stream);
                    if (stream != null) {
                        stream.close();
                    }
                    imageBtn.setImageBitmap(bitmap);
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                } catch(Exception e) {
                    Log.e("InputStream Error", e.toString());
                    e.printStackTrace();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        // Adds a new to chip to the input chip group (recipe keywords)
        private void addChip(final ChipGroup entryChipGroup, String text) {
            final Chip chip = new Chip(Objects.requireNonNull(getContext()));
            chip.setChipDrawable(ChipDrawable.createFromResource(getContext(), R.xml.keyword_chip));
            int paddingDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            chip.setText(text);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( ! MODE.equals("view")) {
                        entryChipGroup.removeView(chip);
                        int keywordIndex = keywordsList.indexOf(chip.getText().toString());
                        if (keywordIndex != -1) { keywordsList.remove(keywordIndex); }
                    }
                }
            });
            entryChipGroup.addView(chip);
        }

        private void setEntryBoxValues(List<EditText> entryBoxes) {
            List<String> values = new ArrayList<>();
            if (existingBoxItem.isUserAdded()) {
                values.addAll(Arrays.asList(existingCustomRecipeItem.getName(), existingCustomRecipeItem.getAuthor(),
                        existingCustomRecipeItem.getDescription(), existingCustomRecipeItem.getTotalTime(), existingCustomRecipeItem.getRecipeYield()));
            } else {
                values.addAll(Arrays.asList(readOnlyItem.getName(), readOnlyItem.getAuthor(),
                        readOnlyItem.getDescription(), readOnlyItem.getTotalTime(), readOnlyItem.getRecipeYield()));
            }
            for (int i=0; i<entryBoxes.size(); i++) {
                if (values.get(i) != null) {
                    entryBoxes.get(i).setText(values.get(i));
                }
            }
        }

        //pulls keywords from existing item and adds them to the chip group
        private void getExistingKeywords(ChipGroup entryChipGroup) {
            if (MODE.equals("create"))
            if (existingBoxItem.isUserAdded()) {
                if (existingCustomRecipeItem.getKeywords() != null) {
                    for (int i=0; i<existingCustomRecipeItem.getKeywords().size(); i++) {
                        addChip(entryChipGroup, existingCustomRecipeItem.getKeywords().get(i));
                    }
                }
            } else {
                if (readOnlyItem.getKeywords() != null) {
                    for (int i=0; i<readOnlyItem.getKeywords().size(); i++) {
                        addChip(entryChipGroup, readOnlyItem.getKeywords().get(i));
                    }
                }
            }
        }

        //gets db categories or existing item category
        private List<String> getCategories() {
            List<String> categories = new ArrayList<>();
            if (MODE.equals("view")) {
                if (existingBoxItem.getCategory() != null) {
                    categories.add(existingBoxItem.getCategory());
                }
            } else {
                categories.add("Category");
                CategoryDao dao = new CategoryDao(getContext());
                categories.addAll(dao.getAllCategoryNames());
            }
            return categories;
        }

        //gets db cuisines or existing item cuisine
        private List<String> getCuisines() {
            List<String> cuisines = new ArrayList<>();
            if (MODE.equals("view")) {
                if (existingBoxItem.isUserAdded()) {
                    if (existingCustomRecipeItem.getRecipeCuisine() != null) {
                        cuisines.add(existingCustomRecipeItem.getRecipeCuisine());
                    }
                } else {
                    if (readOnlyItem.getRecipeCuisine() != null) {
                        cuisines.add(readOnlyItem.getRecipeCuisine());
                    }
                }
            } else {
                cuisines.add("Cuisine");
                CuisineDao dao = new CuisineDao(getContext());
                cuisines.addAll(dao.getAllCuisineNames());
            }
            return cuisines;
        }
    }

    public static class SecondPageFragment extends Fragment {
        private List<RecipeIngredientsViewModel> modelList = new ArrayList<>();
        private String MODE;

        SecondPageFragment(String mode) {
            MODE = mode;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.custom_recipe_page_2, container, false);

            //set up recycler
            RecyclerView rv = view.findViewById(R.id.customRecipeIngredientsRecycler);
            rv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            rv.setLayoutManager(llm);
            ingredientsAdapter = new RecipeIngredientsAdapter();
            rv.setAdapter(ingredientsAdapter);
            ingredientsAdapter.updateData(modelList);
            //set custom animation?

            //handle different modes
            switch (MODE) {
                case "create":
                    //initialize empty page
                    ingredientsAdapter.isClickable = true;
                    modelList.add(new RecipeIngredientsViewModel(true, "", "", "", ""));
                    ingredientsAdapter.updateData(modelList);
                    break;
                case "view":
                    //initialize disabled page filled with data
                    ingredientsAdapter.isClickable = false;
                    getExistingItems();
                    ingredientsAdapter.updateData(modelList);
                    break;
                case "edit":
                    //best of both worlds! initialize editable page filled with data
                    ingredientsAdapter.isClickable = true;
                    getExistingItems();
                    modelList.add(new RecipeIngredientsViewModel(true, "", "", "", ""));
                    ingredientsAdapter.updateData(modelList);
                    break;
                default:
                    Log.e("Recipe Dialog Error", "Invalid mode selected: " + MODE);
                    break;
            }
            return view;
        }

        //set ingredients text to existing ingredients values
        private void getExistingItems() {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (existingBoxItem.isUserAdded()) {
                        UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                        UserRecipeItemJoinDao dao = db.getUserRecipeItemJoinDao();
                        List<UserRecipeItemJoin> joinItems = dao.getJoinItemsInRecipe(existingCustomRecipeItem.get_ID());
                        for (int i=0; i<joinItems.size(); i++) {
                            modelList.add(new RecipeIngredientsViewModel(false, joinItems.get(i).itemName, joinItems.get(i).detail,
                                    joinItems.get(i).quantity, joinItems.get(i).unit));
                        }
                    } else {
                        RecipeItemJoinDao dao = new RecipeItemJoinDao(getContext());
                        ItemDao itemDao = new ItemDao(getContext());
                        List<RecipeItemJoin> joinItems = dao.getJoinItemsInRecipe(String.valueOf(readOnlyItem.get_ID()));
                        for (int i=0; i<joinItems.size(); i++) {
                            String itemName = itemDao.getItemName(String.valueOf(joinItems.get(i).getItemId()));
                            modelList.add(new RecipeIngredientsViewModel(false, itemName, joinItems.get(i).getDetail(),
                                    joinItems.get(i).getQuantity(), joinItems.get(i).getUnit()));
                        }
                    }
                }
            });
            t.start();
            try {
                t.join();
            }catch (Exception e) {
                Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
            }
        }
    }

    public static class ThirdPageFragment extends Fragment {
        private String MODE;
        private ArrayList<String> instructions = new ArrayList<>();
        private List<RecipeInstructionsViewModel> modelList = new ArrayList<>();

        ThirdPageFragment(String mode) {
            MODE = mode;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.custom_recipe_page_3, container, false);

            //get views, set up recycler
            RecyclerView rv = view.findViewById(R.id.customRecipeInstructionsRecycler);
            rv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            rv.setLayoutManager(llm);
            instructionsAdapter = new RecipeInstructionsAdapter();
            rv.setAdapter(instructionsAdapter);
            instructionsAdapter.updateData(modelList);
            //TODO: possibly create custom animation...
            //((SimpleItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);

            //figure out mode
            switch (MODE) {
                case "create":
                    //set up empty page
                    instructionsAdapter.isClickable = true;
                    modelList.add(new RecipeInstructionsViewModel("", true));
                    instructionsAdapter.updateData(modelList);
                    break;
                case "view":
                    //filled page that you can't edit
                    instructionsAdapter.isClickable = false;
                    instructions = existingBoxItem.isUserAdded() ? existingCustomRecipeItem.getRecipeInstructions() : readOnlyItem.getRecipeInstructions();
                    if (instructions != null) {
                        //only set steps if there were actually instructions previously
                        for (int i = 0; i < instructions.size(); i++) {
                            modelList.add(new RecipeInstructionsViewModel(instructions.get(i), false));
                        }
                        instructionsAdapter.updateData(modelList);
                    } else {
                        instructions = new ArrayList<>();
                    }
                    break;
                case "edit":
                    //have your data, and edit it, too!
                    instructionsAdapter.isClickable = true;
                    instructions = existingBoxItem.isUserAdded() ? existingCustomRecipeItem.getRecipeInstructions() : readOnlyItem.getRecipeInstructions();
                    if (instructions != null) {
                        //only set steps if there were actually instructions previously
                        for (int i = 0; i < instructions.size(); i++) {
                            modelList.add(new RecipeInstructionsViewModel(instructions.get(i), false));
                        }
                    } else {
                        instructions = new ArrayList<>();
                    }
                    modelList.add(new RecipeInstructionsViewModel("", true));
                    instructionsAdapter.updateData(modelList);
                    break;
                default:
                    Log.e("Recipe Dialog Error", "Invalid mode selected: " + MODE);
                    break;
            }
            return view;
        }
    }
}
