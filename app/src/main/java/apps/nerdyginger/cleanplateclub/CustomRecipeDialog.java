package apps.nerdyginger.cleanplateclub;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import apps.nerdyginger.cleanplateclub.dao.CategoryDao;
import apps.nerdyginger.cleanplateclub.dao.CuisineDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;

/*
 * This is the dialog for custom recipe input. It must be beautiful, elegant, and absolutely dreamy.
 * Last Edited: 10/18/19
 */
public class CustomRecipeDialog extends DialogFragment {
    private static final int pages = 3; // Slide-able pages for basic info, ingredients, and instructions
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.custom_recipe_page_1, container, false);
        View parentView = inflater.inflate(R.layout.add_custom_recipe, container, false);
        pager = (ViewPager) parentView.findViewById(R.id.customRecipeViewPager);
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

        Button nextBtn = view.findViewById(R.id.customRecipeSaveBtn);
        if (pager.getCurrentItem() == 2) {
            nextBtn.setText("Save");
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //perform save operation
                    dismiss();
                }
            });
        } else {
            nextBtn.setText("Next");
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                }
            });
        }
    }

    // Adds a new to chip to the input chip group (recipe keywords)
    private Chip getChip(final ChipGroup entryChipGroup, String text) {
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
                //return first page
                Fragment f = new FirstPageFragment();
                return f;
            } else if (position == 1) {
                return new SecondPageFragment();
            } else {
                //defaults to third page
                return new ThirdPageFragment();
            }
        }

        @Override
        public int getCount() {
            return pages;
        }

    }

    public static class FirstPageFragment extends Fragment {

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
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getCategories());
            categorySpinner.setAdapter(categoryAdapter);

            //add cuisines to spinner
            Spinner cuisineSpinner = view.findViewById(R.id.customRecipeCuisine);
            ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getCuisines());
            cuisineSpinner.setAdapter(cuisineAdapter);

            return view;
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
            //manipulate views as needed
            Spinner unitSpinner = view.findViewById(R.id.customRecipeIngredientsUnit);
            ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getUnits());
            unitSpinner.setAdapter(unitAdapter);
            return view;
        }

        private List<String> getUnits() {
            SharedPreferences userPreferences = getContext().getSharedPreferences(getContext().getPackageName() + "userPreferences", Context.MODE_PRIVATE);
            UnitDao dao = new UnitDao(getContext());
            return dao.getAllUnitNamesBySystemId(userPreferences.getString("unitSystemId", "1"));
        }
    }

    public static class ThirdPageFragment extends Fragment {

        public ThirdPageFragment() {
            //empty constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.custom_recipe_page_2, container, false);
            //manipulate views as needed

            return view;
        }
    }
}
