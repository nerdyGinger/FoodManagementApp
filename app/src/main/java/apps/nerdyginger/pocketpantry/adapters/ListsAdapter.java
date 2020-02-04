package apps.nerdyginger.pocketpantry.adapters;

import android.app.LauncherActivity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.EmptyRecyclerView;
import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.RecyclerViewClickListener;
import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.models.UserListItem;

public class ListsAdapter extends  EmptyRecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UserListItem> dataSet = new ArrayList<>();
    private RecyclerViewClickListener mListener;

    public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox checkBox;
        TextView itemName, quantity;

        ListItemViewHolder(final View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.listCheck);
            itemName = itemView.findViewById(R.id.listItemName);
            quantity = itemView.findViewById(R.id.listItemQuantity);
            itemView.setOnClickListener(this);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //UserListItem clicked = getItemAtPosition(getAdapterPosition());
                    //clicked.setChecked( ! clicked.isChecked());
                    mListener.onClick(itemView, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public ListsAdapter() {
        //empty constructor
    }

    public ListsAdapter(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    public void updateData(List<UserListItem> data) {
        if (data == null) {
            Log.e("-PRO TIPS!(and errors)-", "ListsAdapter data set was null");
            return;
        }
        dataSet = data;
        notifyDataSetChanged();
    }

    public void sortData() {
        List<UserListItem> unchecked = new ArrayList<>();
        List<UserListItem> checked = new ArrayList<>();

        for (int i=0; i<dataSet.size(); i++) {
            if (dataSet.get(i).isChecked()) {
                checked.add(dataSet.get(i));
            } else {
                unchecked.add(dataSet.get(i));
            }
        }

        // add checked items to bottom of unchecked list, reload data set
        unchecked.addAll(checked);
        updateData(unchecked);
    }

    public UserListItem getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    public UserListItem deleteItem(int position) {
        UserListItem deletedItem = dataSet.get(position);
        dataSet.remove(position);
        notifyDataSetChanged();
        return deletedItem;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ListItemViewHolder(inflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        UserListItem item = dataSet.get(position);
        ListItemViewHolder holder = (ListItemViewHolder) viewHolder;
        holder.itemName.setText(item.getItemName());
        holder.quantity.setText(item.getQuantity());
        holder.checkBox.setChecked(item.isChecked());
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
        return 1;
    }

}
