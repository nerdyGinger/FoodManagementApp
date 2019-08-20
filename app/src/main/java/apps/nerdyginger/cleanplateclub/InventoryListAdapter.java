package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.models.Unit;
import apps.nerdyginger.cleanplateclub.models.UserInventory;
import apps.nerdyginger.cleanplateclub.wrappers.InventoryItemWrapper;

public class InventoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RecyclerViewClickListener myListener;
    private List<UserInventory> dataSet = new ArrayList<>();
    private List<Unit> itemUnits = new ArrayList<>();
    private TextView itemName, itemQuantity, itemNameLifebar, itemQuantityLifebar;
    private ProgressBar lifebar;
    private UserInventory deletedItem;
    private int deletedPosition;
    private Context mContext;

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
                LifebarViewHolder lifebarHolder = (LifebarViewHolder) holder;
                UserInventory item2 = dataSet.get(position);
                Unit unit2 = itemUnits.get(position);
                itemNameLifebar.setText(item2.getItemName());
                itemQuantityLifebar.setText(item2.getQuantity() + " " + unit2.getAbbreviation());
                lifebar.setMax(item2.getMaxQuantity());
                lifebar.setProgress(item2.getQuantity());
        }
    }

    public void updateData(List<UserInventory> data, final Context context) {
        mContext = context;
        UnitDao unitDao = new UnitDao(context);
        dataSet.clear();
        dataSet.addAll(data);
        notifyDataSetChanged();
        for (int i=0; i<dataSet.size(); i++) {
            Log.e("_IDs", String.valueOf(dataSet.get(i).get_ID()));
            itemUnits.add(unitDao.getUnitById(dataSet.get(i).getUnit()));
        }
    }

    public UserInventory deleteItem(int position) {
        deletedItem = dataSet.get(position);
        deletedPosition = position;
        dataSet.remove(position);
        itemUnits.remove(position);

        notifyItemRemoved(position);
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
        notifyItemInserted(deletedPosition);
    }

    public Context getContext() {
        return mContext;
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
