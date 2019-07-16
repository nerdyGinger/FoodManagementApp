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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InventoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private UserCustomDatabase userDatabase;
    private SQLiteDatabase readonlyDatabase;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InventoryFragment() {
        // Required empty public constructor
    }

    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
