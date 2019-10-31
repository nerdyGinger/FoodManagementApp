package apps.nerdyginger.cleanplateclub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

/*
 * This is the dialog for custom recipe input. It must be beautiful, elegant, and absolutely dreamy.
 * Last Edited: 10/30/19
 */
public class CustomRecipeDialog extends DialogFragment {
    private static final int pages = 3; // Slide-able pages for basic info, ingredients, and instructions
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    Button nextBtn;
    private static UserRecipe newRecipe = new UserRecipe();
    private static final ArrayList<UserRecipeItemJoin> ingredientsList = new ArrayList<>();
    private static final ArrayList<String> keywordsList = new ArrayList<>();

    //enable reuse for viewing/editing recipes
    //   "create"   ---   new custom recipe
    //   "view"     ---   view existing recipe (not editable)
    //   "edit"     ---   edit existing recipe
    private String MODE;
    static UserRecipeBoxItem existingItem;

    //empty constructor, default "create" mode
    public CustomRecipeDialog() {
        MODE = "create";
    }

    //constructor to set mode, pass in existing item
    public CustomRecipeDialog(String mode, UserRecipeBoxItem item) {
        MODE = mode;
        existingItem = item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.add_custom_recipe, container, false);
        pager = (ViewPager) parentView.findViewById(R.id.customRecipeViewPager);
        pager.setOffscreenPageLimit(2);
        TabLayout tabs = (TabLayout) parentView.findViewById(R.id.customRecipePagerDots);
        tabs.setupWithViewPager(pager, true);
        pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(), MODE);
        pager.setAdapter(pagerAdapter);
        addDialogBtnClicks(parentView);
        return parentView;
    }

    private void addDialogBtnClicks(View view) {
        Button backBtn = view.findViewById(R.id.customRecipeBackBtn);
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
        nextBtn.setText("Next");
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() == 2) {
                    newRecipe.setKeywords(keywordsList);
                    performInsertOperations();
                    dismiss();
                } else {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                }
            }
        });
    }

    private void performInsertOperations(){
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //insert UserRecipe
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                    UserRecipeDao dao = db.getUserRecipeDao();
                    UserRecipeItemJoinDao itemsDao = db.getUserRecipeItemJoinDao();
                    UserRecipeBoxDao recipeBoxDao = db.getUserRecipeBoxDao();
                    long[] id = dao.insert(newRecipe);

                    //insert UserRecipeBoxItem
                    UserRecipeBoxItem boxItem = new UserRecipeBoxItem();
                    boxItem.setUserAdded(true);
                    boxItem.setRecipeId((int) id[0]);
                    boxItem.setRecipeName(newRecipe.getName());
                    boxItem.setCategory(newRecipe.getRecipeCategory());
                    boxItem.setServings(newRecipe.getRecipeYield());
                    recipeBoxDao.insert(boxItem);

                    Log.e("DEBUG_DEBUG", "Recipe ID: " + String.valueOf(id[0]));

                    //add ingredients to join table
                    for (int i=0; i<ingredientsList.size()-1; i++) {
                        if (ingredientsList.get(i).itemId == -1) {
                            ingredientsList.get(i).itemId = 0;
                        }
                        Log.e("DEBUG_DEBUG", "Item ID: " + String.valueOf(ingredientsList.get(i).itemId));
                        ingredientsList.get(i).recipeId = (int) id[0];
                        itemsDao.insert(ingredientsList.get(i));
                    }
                }
            }).start();
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        }
    }

    /*
     * Inner adapter class for our lovely pager in the custom recipe dialog
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
                nextBtn.setText("Next");
                return new FirstPageFragment(parentMode);
            } else if (position == 1) {
                nextBtn.setText("Next");
                return new SecondPageFragment(parentMode);
            } else {
                //defaults to third page
                nextBtn.setText("Save");
                return new ThirdPageFragment(parentMode);
            }
        }

        @Override
        public int getCount() {
            return pages;
        }

    }

    public static class FirstPageFragment extends Fragment {
        private UserRecipe existingCustomItem;
        private Recipe readOnlyItem;
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
                getDbItem();
                //set image
                imageBtn.setEnabled(false);
                categorySpinner.setEnabled(false);
                cuisineSpinner.setEnabled(false);
                keywordSearch.setEnabled(false);
                getKeywords(keywordChipGroup);
                setEntryBoxValues(entryBoxes);
                for (int i=0; i<entryBoxes.size(); i++) {
                    entryBoxes.get(i).setEnabled(false);
                }
            } else if (MODE.equals("edit")) {
                // set up page filled with data, prepare to overwrite existing values
                getDbItem();
                //set image
                getKeywords(keywordChipGroup);
                setEntryBoxValues(entryBoxes);
            } else {
                Log.e("Recipe Dialog Error", "Invalid mode selected: " + MODE);
            }

            //add categories to spinner
            final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getCategories());
            categorySpinner.setAdapter(categoryAdapter);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if ( ! categoryAdapter.getItem(position).equals("Category") && ! MODE.equals("view")) {
                        newRecipe.setRecipeCategory(categoryAdapter.getItem(position));
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
            cuisineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if ( ! cuisineAdapter.getItem(position).equals("Cuisine") && ! MODE.equals("view")) {
                        newRecipe.setRecipeCuisine(cuisineAdapter.getItem(position));
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
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    addChip(keywordChipGroup, keywordSearch.getQuery().toString());
                    return false;
                }
            });
            keywordSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    addChip(keywordChipGroup, keywordSearch.getQuery().toString());
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
                        newRecipe.setDescription(descriptionBox.getText().toString());
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
            final Chip chip = new Chip(getContext());
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
            keywordsList.add(text);
        }

        private void setEntryBoxValues(List<EditText> entryBoxes) {
            List<String> values = new ArrayList<>();
            if (existingItem.isUserAdded()) {
                values.addAll(Arrays.asList(existingCustomItem.getName(), existingCustomItem.getAuthor(),
                        existingCustomItem.getDescription(), existingCustomItem.getTotalTime(), existingCustomItem.getRecipeYield()));
            } else {
                values.addAll(Arrays.asList(readOnlyItem.getName(), readOnlyItem.getAuthor(),
                        readOnlyItem.getDescription(), readOnlyItem.getTotalTime(), readOnlyItem.getRecipeYield()));
            }
            for (int i=0; i<entryBoxes.size(); i++) {
                entryBoxes.get(i).setText(values.get(i));
            }
        }

        //if dialog is in view or edit mode, get the existing item from appropriate db
        private void getDbItem() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (existingItem.isUserAdded()) {
                        try {
                            UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                            UserRecipeDao dao = db.getUserRecipeDao();
                            existingCustomItem = dao.getUserRecipeById(existingItem.getRecipeId());
                        } catch (Exception e) {
                            Log.e("Database Error", "Error accessing custom recipe item: " + e.toString());
                        }
                    } else {
                        try {
                            RecipeDao dao = new RecipeDao(getContext());
                            readOnlyItem = dao.buildRecipeFromId(String.valueOf(existingItem.getRecipeId()));
                        } catch (Exception e) {
                            Log.e("Database Error", "Error accessing read-only recipe item: " + e.toString());
                        }
                    }
                }
            }).start();
        }

        //pulls keywords from existing item and adds them to the chip group
        private void getKeywords(ChipGroup entryChipGroup) {
            if (existingItem.isUserAdded()) {
                for (int i=0; i<existingCustomItem.getKeywords().size(); i++) {
                    addChip(entryChipGroup, existingCustomItem.getKeywords().get(i));
                }
            } else {
                for (int i=0; i<readOnlyItem.getKeywords().size(); i++) {
                    addChip(entryChipGroup, readOnlyItem.getKeywords().get(i));
                }
            }
        }

        //gets db categories or existing item category
        private List<String> getCategories() {
            List<String> categories = new ArrayList<>();
            categories.add("Category");
            if (MODE.equals("view") || MODE.equals("edit")) {
                categories.add(existingItem.getCategory());
            } else {
                CategoryDao dao = new CategoryDao(getContext());
                categories.addAll(dao.getAllCategoryNames());
            }
            return categories;
        }

        //gets db cuisines or existing item cuisine
        private List<String> getCuisines() {
            List<String> cuisines = new ArrayList<>();
            cuisines.add("Cuisine");
            if (MODE.equals("view") || MODE.equals("edit")) {
                if (existingItem.isUserAdded()) {
                    cuisines.add(existingCustomItem.getRecipeCategory());
                } else {
                    cuisines.add(readOnlyItem.getRecipeCategory());
                }
            } else {
                CuisineDao dao = new CuisineDao(getContext());
                cuisines.addAll(dao.getAllCuisineNames());
            }
            return cuisines;
        }
    }

    public static class SecondPageFragment extends Fragment {
        TextView ingredientsText;
        private UserRecipe existingCustomItem;
        private Recipe readOnlyItem;
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

            if (MODE.equals("create")) {
                //initialize empty page
            } else if (MODE.equals("view")) {
                //initialize disabled page filled with data
                unitSpinner.setEnabled(false);
                ingredientName.setEnabled(false);
                ingredientAmount.setEnabled(false);
                ingredientDetail.setEnabled(false);
            } else if (MODE.equals("edit")) {
                //best of both worlds! initialize editable page filled with data
            } else {
                Log.e("Recipe Dialog Error", "Invalid mode selected: " + MODE);
            }

            //add units to unit spinner
            final ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getUnits());
            unitSpinner.setAdapter(unitAdapter);

            //add names to ingredient name search
            final ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getItems());
            ingredientName.setAdapter(nameAdapter);

            //add functionality to add button
            ImageButton addBtn = view.findViewById(R.id.customRecipeIngredientsAddBtn);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserRecipeItemJoin joinItem = new UserRecipeItemJoin();
                    joinItem.itemName = ingredientName.getText().toString();
                    ingredientName.setText("");
                    joinItem.itemId = getItemId(joinItem.itemName); //set to -1 if not in db
                    joinItem.quantity = ingredientAmount.getText().toString();
                    ingredientAmount.setText("");
                    joinItem.unit = unitAdapter.getItem(unitSpinner.getSelectedItemPosition()).equals("Units") ? "" : unitAdapter.getItem(unitSpinner.getSelectedItemPosition());
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

        //if dialog is in view or edit mode, get the existing item from appropriate db
        private void getDbItem() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (existingItem.isUserAdded()) {
                        try {
                            UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                            UserRecipeDao dao = db.getUserRecipeDao();
                            existingCustomItem = dao.getUserRecipeById(existingItem.getRecipeId());
                        } catch (Exception e) {
                            Log.e("Database Error", "Error accessing custom recipe item: " + e.toString());
                        }
                    } else {
                        try {
                            RecipeDao dao = new RecipeDao(getContext());
                            readOnlyItem = dao.buildRecipeFromId(String.valueOf(existingItem.getRecipeId()));
                        } catch (Exception e) {
                            Log.e("Database Error", "Error accessing read-only recipe item: " + e.toString());
                        }
                    }
                }
            }).start();
        }

        private void setIngredientsText(TextView box) {
            if (existingItem.isUserAdded()) {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                UserRecipeItemJoinDao dao = db.getUserRecipeItemJoinDao();
                List<UserRecipeItemJoin> joinItems = dao.getJoinItemsInRecipe(existingCustomItem.get_ID());
                for (int i=0; i<joinItems.size(); i++) {
                    box.append(joinItems.get(i).itemName + " " + joinItems.get(i).detail + "   " + joinItems.get(i).quantity +
                            " " + joinItems.get(i).unit + "\n\n");
                }
            } else {
                RecipeItemJoinDao dao = new RecipeItemJoinDao(getContext());
                List<RecipeItemJoin> joinItems = dao.get
            }
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
            SharedPreferences userPreferences = getContext().getSharedPreferences(getContext().getPackageName() + "userPreferences", Context.MODE_PRIVATE);
            UnitDao dao = new UnitDao(getContext());
            units.add("Units");
            units.addAll(dao.getAllUnitNamesBySystemId(userPreferences.getString("unitSystemId", "1")));
            return units;
        }
    }

    public static class ThirdPageFragment extends Fragment {
        private String MODE;
        private Integer steps = 0;
        private ArrayList<String> instructions = new ArrayList<>();
        private UserRecipe existingCustomItem;
        private Recipe readOnlyItem;

        //TODO: need instructions custom ViewGroup to allow editing!
        //TODO: assign custom to newRecipe, also, can't edit a read-only recipe! (but maybe allow copying values over to custom to edit?)

        public ThirdPageFragment(String mode) {
            MODE = mode;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.custom_recipe_page_3, container, false);

            //get views
            final TextView instructionsBox = view.findViewById(R.id.customRecipeInstructions);
            final TextInputEditText instructionEntry = view.findViewById(R.id.customRecipeInstructionsEntry);
            ImageButton addBtn = view.findViewById(R.id.customRecipeInstructionsAddBtn);

            //figure out mode
            if (MODE.equals("create")) {
                //set up empty page
            } else if (MODE.equals("view")) {
                //filled page that you can't edit
                getDbItem();
                setInstructionsText(instructionsBox);
                instructionEntry.setEnabled(false);
                addBtn.setEnabled(false);
            } else if (MODE.equals("edit")) {
                //have your data, and edit it, too!
                getDbItem();
                setInstructionsText(instructionsBox);
            } else {
                Log.e("Recipe Dialog Error", "Invalid mode selected: " + MODE);
            }

            instructionsBox.setMovementMethod(new ScrollingMovementMethod());
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    steps++;
                    instructionsBox.append(steps.toString() + ".) " + instructionEntry.getText() + "\n");
                    instructionEntry.setText("");
                    instructions.add(steps.toString());
                    newRecipe.setRecipeInstructions(instructions);
                }
            });

            return view;
        }

        private void setInstructionsText(TextView box) {
            int count = 0;
            if (existingItem.isUserAdded()) {
                for (int i=0; i<existingCustomItem.getRecipeInstructions().size(); i++) {
                    count++;
                    box.append(String.valueOf(count) + ".) " + existingCustomItem.getRecipeInstructions().get(i) + "\n");
                }
            } else {
                for (int i=0; i<readOnlyItem.getRecipeInstructions().size(); i++) {
                    count++;
                    box.append(String.valueOf(count) + ".) " + readOnlyItem.getRecipeInstructions().get(i) + "\n");
                }
            }
        }

        //if dialog is in view or edit mode, get the existing item from appropriate db
        private void getDbItem() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (existingItem.isUserAdded()) {
                        try {
                            UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                            UserRecipeDao dao = db.getUserRecipeDao();
                            existingCustomItem = dao.getUserRecipeById(existingItem.getRecipeId());
                        } catch (Exception e) {
                            Log.e("Database Error", "Error accessing custom recipe item: " + e.toString());
                        }
                    } else {
                        try {
                            RecipeDao dao = new RecipeDao(getContext());
                            readOnlyItem = dao.buildRecipeFromId(String.valueOf(existingItem.getRecipeId()));
                        } catch (Exception e) {
                            Log.e("Database Error", "Error accessing read-only recipe item: " + e.toString());
                        }
                    }
                }
            }).start();
        }
    }
}
