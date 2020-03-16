package apps.nerdyginger.pocketpantry.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apps.nerdyginger.pocketpantry.EmptyRecyclerView;
import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.RecyclerViewClickListener;
import apps.nerdyginger.pocketpantry.models.UserListItem;

import static android.view.View.GONE;

// A checkable, sortable, expandable grocery list adapter
// Last edited: 3/3/2020
public class ListsAdapter extends  EmptyRecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UserListItem> dataSet = new ArrayList<>();
    private RecyclerViewClickListener mListener;
    private boolean locked;

    public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox checkBox;
        TextView itemName, quantity, noteIndicator, notes;

        ListItemViewHolder(final View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.listCheck);
            itemName = itemView.findViewById(R.id.listItemName);
            quantity = itemView.findViewById(R.id.listItemQuantity);
            noteIndicator = itemView.findViewById(R.id.listNoteIndicator);
            notes = itemView.findViewById(R.id.listNote);
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
            if (dataSet.get(getAdapterPosition()).getNotes().isEmpty()) {
                mListener.onClick(view, getAdapterPosition());
            } else {
                toggleExpansion(dataSet.get(getAdapterPosition()), this);
            }
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

    public void orderItemsByPosition() {
        Collections.sort(dataSet);
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

        // add checked items to bottom of unchecked list, updates position data, reload data set
        unchecked.addAll(checked);
        for (int i=0; i<unchecked.size(); i++) {
            unchecked.get(i).setPosition(i);
        }
        updateData(unchecked);
    }

    // toggle the expansion of the note section, for user notes on grocery list items,
    // i.e. "Market Pantry brand", or "Don't get Fat-free, it gives Karen gas!" or
    // other things like that
    private void toggleExpansion(UserListItem item, ListItemViewHolder holder) {
        holder.notes.setVisibility(item.isExpanded() ? View.GONE : View.VISIBLE);
        item.setExpanded( ! item.isExpanded());
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    public boolean isLocked() {
        return locked;
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
        holder.notes.setText(item.getNotes());
        holder.checkBox.setChecked(item.isChecked());
        if (item.getNotes().isEmpty()) {
            holder.noteIndicator.setText("   ");
        } else {
            holder.noteIndicator.setText("...");
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
        return 1;
    }

}
