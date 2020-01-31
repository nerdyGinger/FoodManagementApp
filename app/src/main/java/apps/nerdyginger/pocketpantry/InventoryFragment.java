package apps.nerdyginger.pocketpantry;

import android.content.Context;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.adapters.InventoryListAdapter;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.view_models.InventoryViewModel;

public class InventoryFragment extends Fragment {
    private UserCustomDatabase userDatabase;
    private Context context;
    private InventoryListAdapter adapter;// = new InventoryListAdapter();

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                dialog.show(Objects.requireNonNull(getFragmentManager()), "input a recipe!");
            }
        });

        // Initialize user database
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context, UserCustomDatabase.class, "userDatabase")
                    .fallbackToDestructiveMigration() //don't do this in production!!! //TODO: write user db migrations to remove destructive migrations
                    .build();
        }

        // Fill in the RecyclerView with inventory data
        EmptyRecyclerView rv = view.findViewById(R.id.inventoryRecycler);
        rv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                CustomItemDialog dialog = new CustomItemDialog(adapter.getItemAtPosition(position));
                dialog.show(Objects.requireNonNull(getFragmentManager()), "input an item!");
            }

            @Override
            public boolean onLongClick(View view, int position) {
                //add onLongClickImplementation
                return true;
            }
        };
        adapter = new InventoryListAdapter(listener);
        rv.setAdapter(adapter);
        rv.setEmptyView(view.findViewById(R.id.inventoryEmptyMessage));

        // Get inventory data
        InventoryViewModel inventoryViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        inventoryViewModel.getInventoryList().observe(getViewLifecycleOwner(), new Observer<List<UserInventoryItem>>() {
            @Override
            public void onChanged(List<UserInventoryItem> userInventories) {
                adapter.updateData(userInventories);
                adapter.notifyDataSetChanged();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new InventorySwipeDeleteCallback(adapter, context, inventoryViewModel));
        itemTouchHelper.attachToRecyclerView(rv);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
