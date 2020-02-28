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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.adapters.ListsAdapter;
import apps.nerdyginger.pocketpantry.callbacks.ListsSwipeDeleteCallback;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.dao.UserListItemDao;
import apps.nerdyginger.pocketpantry.helpers.ItemQuantityHelper;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserListItem;
import apps.nerdyginger.pocketpantry.dialogs.AddListItemDialog;
import apps.nerdyginger.pocketpantry.view_models.ListItemViewModel;

/*
 * Fragment for grocery list(s); currently only 1 list is available, potential plans to allow more than one list, maybe?
 * NOTE: if I want to attempt some bottom nav animation to show that I'm effecting inventory from lists, it's going to
 * take some extra leg-work. Check out this blog: https://blog.stylingandroid.com/bottomnavigationview-animating-icons/
 * TODO-VER1.0: allow multiple lists
 * TODO-VER1.0: animate bottom nav on inventory background edit
 * Last edited: 2/28/2020
 */
public class ListsFragment extends Fragment {
    private Context context;
    private ListsAdapter adapter;

    public ListsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_lists, container, false);
        context = getContext();

        // Set up floating action add item button
        FloatingActionButton addBtn = view.findViewById(R.id.listFloatingActionAddBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open add lists item dialog
                AddListItemDialog dialog = new AddListItemDialog();
                dialog.show(Objects.requireNonNull(getFragmentManager()), "add a list item!");
            }
        });

        // Set up floating action sort button (sort list unchecked/checked)
        FloatingActionButton sortFab = view.findViewById(R.id.listFloatingActionSortBtn);
        sortFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sort the list
                adapter.sortData();
            }
        });

        // Set up EmptyRecyclerView
        final EmptyRecyclerView rv = view.findViewById(R.id.listRecycler);
        rv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                UserListItem clicked = adapter.getItemAtPosition(position);
                if (clicked.isChecked()) {
                    subtractInventory(clicked);
                    Toast.makeText(context, "Removing amount from inventory", Toast.LENGTH_SHORT).show();
                } else {
                    addInventory(clicked);
                    Toast.makeText(context, "Adding amount to inventory", Toast.LENGTH_SHORT).show();
                }
                clicked.setChecked( ! clicked.isChecked());
                adapter.notifyDataSetChanged();
                saveCheckStatus(clicked);
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        };
        adapter = new ListsAdapter(listener);
        rv.setAdapter(adapter);
        rv.setEmptyView(view.findViewById(R.id.listEmptyMessage));

        // Get list item data
        ListItemViewModel viewModel = ViewModelProviders.of(this).get(ListItemViewModel.class);
        viewModel.getListItemList().observe(getViewLifecycleOwner(), new Observer<List<UserListItem>>() {
            @Override
            public void onChanged(List<UserListItem> userListItems) {
                adapter.updateData(userListItems);
                adapter.orderItemsByPosition();
                adapter.notifyDataSetChanged();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ListsSwipeDeleteCallback(adapter, context, viewModel));
        itemTouchHelper.attachToRecyclerView(rv);

        return view;
    }

    // When an item is unchecked, remove that amount from inventory
    private void subtractInventory(final UserListItem item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserInventoryItemDao inventoryDao = UserCustomDatabase.getDatabase(context).getUserInventoryDao();
                UserInventoryItem inventoryItem = inventoryDao.getInventoryItemIdByItemId(item.getItemID(), item.isUserAdded());
                ItemQuantityHelper quantityHelper = new ItemQuantityHelper(context);
                // if both list and inventory item are quantified and have same unit type, subtract list quantity from inventory quantity
                if (! item.getQuantity().equals("") || ! inventoryItem.isQuantify()) { //if both items are quantified...
                    inventoryItem = quantityHelper.subtractListFromInventory(item, inventoryItem);
                }
                inventoryDao.update(inventoryItem);
            }
        }).start();
    }

    // When an item is checked, add that amount to inventory; add new inventory item if there
    // is none for the item
    private void addInventory(final UserListItem item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserInventoryItemDao inventoryDao = UserCustomDatabase.getDatabase(context).getUserInventoryDao();
                UserInventoryItem inventoryItem = inventoryDao.getInventoryItemIdByItemId(item.getItemID(), item.isUserAdded());
                ItemQuantityHelper quantityHelper = new ItemQuantityHelper(context);
                //if inventory item doesn't exist, add it in
                if (inventoryItem == null) {
                    inventoryDao.insert(quantityHelper.addNewInventoryFromList(item));
                }
                else if (! item.getQuantity().equals("") || ! inventoryItem.isQuantify()) { //if both items are quantified...
                    inventoryItem = quantityHelper.addListToInventory(item, inventoryItem);
                    inventoryDao.update(inventoryItem);
                }
            }
        }).start();
    }

    private void saveCheckStatus(final UserListItem item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserListItemDao dao = UserCustomDatabase.getDatabase(getContext()).getUserListItemDao();
                dao.update(item);
            }
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        // save the states of the current list of items, including checked status and position
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserListItemDao dao = UserCustomDatabase.getDatabase(getContext()).getUserListItemDao();
                    for (int i=0; i<adapter.getItemCount(); i++) {
                        UserListItem item = adapter.getItemAtPosition(i);
                        dao.update(item);
                    }
                }
            }).start();
        } catch (Exception e) {
            Log.e("MY_CODE_ISSUES", "Error in List onPause: " + e);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
