package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;

public class AddInventoryDialog extends DialogFragment {
    private AutoCompleteTextView search;
    private EditText quantityBox;
    private Spinner unitSpinner;
    private Button cancelBtn, addBtn;
    private SeekBar stockMeter;

    public interface AddInventoryDialogListener {
        void onAddInventoryDialogPositiveClick(AddInventoryDialog dialog, String itemName, int quantity,
                                               String unitName, int stockLevel);
        void onAddInventoryDialogNegativeClick(AddInventoryDialog dialog);
    }

    AddInventoryDialogListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_add_inventory_item, container, false);

        cancelBtn = rootView.findViewById(R.id.addInventoryCancelBtn);
        addBtn = rootView.findViewById(R.id.addInventoryAddBtn);
        search = rootView.findViewById(R.id.addInventorySearch);
        quantityBox = rootView.findViewById(R.id.addInventoryQuantity);
        unitSpinner = rootView.findViewById(R.id.addInventoryUnit);
        stockMeter = rootView.findViewById(R.id.addInventoryStockMeter);

        List<String> units = getUnits();
        ArrayAdapter<String> unitsAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_dropdown_item_1line, units);
        unitSpinner.setAdapter(unitsAdapter);

        List<String> names = getItems();
        ArrayAdapter<String> namesAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_dropdown_item_1line, names);
        search.setAdapter(namesAdapter);

        //setup buttons to perform interface implementations
        mListener = (AddInventoryDialogListener) getActivity();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAddInventoryDialogNegativeClick(AddInventoryDialog.this);
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = search.getText().toString();
                int quantity = Integer.parseInt(quantityBox.getText().toString());
                String unitName = unitSpinner.getSelectedItem().toString();
                int stockLevel = stockMeter.getProgress();
                if (name != "") {
                    //TODO: Refine logic for inventory additions
                    mListener.onAddInventoryDialogPositiveClick(AddInventoryDialog.this, name,
                            quantity, unitName, stockLevel);
                }
            }
        });
        return rootView;
    }

    private List<String> getUnits() {
        List<String> units;
        UnitDao dao = new UnitDao(getContext());
        units = dao.getAllUnitNames();
        return units;
    }

    private List<String> getItems() {
        List<String> names;
        ItemDao dao = new ItemDao(getContext());
        names = dao.getAllItemNames();
        return names;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (AddInventoryDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString());
        }
    }

}
