package apps.nerdyginger.cleanplateclub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.R;

public class BrowseRecipesCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RecyclerView> dataSet = new ArrayList<>();

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        RecyclerView rv;
        CategoryViewHolder(View view) {
            super(view);
            categoryName = view.findViewById(R.id.browseRecipesCategoryName);
            rv = view.findViewById(R.id.browseRecipesCategoryRecycler);
        }
    }

    public BrowseRecipesCategoryAdapter() { }

    public void updateData(List<RecyclerView> data) {
        dataSet = data;
    }

    public RecyclerView getItemAtPosition(int position) {
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
        RecyclerView item = dataSet.get(position);
        CategoryViewHolder holder = (CategoryViewHolder) viewHolder;
        holder.rv = item; //item.getRecyclerView(); // <--- ???
        //holder.categoryName = item.getCategoryName();
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
