package apps.nerdyginger.cleanplateclub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
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

import apps.nerdyginger.cleanplateclub.adapters.RecipeInstructionsAdapter;
import apps.nerdyginger.cleanplateclub.dao.CategoryDao;
import apps.nerdyginger.cleanplateclub.dao.CuisineDao;
import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.RecipeDao;
import apps.nerdyginger.cleanplateclub.dao.RecipeItemJoinDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeBoxDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeItemJoinDao;
import apps.nerdyginger.cleanplateclub.models.Recipe;
import apps.nerdyginger.cleanplateclub.models.RecipeItemJoin;
import apps.nerdyginger.cleanplateclub.models.UserRecipe;
import apps.nerdyginger.cleanplateclub.models.UserRecipeBoxItem;
import apps.nerdyginger.cleanplateclub.models.UserRecipeItemJoin;
import apps.nerdyginger.cleanplateclub.view_models.RecipeInstructionsViewModel;

/*
 * This is the dialog for custom recipe input. It must be beautiful, elegant, and absolutely dreamy.
 * Last Edited: 11/4/19
 */
public class CustomRecipeDialog extends DialogFragment {
    private static final int pages = 3; // Slide-able pages for basic info, ingredients, and instructions
    private ViewPager pager;
    private Button nextBtn;
    private static UserRecipe newRecipe = new UserRecipe();
    private static final ArrayList<UserRecipeItemJoin> ingredientsList = new ArrayList<>();
    private static final ArrayList<String> keywordsList = new ArrayList<>();
    private static RecipeInstructionsAdapter instructionsAdapter = new RecipeInstructionsAdapter();

    //enable reuse for viewing/editing recipes
    //   "create"   ---   new custom recipe
    //   "view"     ---   view existing recipe (not editable)
    //   "edit"     ---   edit existing recipe
    private String MODE;
    private static UserRecipeBoxItem existingBoxItem;

    //for edit mode on read-only recipe
    private static Recipe readOnlyItem;
    private static String nameNotAllowed;

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
        if ( ! MODE.equals("create") && ! existingBoxItem.isUserAdded()) {
            //if passed in item is read-only, get from appropriate db
            getReadOnlyItem();
            if (MODE.equals("edit")) {
                //if we're trying to edit a read-only item, we'll actually create a new custom item (like save-as)
                newRecipe.setName(readOnlyItem.getName());
                nameNotAllowed = readOnlyItem.getName(); //don't save a custom recipe with the same name
                newRecipe.setAuthor(readOnlyItem.getAuthor());
                newRecipe.setDescription(readOnlyItem.getDescription());
                newRecipe.setTotalTime(readOnlyItem.getTotalTime());
                newRecipe.setRecipeYield(readOnlyItem.getRecipeYield());
                newRecipe.setKeywords(readOnlyItem.getKeywords());
                newRecipe.setRecipeCategory(readOnlyItem.getRecipeCategory());
                newRecipe.setRecipeCuisine(readOnlyItem.getRecipeCuisine());
                newRecipe.setRecipeInstructions(readOnlyItem.getRecipeInstructions());
            }
            //ingredients???
        } else if ( ! MODE.equals("create")) {
            //existing item is custom, so get whether viewing or editing
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
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                    CustomRecipeDialog dialog = new CustomRecipeDialog("edit", existingBoxItem);
                    dialog.show(getFragmentManager(), "input a recipe!");
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

        addDialogBtnClicks(parentView);

        return parentView;
    }

    private void addDialogBtnClicks(View view) {
        final Button backBtn = view.findViewById(R.id.customRecipeBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() == 2) {
                    nextBtn.setText("Next");
                }
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
        nextBtn.setText("Next");
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() == 2) {
                    ArrayList<String> instructions = new ArrayList<>();
                    for (int i = 0; i< instructionsAdapter.getItemCount(); i++) {
                        instructions.add(instructionsAdapter.getItemAtPosition(i).getInstructionText());
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
                    if (pager.getCurrentItem() == 1) {
                        nextBtn.setText("Save");
                    }
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                }
            }
        });
    }

    private void performUpdateOperations() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                    UserRecipeDao dao = db.getUserRecipeDao();
                    UserRecipeItemJoinDao joinDao = db.getUserRecipeItemJoinDao();
                    UserRecipeBoxDao recipeBoxDao = db.getUserRecipeBoxDao();

                    if (existingBoxItem.isUserAdded()) {
                        //editing an existing custom recipe
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
                            Log.e("DEBUG_DEBUG", "Item ID: " + ingredientsList.get(i).itemId);
                            ingredientsList.get(i).recipeId = existingBoxItem.getRecipeId();
                            joinDao.insert(ingredientsList.get(i));
                        }

                    } else {
                        //performing "Save As" on a read-only recipe
                        long[] id = dao.insert(newRecipe);

                        UserRecipeBoxItem boxItem = new UserRecipeBoxItem();
                        boxItem.setUserAdded(true);
                        boxItem.setRecipeId((int) id[0]);
                        boxItem.setRecipeName(newRecipe.getName());
                        boxItem.setCategory(newRecipe.getRecipeCategory());
                        boxItem.setServings(newRecipe.getRecipeYield());
                        recipeBoxDao.insert(boxItem);

                        //add ingredients to join table
                        for (int i=0; i<ingredientsList.size(); i++) {
                            if (ingredientsList.get(i).itemId == -1) {
                                ingredientsList.get(i).itemId = 0;
                            }
                            Log.e("DEBUG_DEBUG", "Item ID: " + ingredientsList.get(i).itemId);
                            ingredientsList.get(i).recipeId = (int) id[0];
                            joinDao.insert(ingredientsList.get(i));
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        }
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
                    Log.e("INSERT_DEBUG", "Old _ID: " + newRecipe.get_ID());
                    long[] id = new long[1];
                    try {
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

                    Log.e("DEBUG_DEBUG", "Recipe ID: " + id[0]);

                    //add ingredients to join table
                    for (int i = 0; i < ingredientsList.size(); i++) {
                        if (ingredientsList.get(i).itemId == -1) {
                            ingredientsList.get(i).itemId = 0;
                        }
                        Log.e("DEBUG_DEBUG", "Item ID: " + ingredientsList.get(i).itemId);
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
            if (MODE.equals("create")) {
                // set up empty page
            } else if (MODE.equals("view")) {
                // set up disabled page filled with data
                //set image
                imageBtn.setEnabled(false);
                categorySpinner.setEnabled(false);
                cuisineSpinner.setEnabled(false);
                keywordSearch.setEnabled(false);
                keywordSearch.setVisibility(View.GONE);
                getExistingKeywords(keywordChipGroup);
                setEntryBoxValues(entryBoxes);
                for (int i=0; i<entryBoxes.size(); i++) {
                    entryBoxes.get(i).setEnabled(false);
                }
            } else if (MODE.equals("edit")) {
                // set up page filled with data, prepare to overwrite existing values
                //set image
                getExistingKeywords(keywordChipGroup);
                setEntryBoxValues(entryBoxes);
                if ( ! existingBoxItem.isUserAdded()) {
                    //if editing read-only recipe, don't allow same name to be used (we're performing a pseudo save-as function!)
                    nameBox.setTextColor(Color.RED);
                }
            } else {
                Log.e("Recipe Dialog Error", "Invalid mode selected: " + MODE);
            }

            //add categories to spinner
            final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getCategories());
            categorySpinner.setAdapter(categoryAdapter);
            if (MODE.equals("edit")) { categorySpinner.setSelection(categoryAdapter.getPosition(existingBoxItem.getCategory())); }
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if ( ! categoryAdapter.getItem(position).equals("Category") && ! MODE.equals("view")) {
                        newRecipe.setRecipeCategory(categoryAdapter.getItem(position));
                    } else if (categoryAdapter.getItem(position).equals("Category")) {
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
                    if ( ! cuisineAdapter.getItem(position).equals("Cuisine") && ! MODE.equals("view")) {
                        newRecipe.setRecipeCuisine(cuisineAdapter.getItem(position));
                    } else if (cuisineAdapter.getItem(position).equals("Cuisine")) {
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
                        Intent imageIntent = new Intent();
                        imageIntent.setType("image/*");
                        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                        imageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(imageIntent, 1);
                    }
                });

                //prep entry boxes for saving data
                nameBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (MODE.equals("edit") && ! existingBoxItem.isUserAdded() && nameNotAllowed.equals(nameBox.getText().toString())) {
                            nameBox.setTextColor(Color.RED);
                        }
                        nameBox.setTextColor(getResources().getColor(R.color.editTextColor));
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
                    InputStream stream = getContext().getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
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
        TextView ingredientsText;
        private String MODE;

        //TODO: need to update ingredients display to view group so they can be edited!

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

            //get views for page
            ingredientsText = view.findViewById(R.id.customRecipeIngredients);
            final Spinner unitSpinner = view.findViewById(R.id.customRecipeIngredientsUnit);
            final AutoCompleteTextView ingredientName = view.findViewById(R.id.customRecipeIngredientName);
            final EditText ingredientAmount = view.findViewById(R.id.customRecipeIngredientsAmount);
            final EditText ingredientDetail = view.findViewById(R.id.customRecipeIngredientsDetail);
            ImageButton addBtn = view.findViewById(R.id.customRecipeIngredientsAddBtn);

            switch (MODE) {
                case "create":
                    //initialize empty page
                    break;
                case "view":
                    //initialize disabled page filled with data
                    unitSpinner.setVisibility(View.GONE);
                    ingredientName.setVisibility(View.GONE);
                    ingredientAmount.setVisibility(View.GONE);
                    ingredientDetail.setVisibility(View.GONE);
                    addBtn.setVisibility(View.GONE);
                    setIngredientsText(ingredientsText);
                    break;
                case "edit":
                    //best of both worlds! initialize editable page filled with data
                    setIngredientsText(ingredientsText);
                    break;
                default:
                    Log.e("Recipe Dialog Error", "Invalid mode selected: " + MODE);
                    break;
            }

            //add units to unit spinner
            final ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, getUnits());
            unitSpinner.setAdapter(unitAdapter);

            //add names to ingredient name search
            final ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getItems());
            ingredientName.setAdapter(nameAdapter);

            //add functionality to add button
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserRecipeItemJoin joinItem = new UserRecipeItemJoin();
                    joinItem.itemName = ingredientName.getText().toString();
                    ingredientName.setText("");
                    joinItem.itemId = getItemId(joinItem.itemName); //set to -1 if not in db
                    joinItem.quantity = ingredientAmount.getText().toString();
                    ingredientAmount.setText("");
                    joinItem.unit = Objects.equals(unitAdapter.getItem(unitSpinner.getSelectedItemPosition()), "Units") ? "" : unitAdapter.getItem(unitSpinner.getSelectedItemPosition());
                    if (ingredientDetail.getText().toString().equals("")) {
                        joinItem.detail = "";
                    } else {
                        joinItem.detail = "(" + ingredientDetail.getText().toString() + ")";
                        ingredientDetail.setText("");
                    }
                    ingredientsList.add(joinItem);
                    addIngredientText(joinItem);
                }
            });

            return view;
        }

        private void addIngredientText(UserRecipeItemJoin joinItem) {
            String newText = joinItem.itemName + " " + joinItem.detail + "   " + joinItem.quantity + " " + joinItem.unit + "\n\n";
            ingredientsText.append(newText);
        }

        //set ingredients text to existing ingredients values
        private void setIngredientsText(final TextView box) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (existingBoxItem.isUserAdded()) {
                        UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                        UserRecipeItemJoinDao dao = db.getUserRecipeItemJoinDao();
                        List<UserRecipeItemJoin> joinItems = dao.getJoinItemsInRecipe(existingCustomRecipeItem.get_ID());
                        for (int i=0; i<joinItems.size(); i++) {
                            box.append(joinItems.get(i).itemName + " " + joinItems.get(i).detail + "   " + joinItems.get(i).quantity +
                                    " " + joinItems.get(i).unit + "\n\n");
                        }
                    } else {
                        RecipeItemJoinDao dao = new RecipeItemJoinDao(getContext());
                        ItemDao itemDao = new ItemDao(getContext());
                        List<RecipeItemJoin> joinItems = dao.getJoinItemsInRecipe(String.valueOf(readOnlyItem.get_ID()));
                        for (int i=0; i<joinItems.size(); i++) {
                            String itemName = itemDao.getItemName(String.valueOf(joinItems.get(i).getItemId()));
                            box.append(itemName + " " + joinItems.get(i).getDetail() + "   " + joinItems.get(i).getQuantity() +
                                    " " + joinItems.get(i).getUnit() + "\n\n");
                        }
                    }
                }
            });
            t.start();

        }

        private int getItemId(String name) {
            try {
                ItemDao dao = new ItemDao(getContext());
                String stringId =  dao.getItemId(name);
                return Integer.parseInt(stringId);
            } catch (Exception e) {
                //item doesn't exist in db
                return -1;
            }
        }

        private List<String> getItems() {
            List<String> names;
            ItemDao dao = new ItemDao(getContext());
            names = dao.getAllItemNames();
            return names;
        }

        private List<String> getUnits() {
            List<String> units = new ArrayList<>();
            SharedPreferences userPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(getContext().getPackageName() + "userPreferences", Context.MODE_PRIVATE);
            UnitDao dao = new UnitDao(getContext());
            units.add("Units");
            units.addAll(dao.getAllUnitNamesBySystemId(userPreferences.getString("unitSystemId", "1")));
            return units;
        }
    }

    public static class ThirdPageFragment extends Fragment {
        private String MODE;
        private ArrayList<String> instructions = new ArrayList<>();
        private List<RecipeInstructionsViewModel> modelList = new ArrayList<>();

        //TODO: add button for deletion

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
            ((SimpleItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);


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

        //TODO: figure out how to actually save the edited instructions
        // (vvv    'cause this isn't working    vvv)
        @Override
        public void onStop() {
            super.onStop();
            instructions = new ArrayList<>();
            for (int i = 0; i< instructionsAdapter.getItemCount(); i++) {
                instructions.add(instructionsAdapter.getItemAtPosition(i).getInstructionText());
            }
            newRecipe.setRecipeInstructions(instructions);
        }

    }
}
