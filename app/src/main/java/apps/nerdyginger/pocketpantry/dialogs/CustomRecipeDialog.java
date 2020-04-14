package apps.nerdyginger.pocketpantry.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import apps.nerdyginger.pocketpantry.Fraction;
import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.RecipeBookActivity;
import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.adapters.RecipeIngredientsAdapter;
import apps.nerdyginger.pocketpantry.adapters.RecipeInstructionsAdapter;
import apps.nerdyginger.pocketpantry.dao.CategoryDao;
import apps.nerdyginger.pocketpantry.dao.CuisineDao;
import apps.nerdyginger.pocketpantry.dao.ItemDao;
import apps.nerdyginger.pocketpantry.dao.RecipeBookDao;
import apps.nerdyginger.pocketpantry.dao.RecipeDao;
import apps.nerdyginger.pocketpantry.dao.RecipeItemJoinDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeBoxDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeItemJoinDao;
import apps.nerdyginger.pocketpantry.helpers.ImageHelper;
import apps.nerdyginger.pocketpantry.helpers.ItemQuantityHelper;
import apps.nerdyginger.pocketpantry.helpers.RecipeDialogHelper;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.RecipeBook;
import apps.nerdyginger.pocketpantry.models.RecipeItemJoin;
import apps.nerdyginger.pocketpantry.models.UserRecipe;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;
import apps.nerdyginger.pocketpantry.models.UserRecipeItemJoin;
import apps.nerdyginger.pocketpantry.view_models.RecipeIngredientsViewModel;
import apps.nerdyginger.pocketpantry.view_models.RecipeInstructionsViewModel;

/*
 * This is the dialog for custom recipe input. It must be beautiful, elegant, and absolutely dreamy.
 *
 * ... Okay, so the _dialog_ has to be beautiful, elegant, and absolutely dreamy. Apparently I'm willing
 * to sacrifice the beauty of this class for that cause, because no 900+ LoC class can be called dreamy.
 *
 * Last Edited: 3/19/2020
 */
public class CustomRecipeDialog extends DialogFragment {
    private Context context;
    private ItemQuantityHelper quantityHelper;
    private RecipeDialogHelper dialogHelper;
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

    //for view mode on read-only recipe
    private static Recipe readOnlyItem;
    private static RecipeBook recipeBook;

    //for edit mode on custom recipe
    private static UserRecipe existingCustomRecipeItem;

    //empty constructor, default "create" mode
    public CustomRecipeDialog() {
        MODE = "create";
    }

    public CustomRecipeDialog(String mode, UserRecipeBoxItem item, Context context) {
        this.context = context;
        constructorGuts(mode, item);
    }

    //constructor to set mode, pass in existing item
    public CustomRecipeDialog(String mode, UserRecipeBoxItem item) {
        this.context = getContext();
        constructorGuts(mode, item);
    }

    private void constructorGuts(String mode, UserRecipeBoxItem item) {
        MODE = mode;
        dialogHelper = new RecipeDialogHelper(context);
        existingBoxItem = item;
        newRecipe = new UserRecipe();
        if ( ! MODE.equals("create") && ! existingBoxItem.isUserAdded()) {
            //view/edit mode, read-only item; get from read-only db
            readOnlyItem =  dialogHelper.getReadOnlyItem(existingBoxItem);
            recipeBook = dialogHelper.getRecipeBook(readOnlyItem);
        } else if ( ! MODE.equals("create")) {
            //view/edit mode, is custom; get from custom db, set newRecipe value to existing values
            existingCustomRecipeItem = dialogHelper.getCustomItem(existingBoxItem);
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

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.recipe_dialog, container, false);
        quantityHelper = new ItemQuantityHelper(getContext());

        //find views
        TextView title = parentView.findViewById(R.id.customRecipeTitle);
        Button editBtn = parentView.findViewById(R.id.customRecipeEditBtn);
        pager = parentView.findViewById(R.id.customRecipeViewPager);
        TabLayout tabs = parentView.findViewById(R.id.customRecipePagerDots);

        //set parent views according to mode
        if (MODE.contains("view")) {
            title.setText(getString(R.string.recipe_dialog_view_title));
            editBtn.setVisibility(View.VISIBLE);
            if ( ! existingBoxItem.isUserAdded()) {
                editBtn.setVisibility(View.GONE);
                editBtn.setEnabled(false);
            }
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Objects.requireNonNull(getDialog()).dismiss();
                    CustomRecipeDialog dialog = new CustomRecipeDialog("edit", existingBoxItem);
                    dialog.show(Objects.requireNonNull(getFragmentManager()), "input a recipe!");
                }
            });
        } else if (MODE.equals("edit")) {
            title.setText(getString(R.string.recipe_dialog_edit_title));
            editBtn.setVisibility(View.GONE);
        } else { // "create"
            editBtn.setVisibility(View.GONE);
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
                    nextBtn.setText(getString(R.string.app_save_btn));
                } else {
                    nextBtn.setText(getString(R.string.app_next_btn));
                }
            }
            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    nextBtn.setText(getString(R.string.app_save_btn));
                } else {
                    nextBtn.setText(getString(R.string.app_next_btn));
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {  }
        });
        addDialogBtnClicks(parentView);
        return parentView;
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
        if (MODE.equals("browse-view")) {
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // this means we want to add the recipe to recipe box
                    if (pager.getCurrentItem() == 2) {
                        //last page, add to recipe box and dismiss
                        dismiss();
                    } else {
                        pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                    }
                }
            });
        } else {
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
                            RecipeIngredientsViewModel viewModelItem = ingredientsAdapter.getItemAtPosition(i);
                            if ( ! viewModelItem.getItemName().equals("")) {
                                UserRecipeItemJoin tempItem = new UserRecipeItemJoin();
                                tempItem.recipeId = ! MODE.equals("create") && existingBoxItem.isUserAdded() ? existingBoxItem.getRecipeId() : -1;
                                tempItem.itemId = dialogHelper.getItemId(viewModelItem.getItemName());
                                tempItem.itemName = viewModelItem.getItemName();
                                tempItem.detail = viewModelItem.getDetail();
                                Fraction quantityFrac = new Fraction();
                                if ( ! quantityFrac.isValidString(viewModelItem.getAmount())) {
                                    Toast.makeText(getContext(), "Invalid ingredient amount: " + viewModelItem.getAmount() +
                                            tempItem.itemName, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                tempItem.quantity = quantityFrac.fromString(viewModelItem.getAmount()).toString();
                                tempItem.unit = viewModelItem.getUnit().equals("Unit") ? "" : viewModelItem.getUnit();
                                ingredientsList.add(tempItem);
                            }
                        }
                        newRecipe.setRecipeInstructions(instructions);
                        newRecipe.setKeywords(keywordsList);
                        if (MODE.equals("edit") && existingBoxItem.isUserAdded()) {
                            performUpdateOperations();
                        } else if (MODE.equals("edit") | MODE.equals("create")) {
                            dialogHelper.performInsertOperations(newRecipe, ingredientsList);
                        }
                        dismiss();
                    } else {
                        pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                    }
                }
            });
        }
    }

    private void performUpdateOperations()  {
        if (dialogHelper.updateCustomRecipe(newRecipe, existingBoxItem)) {
            dialogHelper.updateIngredients(ingredientsList, existingBoxItem.getRecipeId());
        }
    }

    /*
     * Inner instructionsAdapter class for our lovely pager in the custom recipe dialog
     */
    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private String parentMode;

        ScreenSlidePagerAdapter (FragmentManager manager, String mode) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            parentMode = mode;
        }

        @NonNull
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
            ImageHelper imageHelper = new ImageHelper(getContext());

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
            RelativeLayout recipeBookContainer = view.findViewById(R.id.customRecipeBookContainer);

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
                    //show/hide recipe book info
                    if (existingBoxItem.isUserAdded()) {
                        recipeBookContainer.setVisibility(View.GONE);
                        //TODO: set user-added image
                    } else {
                        // is read-only, set image
                        imageBtn.setImageBitmap(imageHelper.retrieveImage(imageHelper.getFilename(recipeBook, readOnlyItem)));
                        recipeBookContainer.setVisibility(View.VISIBLE);
                        TextView recipeBookName = view.findViewById(R.id.bookContainerName);
                        recipeBookName.setText(recipeBook.getName());
                        Button recipeBookBtn = view.findViewById(R.id.bookContainerBtn);
                        recipeBookBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), RecipeBookActivity.class);
                                intent.putExtra("RecipeBookId", recipeBook.get_ID());
                                startActivity(intent);
                            }
                        });
                    }
                    break;
                case "browse-view":
                    // set a different layout?
                    imageBtn.setImageBitmap(imageHelper.retrieveImage(imageHelper.getFilename(recipeBook, readOnlyItem)));
                    recipeBookContainer.setVisibility(View.VISIBLE);
                    TextView recipeBookName = view.findViewById(R.id.bookContainerName);
                    recipeBookName.setText(recipeBook.getName());
                    Button recipeBookBtn = view.findViewById(R.id.bookContainerBtn);
                    recipeBookBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), RecipeBookActivity.class);
                            intent.putExtra("RecipeBookId", recipeBook.get_ID());
                            startActivity(intent);
                        }
                    });
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
                    ArrayList<String> instructions = existingBoxItem.isUserAdded() ? existingCustomRecipeItem.getRecipeInstructions() : readOnlyItem.getRecipeInstructions();
                    if (instructions != null) {
                        //only set steps if there were actually instructions previously
                        for (int i = 0; i < instructions.size(); i++) {
                            modelList.add(new RecipeInstructionsViewModel(instructions.get(i), false));
                        }
                        instructionsAdapter.updateData(modelList);
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
