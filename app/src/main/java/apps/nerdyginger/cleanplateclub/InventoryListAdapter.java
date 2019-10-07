package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserInventoryItem;

public class InventoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UserInventoryItem> dataSet = new ArrayList<>();
    private RecyclerViewClickListener mListener;

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemName, itemQuantity;

        SimpleItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public class LifeBarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemName, itemQuantity;
        ProgressBar lifeBar;

        LifeBarViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name_trackable);
            itemQuantity = itemView.findViewById(R.id.item_quantity_trackable);
            lifeBar = itemView.findViewById(R.id.lifebar);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    InventoryListAdapter() { }

    InventoryListAdapter(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    void updateData(List<UserInventoryItem> data) {
        if (data == null) {
            Log.e("-PRO TIPS!(and errors)-", "InventoryListAdapter data set was null");
            return;
        }
        dataSet.clear();
        dataSet.addAll(data);
        notifyDataSetChanged();
    }

    UserInventoryItem getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    UserInventoryItem deleteItem(int position) {
        UserInventoryItem deletedItem = dataSet.get(position);
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

        if (viewType == 1) {
            View view = inflater.inflate(R.layout.inventory_list_simple, parent, false);
            return new SimpleItemViewHolder(view);
        } else {
            //inflate other layout
            View view = inflater.inflate(R.layout.inventory_list_trackable, parent, false);
            return new LifeBarViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        UserInventoryItem item = dataSet.get(position);
        if (viewHolder.getItemViewType() == 1) { // ---> item is not being tracked
            SimpleItemViewHolder simpleHolder = (SimpleItemViewHolder) viewHolder;
            TextView name = simpleHolder.itemName;
            name.setText(item.getItemName());
            TextView quantity = simpleHolder.itemQuantity;
            if (item.isQuantify()) {    //check if item is quantified
                quantity.setText(item.getQuantity() + " "); //TODO: add unit tracking
            } else {
                quantity.setText("");
            }
        } else { // -----------------------------------> user is tracking quantity
            LifeBarViewHolder detailHolder = (LifeBarViewHolder) viewHolder;
            TextView name = detailHolder.itemName;
            name.setText(item.getItemName());
            TextView quantity = detailHolder.itemQuantity;
            quantity.setText(item.getQuantity() + " "); //TODO: add unit tracking
            ProgressBar lifeBar = detailHolder.lifeBar;
            lifeBar.setMax(item.getMaxQuantity());
            lifeBar.setProgress(item.getQuantity());
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
        UserInventoryItem item = dataSet.get(position);
        if (item.isMultiUnit()) {
            // user is tracking quantities (has target max)
            return 2;
        } else {
            return 1;
        }
    }
}