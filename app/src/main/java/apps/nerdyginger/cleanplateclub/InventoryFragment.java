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
import apps.nerdyginger.cleanplateclub.view_models.InventoryViewModel;

public class InventoryFragment extends Fragment {
    private UserCustomDatabase userDatabase;
    private SharedPreferences userPreferences;
    private Context context;
    private InventoryListAdapter adapter;// = new InventoryListAdapter();

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = context.getSharedPreferences(context.getPackageName() + "userPreferences", Context.MODE_PRIVATE);
        //currently in userPreferences: String("unitSystemId", "3");

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
                CustomItemDialog dialog = new CustomItemDialog();
                dialog.show(getFragmentManager(), "input a recipe!");
            }
        });

        // Initialize user database
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context, UserCustomDatabase.class, "userDatabase")
                    .fallbackToDestructiveMigration() //don't do this in production!!! //TODO: write user db migrations to remove destructive migrations
                    .build();
        }

        // Fill in the RecyclerView with inventory data
        final RecyclerView rv = view.findViewById(R.id.inventoryRecycler);
        rv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                CustomItemDialog dialog = new CustomItemDialog(adapter.getItemAtPosition(position));
                dialog.show(getFragmentManager(), "input an item!");
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
        InventoryViewModel inventoryViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        inventoryViewModel.getInventoryList().observe(this, new Observer<List<UserInventoryItem>>() {
            @Override
            public void onChanged(List<UserInventoryItem> userInventories) {
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
