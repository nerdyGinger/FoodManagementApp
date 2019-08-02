package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import apps.nerdyginger.cleanplateclub.models.UserInventory;

public class AddInventoryDialog extends DialogFragment {
    SearchView search;
    EditText quantityBox;
    Spinner unitSpinner;
    Button cancelBtn, addBtn;

    public interface DialogListener {
        void onDialogPositiveClick();
        void onDialogNegativeClick();
    }

    DialogListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_add_inventory_item, container, false);

        cancelBtn = rootView.findViewById(R.id.addInventoryCancelBtn);
        addBtn = rootView.findViewById(R.id.addInventoryAddBtn);
        quantityBox = rootView.findViewById(R.id.addInventoryQuantity);
        unitSpinner = rootView.findViewById(R.id.addInventoryUnit);
        search = rootView.findViewById(R.id.addInventorySearch);

        //setup buttons to perform interface implementations
        mListener = (DialogListener) getActivity();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogNegativeClick();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogPositiveClick();
            }
        });
        return rootView;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement my cool DialogListener interface, ya scallywag");
        }
    }

}
