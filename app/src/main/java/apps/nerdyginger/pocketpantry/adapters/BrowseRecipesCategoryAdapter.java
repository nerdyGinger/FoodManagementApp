package apps.nerdyginger.pocketpantry.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.BrowseRecipeClickListener;
import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeCategory;

/*
 * Parent adapter for the nested RecyclerViews on the browser recipe page
 * Last edited: 3/13/20
 */
public class BrowseRecipesCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BrowseRecipeCategory> dataSet = new ArrayList<>();
    private BrowseRecipeClickListener childListener;

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        RecyclerView childRv;
        CategoryViewHolder(View view) {
            super(view);
            categoryName = view.findViewById(R.id.browseRecipesCategoryName);
            childRv = view.findViewById(R.id.browseRecipesCategoryRecycler);
        }
    }

    public void setChildListener(BrowseRecipeClickListener childListener) {
        this.childListener = childListener;
    }

    public BrowseRecipesCategoryAdapter() { }

    public void updateData(List<BrowseRecipeCategory> data) {
        dataSet = data;
    }

    public BrowseRecipeCategory getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.browse_recipes_category_list, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        BrowseRecipeCategory item = dataSet.get(position);
        final CategoryViewHolder holder = (CategoryViewHolder) viewHolder;
        holder.categoryName.setText(item.getCategoryName());

        //set up child RecyclerViews
        LinearLayoutManager childLlm = new LinearLayoutManager(holder.childRv.getContext(), LinearLayoutManager.HORIZONTAL, false);
        childLlm.setInitialPrefetchItemCount(4);
        holder.childRv.setLayoutManager(childLlm);
        final BrowseRecipesItemAdapter childAdapter = new BrowseRecipesItemAdapter(childListener);
        childAdapter.updateData(item.getRecipeCards());
        holder.childRv.setAdapter(childAdapter);
    }

    @Override
    public int getItemCount() {
        try {
            return dataSet.size();
        } catch (Exception e){
            return 0;
        }
    }
}
