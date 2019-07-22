package apps.nerdyginger.cleanplateclub;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.models.Recipe;
import apps.nerdyginger.cleanplateclub.models.UserRecipe;

public class RecipesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    RecyclerViewClickListener myListener;
    boolean detailedList;
    private List<Recipe> readOnlyDataSet = new ArrayList<>();
    private List<UserRecipe> customDataSet = new ArrayList<>();
    private TextView recipeNameSimple, recipeNameDetailed, recipeServingsDetailed, recipeCategoryDetailed;

    public RecipesListAdapter(RecyclerViewClickListener listener, boolean detailed) {
        myListener = listener;
        detailedList = detailed;
    }

    public class SimpleRowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RecyclerViewClickListener rowListener;
        SimpleRowViewHolder  (View v, RecyclerViewClickListener listener) {
            super(v);
            recipeNameSimple = v.findViewById(R.id.recipeNameSimple);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            myListener.onClick(view, getAdapterPosition());
        }
    }

    public class DetailedRowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RecyclerViewClickListener rowListener;
        DetailedRowViewHolder (View v, RecyclerViewClickListener listener) {
            super(v);
            recipeNameDetailed = v.findViewById(R.id.recipeNameDetailed);
            recipeServingsDetailed = v.findViewById(R.id.recipeServingsDetailed);
            recipeCategoryDetailed = v.findViewById(R.id.recipeCategoryDetailed);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            myListener.onClick(view, getAdapterPosition());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case 1:
                View v = LayoutInflater.from(context).inflate(R.layout.recipe_list_simple, parent, false);
                return new SimpleRowViewHolder(v, myListener);
            case 2:
                View view = LayoutInflater.from(context).inflate(R.layout.recipe_list_detailed, parent, false);
                return new DetailedRowViewHolder(view, myListener);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_list_simple, parent, false);
        return new SimpleRowViewHolder(view, myListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isCustomRecipe(position)) {
            UserRecipe recipe = customDataSet.get(position);
            switch(holder.getItemViewType()) {
                case 1:
                    SimpleRowViewHolder simpleRowViewHolder = (SimpleRowViewHolder) holder;
                    recipeNameSimple.setText(recipe.getName());
                    break;
                case 2:
                    DetailedRowViewHolder detailedRowViewHolder = (DetailedRowViewHolder) holder;
                    recipeNameDetailed.setText(recipe.getName());
                    recipeServingsDetailed.setText(recipe.getRecipeYield());
                    recipeCategoryDetailed.setText(recipe.getRecipeCategory());
            }
        }
        else {
            Recipe recipe = readOnlyDataSet.get(position-getCustomItemCount());
            switch(holder.getItemViewType()) {
                case 1:
                    SimpleRowViewHolder simpleRowViewHolder = (SimpleRowViewHolder) holder;
                    recipeNameSimple.setText(recipe.getName());
                    break;
                case 2:
                    DetailedRowViewHolder detailedRowViewHolder = (DetailedRowViewHolder) holder;
                    recipeNameDetailed.setText(recipe.getName());
                    recipeServingsDetailed.setText(recipe.getRecipeYield());
                    recipeCategoryDetailed.setText(recipe.getRecipeCategory());
            }
        }
    }

    public void updateReadOnlyData(List<Recipe> data) {
        readOnlyDataSet.clear();
        readOnlyDataSet.addAll(data);
        notifyDataSetChanged();
    }

    public void updateCustomData(List<UserRecipe> data) {
        customDataSet.clear();
        customDataSet.addAll(data);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return customDataSet.size() + readOnlyDataSet.size();
    }

    public int getCustomItemCount() {
        return customDataSet.size();
    }

    public int getReadOnlyItemCount() {
        return readOnlyDataSet.size();
    }

    public boolean isCustomRecipe(int position) {
        return (position < getCustomItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        if (detailedList) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
