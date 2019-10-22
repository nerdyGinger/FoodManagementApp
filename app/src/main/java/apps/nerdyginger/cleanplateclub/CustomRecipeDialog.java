package apps.nerdyginger.cleanplateclub;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

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
            }
            //continue ifs for other pages
            Fragment f = new FirstPageFragment();
            return f;
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
            //handle view as needed
            return view;
        }
    }
}
