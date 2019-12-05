package apps.nerdyginger.pocketpantry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.dao.ItemDao;
import apps.nerdyginger.pocketpantry.dao.UnitDao;
import apps.nerdyginger.pocketpantry.dao.UnitSystemDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.models.Unit;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;

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
    private List<String> names = new ArrayList<>();
    private Map<String, String> unitAbbrevPairs = new HashMap<>();

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
        userPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

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
            amount.setText(existingItem.getQuantity());
            unit.setSelection(unitsAdapter.getPosition(unitAbbrevPairs.get(existingItem.getUnit())));
            if (existingItem.isMultiUnit()) {
                Fraction quantityFraction = new Fraction().fromString(existingItem.getQuantity());
                stockMeter.setProgress(Integer.parseInt(existingItem.getQuantity().contains("-") ? "0" :
                        String.valueOf(quantityFraction.getWholeNum())) * 100 / Integer.parseInt(existingItem.getMaxQuantity()));
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
                    valid = saveNewItem();
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
        String newQuantity = amount.getText().toString();
        String unitName = unit.getSelectedItem().toString();
        int stockLevel =  stockMeter.getProgress();
        if (!newName.equals("")) {
            if ((stockClicked || !existingItem.getMaxQuantity().equals("")) && stockLevel == 0) { //preventing divide by zero
                Toast.makeText(getContext(), "Don't forget to set your stock level!", Toast.LENGTH_SHORT).show();
                return false;
            }
            updateItem(newName, newQuantity, unitName, stockLevel);
            return true;
        } else {
            Toast.makeText(getContext(), "Item name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void updateItem(String name, String quantity, final String unitName, int stockLevel) {
        final UserCustomDatabase userDatabase = Room.databaseBuilder(getContext(), UserCustomDatabase.class, "userDatabase")
                .fallbackToDestructiveMigration() //don't do this in production!!!
                .build();
        final UserInventoryItem item = new UserInventoryItem();
        item.setItemName(name);
        if (names.contains(name)) {
            item.setUserAdded(true);
        } else {
            item.setUserAdded(false);
        }
        item.setQuantity(quantity);
        // only set the stock level if it was click at least once
        if (stockClicked || !existingItem.getMaxQuantity().equals("")) {
            if (stockLevel == 0) {
                Toast.makeText(getContext(), "Don't forget to set your stock level!", Toast.LENGTH_SHORT).show();
                return;
            }
            Fraction quantityFraction = new Fraction().fromString(quantity);
            item.setMaxQuantity(String.valueOf(quantityFraction.getWholeNum() * 100 / stockLevel));
        }
        Thread t = new Thread(new Runnable() {                                                                        // (or if existing item had a max quantity)
            @Override
            public void run() {
                try {
                    UserInventoryItemDao dao = UserCustomDatabase.getDatabase(getContext()).getUserInventoryDao();
                    UnitDao unitDao = new UnitDao(getContext());
                    item.setUnit(unitName.equals("(No Unit)") ? "" : unitDao.getUnitAbbrevByName(unitName));
                    item.set_ID(existingItem.get_ID());
                    dao.update(item);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "An error occurred - data may not have been saved", Toast.LENGTH_SHORT).show();
                    Log.e("Database Error", e.toString());
                }
            }
        });
        t.start();

        try {
            t.join();
        }catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
    }

    private boolean saveNewItem() {
        String name = itemName.getText().toString();
        String quantity = amount.getText().toString(); //TODO: fraction input validation
        String unitName = unit.getSelectedItem().toString();
        int stockLevel = stockMeter.getProgress();
        if (!name.equals("")) {
            addItem(name, quantity, unitName, stockLevel);
            return true;
        } else if (quantity.equals("") && ! stockClicked) {
            // tell user to enter quantity for stock level calculations
            Toast.makeText(getContext(), "Please enter quantity for stock level calculations",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            // tell user to enter item name
            Toast.makeText(getContext(), "Item name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void addItem(final String name, String quantity, final String unitName, int stockLevel) {
        final UserInventoryItem item = new UserInventoryItem();
        item.setItemName(name);
        if (names.contains(name)) {
            item.setUserAdded(true);
        } else {
            item.setUserAdded(false);
        }
        item.setQuantity(quantity);
        // only set the stock level if it was clicked at least once and there is a quantity set
        if (stockClicked && ! item.getQuantity().equals("")) {
            item.setMaxQuantity(String.valueOf(Integer.parseInt(quantity) * 100 / stockLevel));
        } else {
            item.setMaxQuantity("");
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserInventoryItemDao dao = UserCustomDatabase.getDatabase(getContext()).getUserInventoryDao();
                    UnitDao unitDao = new UnitDao(getContext());
                    item.setUnit(unitName.equals("(No Unit)") ? "" : unitDao.getUnitAbbrevByName(unitName));
                    dao.insert(item);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "An error occurred - data may not have been saved", Toast.LENGTH_SHORT).show();
                    Log.e("Database Error", e.toString());
                }
            }
        });
        t.start();
        try {
            t.join();
        }catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
    }

    private List<String> getItems() {
        ItemDao dao = new ItemDao(getContext());
        names = dao.getAllItemNames();
        return names;
    }

    private List<String> getUnits() {
        unitAbbrevPairs.put("", "(No Unit)");
        UnitDao dao = new UnitDao(getContext());
        UnitSystemDao systemDao = new UnitSystemDao(getContext());
        List<String> unitAbbrevs = new ArrayList<>();
        unitAbbrevs.add("(No Unit)");
        String systemId = systemDao.getSystemIdByName(userPreferences.getString("unitSystem", "Metric"));
        unitAbbrevs.addAll(dao.getAllUnitNamesBySystemId(systemId));
        List<Unit> units = new ArrayList<>(dao.getAllUnits());
        for (int i=0; i<units.size(); i++) {
            if (String.valueOf(units.get(i).getSystemId()).equals(systemId)) {
                unitAbbrevPairs.put(units.get(i).getAbbreviation(), units.get(i).getFullName());
            }
        }
        return unitAbbrevs;
    }

}
