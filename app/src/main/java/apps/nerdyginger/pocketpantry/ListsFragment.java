package apps.nerdyginger.pocketpantry;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.adapters.ListsAdapter;
import apps.nerdyginger.pocketpantry.dao.UserListItemDao;
import apps.nerdyginger.pocketpantry.models.UserListItem;
import apps.nerdyginger.pocketpantry.view_models.AddListItemDialog;
import apps.nerdyginger.pocketpantry.view_models.ListItemViewModel;


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

        // Set up add item button
        Button addBtn = view.findViewById(R.id.listsAddBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open add lists item dialog
                AddListItemDialog dialog = new AddListItemDialog();
                dialog.show(Objects.requireNonNull(getFragmentManager()), "add a list item!");
            }
        });

        // Set up floating action button (sort list unchecked/checked)
        FloatingActionButton fab = view.findViewById(R.id.listFloatingActionBtn);
        fab.setOnClickListener(new View.OnClickListener() {
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
                adapter.notifyDataSetChanged();
            }
        });

        return view;
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
