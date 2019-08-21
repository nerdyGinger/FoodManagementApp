package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.models.Unit;
import apps.nerdyginger.cleanplateclub.models.UserInventory;

public class InventoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RecyclerViewClickListener myListener;
    private List<UserInventory> dataSet = new ArrayList<>();
    private List<Unit> itemUnits = new ArrayList<>();
    private TextView itemName, itemQuantity, itemNameLifeBar, itemQuantityLifeBar;
    private ProgressBar lifeBar;
    private UserInventory deletedItem;
    private int deletedPosition;

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

    public class LifeBarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RecyclerViewClickListener rowListener;
        LifeBarViewHolder (View v, RecyclerViewClickListener listener) {
            super(v);
            itemNameLifeBar = v.findViewById(R.id.item_name_trackable);
            itemQuantityLifeBar = v.findViewById(R.id.item_quantity_trackable);
            lifeBar = v.findViewById(R.id.lifebar);
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
        setHasStableIds(true);
    }

    //empty constructor
    InventoryListAdapter() {}

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
                return new LifeBarViewHolder(view, myListener);
        }
        //shouldn't happen
        View view = LayoutInflater.from(context).inflate(R.layout.inventory_list_item, parent, false);
        return new RowViewHolder(view, myListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()) {
            case 1:
                RowViewHolder rowHolder = (RowViewHolder) holder;
                UserInventory item = dataSet.get(position);
                Unit unit = itemUnits.get(position);
                itemName.setText(item.getItemName());
                if (item.isQuantify()) { //check if item is actually quantified first
                    itemQuantity.setText(item.getQuantity() + " " +  unit.getAbbreviation());
                } else {                 //otherwise, quantity needs to be empty string
                    itemQuantity.setText("");
                }
                break;
            case 2:
                LifeBarViewHolder lifebarHolder = (LifeBarViewHolder) holder;
                UserInventory item2 = dataSet.get(position);
                Unit unit2 = itemUnits.get(position);
                itemNameLifeBar.setText(item2.getItemName());
                itemQuantityLifeBar.setText(item2.getQuantity() + " " + unit2.getAbbreviation());
                lifeBar.setMax(item2.getMaxQuantity());
                lifeBar.setProgress(item2.getQuantity());
                break;
        }
    }

    public void updateData(List<UserInventory> data, final Context context) {
        if (data == null) { return; }
        UnitDao unitDao = new UnitDao(context);
        dataSet.clear();
        itemUnits.clear();
        dataSet.addAll(data);
        for (int i=0; i<dataSet.size(); i++) {
            //Log.e("_IDs", String.valueOf(dataSet.get(i).get_ID()));
            itemUnits.add(unitDao.getUnitById(dataSet.get(i).getUnit()));
        }
        notifyDataSetChanged();
    }

    public UserInventory deleteItem(int position) {
        deletedItem = dataSet.get(position);
        deletedPosition = position;
        dataSet.remove(position);
        itemUnits.remove(position);
        //notifyDataSetChanged();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
        //showUndoSnackBar();
        return deletedItem;
    }
/*
    private void showUndoSnackBar() {
        View view = .findViewById(R.id.coo rdinator_layout);
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo delete item?", v -> undoDelete());
        snackbar.show();
    }*/

    private void undoDelete() {
        dataSet.add(deletedPosition, deletedItem);
        //add back in the unit, as well
        notifyItemInserted(deletedPosition);
        notifyItemRangeChanged(deletedPosition, getItemCount());
    }

    public UserInventory getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    @Override
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
