package apps.nerdyginger.cleanplateclub.adapters;

import android.content.Context;
import android.media.ImageWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.R;
import apps.nerdyginger.cleanplateclub.models.BrowseRecipeItem;

public class BrowseRecipesItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BrowseRecipeItem> dataSet = new ArrayList<>();

    public class RecipeCardViewHolder extends RecyclerView.ViewHolder {
        TextView name, category;
        ImageView image;
        RecipeCardViewHolder(View view ){
            super(view);
            name = view.findViewById(R.id.recipeCardName);
            category = view.findViewById(R.id.recipeCardCategory);
            image = view.findViewById(R.id.recipeCardImage);
        }
    }

    public BrowseRecipesItemAdapter() { }

    public BrowseRecipesItemAdapter(List<BrowseRecipeItem> data) {
        dataSet = data;
    }

    public void updateData(List<BrowseRecipeItem> data) {
        dataSet = data;
    }

    public BrowseRecipeItem getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.browse_recipes_card, parent, false);
        return new RecipeCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BrowseRecipeItem item = dataSet.get(position);
        RecipeCardViewHolder holder = (RecipeCardViewHolder) viewHolder;
        holder.name.setText(item.getRecipeName());
        holder.category.setText(item.getRecipeCategory());
        //holder.image.<setSrc>(item.getRecipeImage()); // figure out logistics of images later
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
