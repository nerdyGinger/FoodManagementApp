package apps.nerdyginger.pocketpantry.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.BrowseRecipeClickListener;
import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.RecyclerViewClickListener;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeItem;

/*
 * Child adapter for the nested RecyclerViews on the browse recipe page
 * Last edited: 3/13/20
 */
public class BrowseRecipesItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int DATA_VIEWTYPE = 1;
    private int ENDCAP_VIEWTYPE = 2;
    private List<BrowseRecipeItem> dataSet = new ArrayList<>();
    private RecyclerViewClickListener homeListener;
    private BrowseRecipeClickListener browseListener;
    private String MODE; //either "browse" or "home"

    public class RecipeCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name, category;
        ImageView image;
        CardView card;
        RecipeCardViewHolder(View view ){
            super(view);
            name = view.findViewById(R.id.recipeCardName);
            category = view.findViewById(R.id.recipeCardCategory);
            image = view.findViewById(R.id.recipeCardImage);
            card = view.findViewById(R.id.recipeCard);
            card.setOnClickListener(this);
            card.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (MODE.equals("home")) {
                homeListener.onClick(view, getAdapterPosition());
            } else {
                browseListener.onClick(getItemAtPosition(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (MODE.equals("home")) {
                homeListener.onLongClick(view, getAdapterPosition());
            } else {
                browseListener.onLongClick(getItemAtPosition(getAdapterPosition()));
            }
            return false;
        }
    }

    public class RecipeCardButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton btn;
        RecipeCardButtonViewHolder(View view) {
            super(view);
            btn = view.findViewById(R.id.recipeCardBtn);
            btn.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            homeListener.onClick(view, getAdapterPosition());
        }
    }

    public BrowseRecipesItemAdapter(String mode) {
        MODE = mode;
    }

    public BrowseRecipesItemAdapter(String mode, RecyclerViewClickListener listener) {
        homeListener = listener;
        MODE = "home";
    }

    public BrowseRecipesItemAdapter(BrowseRecipeClickListener listener) {
        browseListener = listener;
        MODE = "browse";
    }

    public BrowseRecipesItemAdapter(List<BrowseRecipeItem> data , String mode, RecyclerViewClickListener listener) {
        homeListener = listener;
        dataSet = data;
        MODE = "home";
    }

    public void setListener(RecyclerViewClickListener listener) {
        homeListener = listener;
    }

    public void updateData(List<BrowseRecipeItem> data) {
        dataSet = data;
        notifyDataSetChanged();
    }

    public BrowseRecipeItem getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    public BrowseRecipeItem deleteItem(int position) {
        BrowseRecipeItem deletedItem = dataSet.get(position);
        int deletedPosition = position;
        dataSet.remove(position);
        notifyItemRemoved(position);
        //notifyItemRangeChanged(position, getItemCount());
        //showUndoSnackBar(); ??????
        return deletedItem;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == DATA_VIEWTYPE) {
            view = inflater.inflate(R.layout.browse_recipes_card, parent, false);
            return new RecipeCardViewHolder(view);
        } else {
            //switch to image button
            view = inflater.inflate(R.layout.browse_recipes_image_btn, parent, false);
            return new RecipeCardButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == DATA_VIEWTYPE) {
            BrowseRecipeItem item = dataSet.get(position);
            RecipeCardViewHolder holder = (RecipeCardViewHolder) viewHolder;
            holder.name.setText(item.getRecipeName());
            holder.category.setText(item.getCategory());
            holder.image.setImageBitmap(item.getImage());
        } else {
            RecipeCardButtonViewHolder holder = (RecipeCardButtonViewHolder) viewHolder;
            if (MODE.equals("browse")) {
                //set image button src to ...
                holder.btn.setImageResource(R.drawable.ic_more_horiz_black_24dp);
            } else {
                //set image button src to +
                holder.btn.setImageResource(R.drawable.ic_add_black_24dp);
            }
        }
    }

    @Override
    public int getItemCount() {
        try {
            return dataSet.size() + 1;
        } catch (Exception e){
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == dataSet.size()) ? ENDCAP_VIEWTYPE : DATA_VIEWTYPE;
    }

}
