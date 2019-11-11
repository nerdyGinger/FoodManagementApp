package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.dao.UserInventoryItemDao;
import apps.nerdyginger.cleanplateclub.models.UserInventoryItem;

public class CustomItemDialog extends DialogFragment {
    private AutoCompleteTextView itemName;
    private EditText amount;
    private Spinner unit;
    private SeekBar stockMeter;
    private SharedPreferences userPreferences;
    private boolean stockClicked = false;
    //for editing existing item
    private String MODE;
    private UserInventoryItem existingItem;

    public CustomItemDialog() {
        //empty constructor
        MODE = "create";
    }

    public CustomItemDialog(UserInventoryItem item) {
        existingItem = item;
        MODE = "edit";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_inventory_item, container, false);
        userPreferences = getContext().getSharedPreferences(getContext().getPackageName() + "userPreferences", Context.MODE_PRIVATE);

        //find views
        final TextView expandTxt = view.findViewById(R.id.addInventoryExpandLabel);
        final RelativeLayout quantityContainer = view.findViewById(R.id.addInventoryQuantityContainer);
        itemName = view.findViewById(R.id.addInventorySearch);
        amount = view.findViewById(R.id.addInventoryQuantity);
        unit = view.findViewById(R.id.addInventoryUnit);
        stockMeter = view.findViewById(R.id.addInventoryStockMeter);
        Button cancelBtn = view.findViewById(R.id.addInventoryCancelBtn);
        Button saveBtn = view.findViewById(R.id.addInventorySaveBtn);

        //add data adapters
        ArrayAdapter<String> namesAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_dropdown_item_1line, getItems());
        itemName.setAdapter(namesAdapter);
        ArrayAdapter<String> unitsAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_dropdown_item_1line, getUnits());
        unit.setAdapter(unitsAdapter);

        //set values if editing existing item
        if (MODE.equals("edit")) {
            //odd trick to fix an annoying auto-dropdown tendency in AutoCompleteTextViews
            itemName.setThreshold(Integer.MAX_VALUE);
            itemName.setText(existingItem.getItemName());
            itemName.setThreshold(1);
            //set other values
            if (existingItem.getQuantity() != 0) {
                amount.setText(String.valueOf(existingItem.getQuantity()));
            }
            unit.setSelection(unitsAdapter.getPosition(existingItem.getUnit()));
            if (existingItem.getMaxQuantity() != 0) {
                stockMeter.setProgress(existingItem.getQuantity() * 100 / existingItem.getMaxQuantity());
            }
        }

        //set onClick listener for stock bar
        stockMeter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // nothing
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stockClicked = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // nothing
            }
        });

        //handle expansion/de-expansion
        quantityContainer.setVisibility(View.GONE);
        expandTxt.setText("Click to add quantity");
        if (MODE.equals("edit")) { toggleExpansion(quantityContainer, expandTxt); }
        expandTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpansion(quantityContainer, expandTxt);
            }
        });

        //handle button clicks
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            boolean valid = true;
            @Override
            public void onClick(View v) {
                if (MODE.equals("create")) {
                    saveNewItem();
                } else {
                    valid = editExistingItem();
                }
                if (valid) {
                    //only dismiss if we actually we able to save
                    dismiss();
                }
            }
        });

        return view;
    }

    private void toggleExpansion(RelativeLayout container, TextView label) {
        container.setVisibility(container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        label.setText(container.getVisibility() == View.VISIBLE ? "Click to collapse" : "Click to add quantity");
    }

    private boolean editExistingItem() {
        String newName = itemName.getText().toString();
        int newQuantity = Integer.parseInt((amount.getText().toString().equals("") ? "0" : amount.getText().toString()));
        String unitName = unit.getSelectedItem().toString();
        int stockLevel = stockMeter.getProgress();
        if (!newName.equals("")) {
            deleteItem(); //delete existingItem and add new item with edited data
            addItem(newName, newQuantity, unitName, stockLevel);
            return true;
        } else {
            Toast.makeText(getContext(), "Item name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void saveNewItem() {
        String name = itemName.getText().toString();
        int quantity = Integer.parseInt((amount.getText().toString().equals("") ? "0" : amount.getText().toString()));
        String unitName = unit.getSelectedItem().toString();
        int stockLevel = stockMeter.getProgress();
        if (!name.equals("")) {
            //TODO: Refine logic for inventory additions (set level but not unit)
            addItem(name, quantity, unitName, stockLevel);
        } else {
            Toast.makeText(getContext(), "Item name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem() {
        final UserCustomDatabase userDatabase = Room.databaseBuilder(getContext(), UserCustomDatabase.class, "userDatabase")
                .fallbackToDestructiveMigration() //don't do this in production!!!
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserInventoryItemDao dao = userDatabase.getUserInventoryDao();
                dao.delete(existingItem);
            }
        }).start();
    }

    private void addItem(final String name, int quantity, final String unitName, int stockLevel) {
        final UserCustomDatabase userDatabase = Room.databaseBuilder(getContext(), UserCustomDatabase.class, "userDatabase")
                .fallbackToDestructiveMigration() //don't do this in production!!!
                .build();
        final UserInventoryItem item = new UserInventoryItem();
        //Uncomment when deletion is also set up
        item.setItemName(name);
        if (quantity != 0) {
            item.setQuantity(quantity);
        }
        if (stockClicked) { item.setMaxQuantity(quantity * 100 / stockLevel); } // only set the stock level if it was click at least once
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserInventoryItemDao dao = userDatabase.getUserInventoryDao();
                final UnitDao unitDao = new UnitDao(getContext());
                item.setUnit(unitDao.getUnitIdByNameAndSystem(unitName, userPreferences.getString("unitSystemId", "1")));
                dao.insert(item);
            }
        }).start();
    }

    private List<String> getItems() {
        List<String> names;
        ItemDao dao = new ItemDao(getContext());
        names = dao.getAllItemNames();
        return names;
    }

    private List<String> getUnits() {
        List<String> units = new ArrayList<>();
        units.add("(No Unit)");
        UnitDao dao = new UnitDao(getContext());
        units.addAll(dao.getAllUnitNamesBySystemId(userPreferences.getString("unitSystemId", "1")));
        return units;
    }

}
