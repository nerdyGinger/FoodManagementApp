package apps.nerdyginger.cleanplateclub;

import android.app.Dialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        View rootView = inflater.inflate(R.layout.add_custom_recipe, container, false);
        //View parentView = inflater.inflate(R.layout.add_custom_recipe, container, false);
        pager = (ViewPager) rootView.findViewById(R.id.customRecipeViewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);
        return rootView;
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
        public ScreenSlidePagerAdapter (FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return new CustomRecipeDialog();
        }

        @Override
        public int getCount() {
            return pages;
        }
    }
}
