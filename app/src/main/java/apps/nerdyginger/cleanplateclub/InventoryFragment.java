package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.UserInventoryDao;
import apps.nerdyginger.cleanplateclub.dao.UserItemDao;
import apps.nerdyginger.cleanplateclub.models.UserInventory;

public class InventoryFragment extends Fragment {
    private UserCustomDatabase userDatabase;
    private SQLiteDatabase readonlyDatabase;


    private OnFragmentInteractionListener mListener;

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get inventory data
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(getContext(), UserCustomDatabase.class, "userDatabase").build();
        }
        final UserInventoryDao inventoryDao = userDatabase.getUserInventoryDao();
        final List<UserInventory>[] dataList = new List[]{new ArrayList<>()};
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataList[0] = inventoryDao.getAllInventoryItems();
            }
        }).start();

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_inventory, container, false);
        RecyclerView rv = view.findViewById(R.id.inventoryRecycler);
        rv.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //add onClick implementation
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
