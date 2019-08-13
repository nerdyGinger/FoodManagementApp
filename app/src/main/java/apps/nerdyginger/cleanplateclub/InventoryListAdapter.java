package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserInventory;

public class InventoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RecyclerViewClickListener myListener;
    private List<UserInventory> dataSet = new ArrayList<>();
    private TextView itemName, itemQuantity, itemNameLifebar, itemQuantityLifebar;
    private ProgressBar lifebar;

    public class RowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RecyclerViewClickListener rowListener;
        RowViewHolder  (View v, RecyclerViewClickListener listener) {
            super(v);
            itemName = v.findViewById(R.id.item_name);
            itemQuantity = v.findViewById(R.id.item_quantity);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            myListener.onClick(view, getAdapterPosition());
        }
    }

    public class LifebarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RecyclerViewClickListener rowListener;
        LifebarViewHolder (View v, RecyclerViewClickListener listener) {
            super(v);
            itemNameLifebar = v.findViewById(R.id.item_name_trackable);
            itemQuantityLifebar = v.findViewById(R.id.item_quantity_trackable);
            lifebar = v.findViewById(R.id.lifebar);
            myListener = listener;
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            myListener.onClick(view, getAdapterPosition());
        }
    }

    InventoryListAdapter(RecyclerViewClickListener listener) {
        myListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case 1:
                View v = LayoutInflater.from(context).inflate(R.layout.inventory_list_item, parent, false);
                return new RowViewHolder(v, myListener);
            case 2:
                View view = LayoutInflater.from(context).inflate(R.layout.inventory_list_trackable, parent, false);
                return new LifebarViewHolder(view, myListener);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.inventory_list_item, parent, false);
        return new RowViewHolder(view, myListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()) {
            case 1:
                RowViewHolder rowHolder = (RowViewHolder) holder;
                UserInventory item = dataSet.get(position);
                itemName.setText(item.getItemName());
                if (item.isQuantify()) { //check if item is actually quantified first
                    itemQuantity.setText(item.getQuantity() + " " +  item.getUnit());
                } else {                 //otherwise, quantity needs to be empty string
                    itemQuantity.setText("");
                }
                break;
            case 2:
                LifebarViewHolder lifebarHolder = (LifebarViewHolder) holder;
                UserInventory item2 = dataSet.get(position);
                itemNameLifebar.setText(item2.getItemName());
                itemQuantityLifebar.setText(item2.getQuantity() + " " + item2.getUnit());
                lifebar.setMax(item2.getMaxQuantity());
                lifebar.setProgress(item2.getQuantity());
        }
    }

    public void updateData(List<UserInventory> data) {
        dataSet.clear();
        dataSet.addAll(data);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        UserInventory item = dataSet.get(position);
        if (item.isMultiUnit()) {
            //is quantified with target max
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
