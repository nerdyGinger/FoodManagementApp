package apps.nerdyginger.cleanplateclub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.R;

public class BrowseRecipesItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RecyclerView> dataSet = new ArrayList<>(); //TODO: UPDATE DATA TYPE HERE!!!

    public class RecipeCardViewHolder extends RecyclerView.ViewHolder {
        RecipeCardViewHolder(View view ){
            super(view);
        }
    }

    public BrowseRecipesItemAdapter() { }

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
        View view = inflater.inflate(R.layout.browse_recipes_card, parent, false);
        return new RecipeCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        //get data at position
        //set view values
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
