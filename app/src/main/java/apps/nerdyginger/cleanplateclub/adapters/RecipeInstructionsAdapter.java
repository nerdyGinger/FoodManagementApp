package apps.nerdyginger.cleanplateclub.adapters;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.R;
import apps.nerdyginger.cleanplateclub.RecyclerViewClickListener;
import apps.nerdyginger.cleanplateclub.models.UserRecipeBoxItem;
import apps.nerdyginger.cleanplateclub.view_models.RecipeInstructionsViewModel;

// Some help on RecyclerView expansion from this article, thanks!
// https://medium.com/@nikola.jakshic/how-to-expand-collapse-items-in-recyclerview-49a648a403a6
//
// Last edited: 11/6/19
public class RecipeInstructionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RecipeInstructionsViewModel> dataSet = new ArrayList<>();
    private RecyclerViewClickListener mListener;
    public boolean isClickable;

    public class RecipeInstructionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView stepPreview;
        private EditText editStep;
        private TextInputLayout editLayoutHolder;
        private ImageButton addBtn;
        private ImageButton deleteBtn;
        private View itemView;

        RecipeInstructionsViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            stepPreview = itemView.findViewById(R.id.customRecipeInstructionsItemPreview);
            editStep = itemView.findViewById(R.id.customRecipeInstructionsEntry);
            editLayoutHolder = itemView.findViewById(R.id.customRecipeInstructionsEntryLayout);
            addBtn = itemView.findViewById(R.id.customRecipeInstructionsAddBtn);
            deleteBtn = itemView.findViewById(R.id.customRecipeInstructionsDeleteBtn);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemAtPosition(getAdapterPosition()).setInstructionText(editStep.getText().toString());
                    getItemAtPosition(getAdapterPosition()).setExpanded(false);
                    notifyItemChanged(getAdapterPosition());
                    if (getAdapterPosition() == getItemCount() - 1) {
                        //last item just added
                        dataSet.add(new RecipeInstructionsViewModel("", true));
                        notifyDataSetChanged();
                    }
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public RecipeInstructionsAdapter() {
        //empty constructor
    }

    public RecipeInstructionsAdapter(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    public void updateData(List<RecipeInstructionsViewModel> data) {
        if (data == null) {
            Log.e("-PRO TIPS!(and errors)-", "InventoryListAdapter data set was null");
            return;
        }
        dataSet.clear();
        dataSet.addAll(data);
        notifyDataSetChanged();
    }

    public RecipeInstructionsViewModel getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    public RecipeInstructionsViewModel deleteItem(int position) {
        RecipeInstructionsViewModel deletedItem = dataSet.get(position);
        dataSet.remove(position);
        notifyItemRemoved(position);
        return deletedItem;
    }

    private void toggleExpansion(RecipeInstructionsViewModel item, RecipeInstructionsViewHolder holder) {
        boolean expanded = item.isExpanded();
        holder.editStep.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.editLayoutHolder.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.stepPreview.setVisibility(expanded ? View.GONE : View.VISIBLE);
        holder.stepPreview.setText(item.getInstructionText());
        holder.editStep.setText(item.getInstructionText());
        if (isClickable) {
            holder.addBtn.setVisibility(expanded ? View.VISIBLE : View.GONE);
            holder.deleteBtn.setVisibility(expanded ? View.VISIBLE : View.GONE);
        } else {
            holder.addBtn.setVisibility(View.GONE);
            holder.deleteBtn.setVisibility(View.GONE);
            holder.editStep.setEnabled(false);
            holder.editStep.setFocusable(false);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recipe_instructions_item, parent, false);
        return new RecipeInstructionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final RecipeInstructionsViewModel item = getItemAtPosition(position);
        final RecipeInstructionsViewHolder holder = (RecipeInstructionsViewHolder) viewHolder;
        toggleExpansion(item, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setExpanded( ! item.isExpanded());
                if (isClickable) {
                    item.setInstructionText(holder.editStep.getText().toString());
                } else {
                    holder.editStep.setEnabled(false);
                }
                notifyItemChanged(position);
                for (int i=0; i<getItemCount(); i++) {
                    if (i != position) {
                        getItemAtPosition(i).setExpanded(false);
                        notifyItemChanged(i);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        try {
            return dataSet.size();
        } catch (Exception e) {
            return 0; //will return 0 if dataSet is null
        }
    }


}
