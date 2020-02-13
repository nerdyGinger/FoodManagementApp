package apps.nerdyginger.pocketpantry.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.EmptyRecyclerView;
import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.models.HistoryComboItem;

public class HistoryListAdapter extends EmptyRecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<HistoryComboItem> dataSet = new ArrayList<>();

    public class HistorySectionViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public HistorySectionViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.historySectionTitle);
        }
    }

    public class HistoryItemViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName, completedDate;

        public HistoryItemViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.historyListItemRecipeName);
            completedDate = itemView.findViewById(R.id.historyListItemDate);
        }
    }

    public HistoryListAdapter() {
        //empty constructor
    }

    public void updateData(List<HistoryComboItem> data) {
        if (data == null) {
            Log.e("-PRO TIPS!(and errors)-", "InventoryListAdapter data set was null");
            return;
        }
        dataSet = data;
        notifyDataSetChanged();
    }

    public HistoryComboItem getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    public HistoryComboItem deleteItem(int position) {
        HistoryComboItem deletedItem = dataSet.get(position);
        int deletedPosition = position;
        dataSet.remove(position);
        notifyItemRemoved(position);
        return deletedItem;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == 1) {
            View view = inflater.inflate(R.layout.history_section_header, parent, false);
            return new HistorySectionViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.history_list_item, parent, false);
            return new HistoryItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        HistoryComboItem item = dataSet.get(position);
        if (getItemViewType(position) == 1) {
            HistorySectionViewHolder sectionHolder = (HistorySectionViewHolder) viewHolder;
            sectionHolder.title.setText(item.getSectionDateRange());
        } else {
            HistoryItemViewHolder dataHolder = (HistoryItemViewHolder) viewHolder;
            dataHolder.recipeName.setText(item.getRecipeName());
            dataHolder.completedDate.setText(item.getCompletedDate());
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
        if (dataSet.get(position).isSectionHeader()) {
            return 1;
        } else {
            return 2;
        }
    }

}
