package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.dao.UserInventoryDao;
import apps.nerdyginger.cleanplateclub.dao.UserItemDao;
import apps.nerdyginger.cleanplateclub.models.UserInventory;

public class InventoryFragment extends Fragment {
    private UserCustomDatabase userDatabase;
    private OnFragmentInteractionListener mListener;
    private AddInventoryDialog addItemDialog;

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void addItem(String itemName, int quantity, String unitName, int stockLevel) {
        UserInventoryDao dao = userDatabase.getUserInventoryDao();
        UserInventory item = new UserInventory();
        /*Uncomment when deletion is also set up 
        item.setItemName(itemName);
        if (quantity != 0) {
            item.setQuantity(quantity);
        }
        UnitDao unitDao = new UnitDao(getContext());
        //TODO: Figure out how to connect UnitId to spinner
        item.setMaxQuantity(quantity * 100 / stockLevel);*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_inventory, container, false);

        // Set up floating action button
        FloatingActionButton addButton = view.findViewById(R.id.inventoryFloatingAddBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemDialog = new AddInventoryDialog();
                addItemDialog.show(Objects.requireNonNull(getFragmentManager()), "AddItem");
            }
        });

        // Get inventory data
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(Objects.requireNonNull(getContext()), UserCustomDatabase.class, "userDatabase")
                    .fallbackToDestructiveMigration() //don't do this in production!!!
                    .build();
        }
        final UserInventoryDao inventoryDao = userDatabase.getUserInventoryDao();
        final List[] dataList = new List[]{new ArrayList<>()};
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataList[0] = inventoryDao.getAllInventoryItems();
            }
        }).start();

        // Fill in the RecyclerView with inventory data
        RecyclerView rv = view.findViewById(R.id.inventoryRecycler);
        rv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(this.getContext()), LinearLayoutManager.VERTICAL));
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public boolean onLongClick(View view, int position) {
                //add onLongClickImplementation
                return true;
            }
        };
        InventoryListAdapter adapter = new InventoryListAdapter(listener);
        adapter.updateData(dataList[0]);
        rv.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
