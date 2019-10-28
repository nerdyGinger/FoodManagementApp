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
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.dao.CategoryDao;
import apps.nerdyginger.cleanplateclub.dao.CuisineDao;
import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.models.UserItem;
import apps.nerdyginger.cleanplateclub.models.UserRecipe;
import apps.nerdyginger.cleanplateclub.models.UserRecipeItemJoin;

/*
 * This is the dialog for custom recipe input. It must be beautiful, elegant, and absolutely dreamy.
 * Last Edited: 10/18/19
 */
public class CustomRecipeDialog extends DialogFragment {
    private static final int pages = 3; // Slide-able pages for basic info, ingredients, and instructions
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    Button nextBtn;
    private static final UserRecipe newRecipe = new UserRecipe();
    private static final ArrayList<UserRecipeItemJoin> ingredientsList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.custom_recipe_page_1, container, false);
        View parentView = inflater.inflate(R.layout.add_custom_recipe, container, false);
        pager = (ViewPager) parentView.findViewById(R.id.customRecipeViewPager);
        pager.setOffscreenPageLimit(2);
        TabLayout tabs = (TabLayout) parentView.findViewById(R.id.customRecipePagerDots);
        tabs.setupWithViewPager(pager, true);
        pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
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
                    //save newRecipe (generate id)
                    //final int newRecipeId = (int) dao.insert(newRecipe);
                    for (int i=0; i<ingredientsList.size()-1; i++) {
                        //ingredientsList.get(i).recipeId = newRecipeId;
                    }
                    dismiss();
                } else {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                }
            }
        });
    }

    /*
     * Inner adapter class for our lovely pager in the custom recipe dialog
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        ScreenSlidePagerAdapter (FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                nextBtn.setText("Next");
                return new FirstPageFragment();
            } else if (position == 1) {
                nextBtn.setText("Next");
                return new SecondPageFragment();
            } else {
                //defaults to third page
                nextBtn.setText("Save");
                return new ThirdPageFragment();
            }
        }

        @Override
        public int getCount() {
            return pages;
        }

    }

    public static class FirstPageFragment extends Fragment {
        private Bitmap bitmap;
        private ImageButton imageBtn;

        public FirstPageFragment() {
            //empty constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.custom_recipe_page_1, container, false);

            //add categories to spinner
            Spinner categorySpinner = view.findViewById(R.id.customRecipeCategory);
            final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getCategories());
            categorySpinner.setAdapter(categoryAdapter);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    newRecipe.setRecipeCategory(categoryAdapter.getItem(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //newRecipe.setRecipeCategory("");
                }
            });

            //add cuisines to spinner
            final Spinner cuisineSpinner = view.findViewById(R.id.customRecipeCuisine);
            final ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getCuisines());
            cuisineSpinner.setAdapter(cuisineAdapter);
            cuisineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    newRecipe.setRecipeCuisine(cuisineAdapter.getItem(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //newRecipe.setRecipeCuisine("");
                }
            });

            //add functionality to keyword chips
            final SearchView keywordSearch = view.findViewById(R.id.customRecipeKeywordsSearch);
            final ChipGroup keywordChipGroup = view.findViewById(R.id.customRecipeKeywords);
            keywordSearch.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    //getChip(keywordChipGroup, getTextFromSomewhere());
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    //getChip(keywordChipGroup, getTextFromSomewhere());
                    return false;
                }
            });

            //add functionality to image button
            imageBtn = view.findViewById(R.id.customRecipeImage);
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
            final EditText nameBox = view.findViewById(R.id.customRecipeName);
            nameBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    newRecipe.setName(nameBox.getText().toString());
                }
            });

            final EditText authorBox = view.findViewById(R.id.customRecipeAuthor);
            authorBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    newRecipe.setAuthor(authorBox.getText().toString());
                }
            });

            final TextInputEditText descriptionBox = view.findViewById(R.id.customRecipeDescription);
            descriptionBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    newRecipe.setDescription(descriptionBox.getText().toString());
                }
            });

            final EditText timeBox = view.findViewById(R.id.customRecipeTotalTime);
            timeBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    newRecipe.setTotalTime(timeBox.getText().toString());
                }
            });

            final EditText yieldBox = view.findViewById(R.id.customRecipeYield);
            yieldBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    newRecipe.setRecipeYield(yieldBox.getText().toString());
                }
            });

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
        protected Chip getChip(final ChipGroup entryChipGroup, String text) {
            final Chip chip = new Chip(getContext());
            chip.setChipDrawable(ChipDrawable.createFromResource(getContext(), R.xml.keyword_chip));
            int paddingDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            chip.setText(text);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    entryChipGroup.removeView(chip);
                }
            });
            return chip;
        }

        private List<String> getCategories() {
            CategoryDao dao = new CategoryDao(getContext());
            return dao.getAllCategoryNames();
        }

        private List<String> getCuisines() {
            CuisineDao dao = new CuisineDao(getContext());
            return dao.getAllCuisineNames();
        }
    }

    public static class SecondPageFragment extends Fragment {

        public SecondPageFragment() {
            //empty constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.custom_recipe_page_2, container, false);

            //add units to unit spinner
            final Spinner unitSpinner = view.findViewById(R.id.customRecipeIngredientsUnit);
            final ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getUnits());
            unitSpinner.setAdapter(unitAdapter);

            //add names to ingredient name search
            final AutoCompleteTextView ingredientName = view.findViewById(R.id.customRecipeIngredientName);
            final ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getItems());
            ingredientName.setAdapter(nameAdapter);

            //find views for add button
            final EditText ingredientAmount = view.findViewById(R.id.customRecipeIngredientsAmount);
            final EditText ingredientDetail = view.findViewById(R.id.customRecipeIngredientsDetail);

            //add functionality to add button
            ImageButton addBtn = view.findViewById(R.id.customRecipeIngredientsAddBtn);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserRecipeItemJoin joinItem = new UserRecipeItemJoin();
                    joinItem.itemId = getItemId(ingredientName.getText().toString());
                    joinItem.quantity = ingredientAmount.getText().toString();
                    joinItem.unit = unitAdapter.getItem(unitSpinner.getSelectedItemPosition());
                    joinItem.detail = ingredientDetail.getText().toString();
                    ingredientsList.add(joinItem);
                }
            });

            return view;
        }

        private int getItemId(String name) {
            ItemDao dao = new ItemDao(getContext());
            String stringId =  dao.getItemId(name);
            return Integer.parseInt(stringId);
        }

        private List<String> getItems() {
            List<String> names;
            ItemDao dao = new ItemDao(getContext());
            names = dao.getAllItemNames();
            return names;
        }

        private List<String> getUnits() {
            SharedPreferences userPreferences = getContext().getSharedPreferences(getContext().getPackageName() + "userPreferences", Context.MODE_PRIVATE);
            UnitDao dao = new UnitDao(getContext());
            return dao.getAllUnitNamesBySystemId(userPreferences.getString("unitSystemId", "1"));
        }
    }

    public static class ThirdPageFragment extends Fragment {
        private Integer steps = 0;
        private ArrayList<String> instructions = new ArrayList<>();

        public ThirdPageFragment() {
            //empty constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.custom_recipe_page_3, container, false);

            final TextView instructionsBox = view.findViewById(R.id.customRecipeInstructions);
            instructionsBox.setMovementMethod(new ScrollingMovementMethod());
            final TextInputEditText instructionEntry = view.findViewById(R.id.customRecipeInstructionsEntry);
            ImageButton addBtn = view.findViewById(R.id.customRecipeInstructionsAddBtn);
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
    }
}
