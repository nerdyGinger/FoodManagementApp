package apps.nerdyginger.pocketpantry.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.EmptyRecyclerView;
import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.RecyclerViewClickListener;
import apps.nerdyginger.pocketpantry.helpers.ImageHelper;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;

public class RecipesListAdapter extends EmptyRecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UserRecipeBoxItem> dataSet = new ArrayList<>();
    private RecyclerViewClickListener mListener;
    private boolean detailed = true;
    private ImageHelper imageHelper;

    public class SimpleRecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView recipeName;
        //ImageView statusBar;

        SimpleRecipeViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeNameSimple);
            //statusBar = itemView.findViewById(R.id.recipeIndicatorBarSimple);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onClick(view, getAdapterPosition());
            }
        }
    }

    public class DetailedRecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView recipeName, servings, category;
        ImageView image;
        //ImageView statusBar;

        DetailedRecipeViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeNameDetailed);
            servings = itemView.findViewById(R.id.recipeServingsDetailed);
            category = itemView.findViewById(R.id.recipeCategoryDetailed);
            image = itemView.findViewById(R.id.recipeImageDetailed);
            //statusBar = itemView.findViewById(R.id.recipeIndicatorBarDetailed);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onClick(view, getAdapterPosition());
            }
        }
    }

    public RecipesListAdapter() { } //empty constructor

    public RecipesListAdapter(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    public void filter(String text) {
        if ( ! text.isEmpty()) {
            List<UserRecipeBoxItem> dataCopy = dataSet;
            dataSet.clear();
            text = text.toLowerCase();
            for (UserRecipeBoxItem item : dataCopy) {
                if (item.getRecipeName().toLowerCase().contains(text)) {
                    dataSet.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(List<UserRecipeBoxItem> data) {
        if (data == null) {
            Log.e("-PRO TIPS!(and errors)-", "InventoryListAdapter data set was null");
            return;
        }
        dataSet = data;
        notifyDataSetChanged();
    }

    public UserRecipeBoxItem getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    public UserRecipeBoxItem deleteItem(int position) {
        UserRecipeBoxItem deletedItem = dataSet.get(position);
        int deletedPosition = position;
        dataSet.remove(position);
        notifyItemRemoved(position);
        //notifyItemRangeChanged(position, getItemCount());
        //showUndoSnackBar(); ??????
        return deletedItem;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        imageHelper = new ImageHelper(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == 1) {
            View view = inflater.inflate(R.layout.recipe_list_simple, parent, false);
            return new SimpleRecipeViewHolder(view);
        } else {
            //inflate other layout
            View view = inflater.inflate(R.layout.recipe_list_detailed, parent, false);
            return new DetailedRecipeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        UserRecipeBoxItem item = dataSet.get(position);
        if (viewHolder.getItemViewType() == 1) { // ---> viewing simple recipe list
            SimpleRecipeViewHolder simpleHolder = (SimpleRecipeViewHolder) viewHolder;
            TextView name = simpleHolder.recipeName;
            name.setText(item.getRecipeName());
        } else { // -----------------------------------> viewing detailed recipe list
            DetailedRecipeViewHolder detailHolder = (DetailedRecipeViewHolder) viewHolder;
            TextView name = detailHolder.recipeName;
            name.setText(item.getRecipeName());
            TextView servings = detailHolder.servings;
            servings.setText(item.getServings());
            TextView category = detailHolder.category;
            category.setText(item.getCategory());
            if ( ! item.isUserAdded()) { // read-only, set image
                Bitmap bmp = imageHelper.retrieveImage(imageHelper.getFilename(item));
                detailHolder.image.setImageBitmap(bmp);
            }
        }
    }

    @Override
    public int getItemCount() {
        try {
            return dataSet.size();
        } catch (Exception e){
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (detailed) {
            // user has set preference to detailed recipe list
            return 2;
        } else {
            return 1;
        }
    }

    public void setDetailedView(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        detailed = pref.getBoolean("recipeViewingMode", true);
    }
}