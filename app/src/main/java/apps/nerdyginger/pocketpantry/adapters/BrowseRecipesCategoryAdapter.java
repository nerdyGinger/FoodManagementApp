package apps.nerdyginger.pocketpantry.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.RecyclerViewClickListener;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeCategory;

public class BrowseRecipesCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BrowseRecipeCategory> dataSet = new ArrayList<>();
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        RecyclerView childRv;
        CategoryViewHolder(View view) {
            super(view);
            categoryName = view.findViewById(R.id.browseRecipesCategoryName);
            childRv = view.findViewById(R.id.browseRecipesCategoryRecycler);
        }
    }

    public BrowseRecipesCategoryAdapter() { }

    public  BrowseRecipesCategoryAdapter(List<BrowseRecipeCategory> data) {
        dataSet = data;
    }

    public void updateData(List<BrowseRecipeCategory> data) {
        dataSet = data;
    }

    public BrowseRecipeCategory getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.browse_recipes_category_list, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BrowseRecipeCategory item = dataSet.get(position);
        CategoryViewHolder holder = (CategoryViewHolder) viewHolder;
        holder.categoryName.setText(item.getCategoryName());

        //set up child RecyclerViews
        LinearLayoutManager childLlm = new LinearLayoutManager(holder.childRv.getContext(), LinearLayoutManager.HORIZONTAL, false);
        childLlm.setInitialPrefetchItemCount(4);
        holder.childRv.setLayoutManager(childLlm);
        holder.childRv.setAdapter(new BrowseRecipesItemAdapter(item.getRecipeCards(), "browse", new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //do something
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        }));
        holder.childRv.setRecycledViewPool(viewPool);
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
