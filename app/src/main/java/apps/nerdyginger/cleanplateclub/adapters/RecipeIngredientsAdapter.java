package apps.nerdyginger.cleanplateclub.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import apps.nerdyginger.cleanplateclub.R;
import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.view_models.RecipeIngredientsViewModel;


// Adapter for the recipe dialog ingredients. Expands to allow better viewing and/or editing
// of ingredients.
//
// Last edited: 11/7/19
public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RecipeIngredientsViewModel> dataSet = new ArrayList<>();
    public boolean isClickable;
    ArrayAdapter<String> unitAdapter;

    public class RecipeIngredientsViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private LinearLayout labelsContainer;
        private RelativeLayout entryBoxesContainer;
        private TextView nameLabel;
        private TextView detailLabel;
        private TextView amountLabel;
        private TextView unitLabel;
        private AutoCompleteTextView itemName;
        private EditText detail;
        private EditText amount;
        private Spinner unit;
        private ImageButton addBtn;
        private ImageButton deleteBtn;

        RecipeIngredientsViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            labelsContainer = itemView.findViewById(R.id.customRecipeIngredientsLabels);
            entryBoxesContainer = itemView.findViewById(R.id.customRecipeIngredientsEntryBoxes);
            nameLabel = itemView.findViewById(R.id.customRecipeIngredientsNameLabel);
            detailLabel = itemView.findViewById(R.id.customRecipeIngredientsDetailLabel);
            amountLabel = itemView.findViewById(R.id.customRecipeIngredientsAmountLabel);
            unitLabel = itemView.findViewById(R.id.customRecipeIngredientsUnitLabel);
            itemName = itemView.findViewById(R.id.customRecipeIngredientName);
            detail = itemView.findViewById(R.id.customRecipeIngredientsDetail);
            amount = itemView.findViewById(R.id.customRecipeIngredientsAmount);
            unit = itemView.findViewById(R.id.customRecipeIngredientsUnit);
            addBtn = itemView.findViewById(R.id.customRecipeIngredientsAddBtn);
            deleteBtn = itemView.findViewById(R.id.customRecipeIngredientsDeleteBtn);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemAtPosition(getAdapterPosition()).setItemName(itemName.getText().toString());
                    getItemAtPosition(getAdapterPosition()).setDetail(detail.getText().toString());
                    getItemAtPosition(getAdapterPosition()).setAmount(amount.getText().toString());
                    getItemAtPosition(getAdapterPosition()).setUnit(unit.getSelectedItem().toString());
                    getItemAtPosition(getAdapterPosition()).setExpanded(false);
                    notifyItemChanged(getAdapterPosition());
                    if (getAdapterPosition() == getItemCount() - 1) {
                        //last item just added
                        dataSet.add(new RecipeIngredientsViewModel(true, "", "", "", ""));
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
        }
    }

    public RecipeIngredientsAdapter() {
        //empty constructor
    }

    public void updateData(List<RecipeIngredientsViewModel> data) {
        if (data == null) {
            Log.e("-PRO TIPS!(and errors)-", "InventoryListAdapter data set was null");
            return;
        }
        dataSet.clear();
        dataSet.addAll(data);
        notifyDataSetChanged();
    }

    public RecipeIngredientsViewModel getItemAtPosition(int position) {
        return dataSet.get(position);
    }

    public RecipeIngredientsViewModel deleteItem(int position) {
        RecipeIngredientsViewModel deletedItem = dataSet.get(position);
        dataSet.remove(position);
        notifyItemRemoved(position);
        return deletedItem;
    }

    private void toggleExpansion(RecipeIngredientsViewModel item, RecipeIngredientsViewHolder holder) {
        boolean expanded = item.isExpanded();
        holder.entryBoxesContainer.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.labelsContainer.setVisibility(expanded ? View.GONE : View.VISIBLE);
        if (isClickable) {
            holder.addBtn.setVisibility(expanded ? View.VISIBLE : View.GONE);
            holder.deleteBtn.setVisibility(expanded ? View.VISIBLE : View.GONE);
        } else {
            holder.addBtn.setVisibility(View.GONE);
            holder.deleteBtn.setVisibility(View.GONE);
            holder.itemName.setEnabled(false);
            holder.itemName.setFocusable(false);
            holder.detail.setEnabled(false);
            holder.amount.setEnabled(false);
            holder.unit.setEnabled(false);
        }
        //set the text for labels and edit fields
        holder.nameLabel.setText(item.getItemName());
        holder.itemName.setText(item.getItemName());
        holder.detailLabel.setText(item.getDetail());
        holder.detail.setText(item.getDetail());
        holder.amountLabel.setText(item.getAmount());
        holder.amount.setText(item.getAmount());
        holder.unitLabel.setText(item.getUnit().equals("Unit") ? "" : item.getUnit());
        holder.unit.setSelection(unitAdapter.getPosition(item.getUnit().equals("") ? "Unit" : item.getUnit()));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recipe_ingredients_item, parent, false);
        RecipeIngredientsViewHolder viewHolder = new RecipeIngredientsViewHolder(view);
        //add units to unit spinner
        unitAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, getUnits(context));
        viewHolder.unit.setAdapter(unitAdapter);

        //add names to ingredient name search
        final ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getItems(context));
        viewHolder.itemName.setAdapter(nameAdapter);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final RecipeIngredientsViewModel item = getItemAtPosition(position);
        final RecipeIngredientsViewHolder holder = (RecipeIngredientsViewHolder) viewHolder;
        toggleExpansion(item, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setExpanded( ! item.isExpanded());
                if (isClickable) {
                    item.setItemName(holder.itemName.getText().toString());
                    item.setDetail(holder.detail.getText().toString());
                    item.setAmount(holder.amount.getText().toString());
                    item.setUnit(holder.unit.getSelectedItem() != null && Objects.requireNonNull(holder.unit.getSelectedItem()).toString().equals("Unit")
                            ? holder.unit.getSelectedItem().toString() : "");
                } else {
                    holder.entryBoxesContainer.setEnabled(false);
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

    private List<String> getItems(Context context) {
        List<String> names;
        ItemDao dao = new ItemDao(context);
        names = dao.getAllItemNames();
        return names;
    }

    private List<String> getUnits(Context context) {
        List<String> units = new ArrayList<>();
        SharedPreferences userPreferences = context.getSharedPreferences(context.getPackageName() + "userPreferences", Context.MODE_PRIVATE);
        UnitDao dao = new UnitDao(context);
        units.add("Unit");
        units.addAll(dao.getAllUnitAbbrevsBySystemId(userPreferences.getString("unitSystemId", "1")));

        return units;
    }
}
