package apps.nerdyginger.cleanplateclub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import apps.nerdyginger.cleanplateclub.adapters.InventoryListAdapter;
import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.dao.UserInventoryItemDao;
import apps.nerdyginger.cleanplateclub.models.UserInventoryItem;

public class InventoryFragment extends Fragment {
    private UserCustomDatabase userDatabase;
    private OnFragmentInteractionListener mListener;
    private SharedPreferences userPreferences;
    private Context context;
    private List<UserInventoryItem> data;
    private InventoryViewModel inventoryViewModel;
    private InventoryListAdapter adapter = new InventoryListAdapter();

    public InventoryFragment() {
        // Required empty public constructor
    }

    public InventoryListAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = context.getSharedPreferences(context.getPackageName() + "userPreferences", Context.MODE_PRIVATE);
        //currently in userPreferences: String("unitSystemId", "3");

    }

    private List<String> getItems() {
        List<String> names;
        ItemDao dao = new ItemDao(getContext());
        names = dao.getAllItemNames();
        return names;
    }

    private List<String> getUnits() {
        List<String> units;
        UnitDao dao = new UnitDao(getContext());
        units = dao.getAllUnitNamesBySystemId(userPreferences.getString("unitSystemId", "1"));
        return units;
    }

    public void addItem(String itemName, int quantity, final String unitName, int stockLevel) {
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context, UserCustomDatabase.class, "userDatabase")
                .fallbackToDestructiveMigration() //don't do this in production!!!
                .build();
            userPreferences = context.getSharedPreferences(context.getPackageName() + "userPreferences", Context.MODE_PRIVATE);
        }
        final UserInventoryItem item = new UserInventoryItem();
        //Uncomment when deletion is also set up
        item.setItemName(itemName);
        if (quantity != 0) {
            item.setQuantity(quantity);
        }
        item.setMaxQuantity(quantity * 100 / stockLevel);
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserInventoryItemDao dao = userDatabase.getUserInventoryDao();
                final UnitDao unitDao = new UnitDao(context);
                item.setUnit(unitDao.getUnitIdByNameAndSystem(unitName, userPreferences.getString("unitSystemId", "1")));
                dao.insert(item);
            }
        }).start();
        data.add(item);
        adapter.updateData(data);
    }

    public void addItemBtnClick(AlertDialog.Builder dialogBuilder, View addItemView) {
        final AutoCompleteTextView search = addItemView.findViewById(R.id.addInventorySearch);
        List<String> names = getItems();
        ArrayAdapter<String> namesAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_dropdown_item_1line, names);
        search.setAdapter(namesAdapter);
        final EditText quantityBox = addItemView.findViewById(R.id.addInventoryQuantity);
        final Spinner unitSpinner = addItemView.findViewById(R.id.addInventoryUnit);
        List<String> units = getUnits();
        ArrayAdapter<String> unitsAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_dropdown_item_1line, units);
        unitSpinner.setAdapter(unitsAdapter);
        final SeekBar stockMeter = addItemView.findViewById(R.id.addInventoryStockMeter);

        dialogBuilder.setView(addItemView);
        dialogBuilder.setTitle("Add Item");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = search.getText().toString();
                int quantity = Integer.parseInt((quantityBox.getText().toString().equals("") ? "0" : quantityBox.getText().toString()));
                String unitName = unitSpinner.getSelectedItem().toString();
                int stockLevel = stockMeter.getProgress();
                if (!name.equals("")) {
                    //TODO: Refine logic for inventory additions (set level but not unit)
                    addItem(name, quantity, unitName, stockLevel);
                }
                adapter.updateData(data);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // don't do anything
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_inventory, container, false);

        // Set up floating action button
        FloatingActionButton addButton = view.findViewById(R.id.inventoryFloatingAddBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: figure out how to hide soft keyboard on focus change (cause that's really annoying)
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                final View addItemView = inflater.inflate(R.layout.dialog_add_inventory_item, null);
                addItemBtnClick(dialogBuilder, addItemView);
            }
        });

        // Initialize user database
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context, UserCustomDatabase.class, "userDatabase")
                    .fallbackToDestructiveMigration() //don't do this in production!!! //TODO: write user db migrations to remove destructive migrations
                    .build();
        }

        // Fill in the RecyclerView with inventory data
        RecyclerView rv = view.findViewById(R.id.inventoryRecycler);
        rv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getContext(), adapter.getItemAtPosition(position).getItemName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onLongClick(View view, int position) {
                //add onLongClickImplementation
                return true;
            }
        };
        adapter = new InventoryListAdapter(listener);
        rv.setAdapter(adapter);

        // Get inventory data
        inventoryViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        inventoryViewModel.getInventoryList().observe(this, new Observer<List<UserInventoryItem>>() {
            @Override
            public void onChanged(List<UserInventoryItem> userInventories) {
                data = userInventories;
                adapter.updateData(userInventories);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new InventorySwipeDeleteCallback(adapter, context, inventoryViewModel));
        itemTouchHelper.attachToRecyclerView(rv);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
