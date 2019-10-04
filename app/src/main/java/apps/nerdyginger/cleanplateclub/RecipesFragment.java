package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserRecipeBoxItem;


public class RecipesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private UserCustomDatabase userDatabase;
    private List<UserRecipeBoxItem> data;
    private Context context;

    public RecipesFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_recipes, container, false);

        // Set up floating action button
        FloatingActionButton addBtn = view.findViewById(R.id.recipesFloatingAddBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Initialize user database
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context, UserCustomDatabase.class, "userDatabase")
                    .fallbackToDestructiveMigration() //don't do this in production!!! //TODO: write user db migrations to remove destructive migrations
                    .build();
        }

        // Set up RecyclerView
        RecyclerView rv = view.findViewById(R.id.recipesRecycler);
        rv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
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
        final RecipesListAdapter adapter = new RecipesListAdapter(listener);
        //adapter.updateData(dataList[0]);
        rv.setAdapter(adapter);

        // Get recipes data
        RecipeViewModel recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        recipeViewModel.getRecipeList().observe(this, new Observer<List<UserRecipeBoxItem>>() {
            @Override
            public void onChanged(List<UserRecipeBoxItem> userRecipeBoxItems) {
                data = userRecipeBoxItems;
                adapter.updateData(userRecipeBoxItems);
            }
        });
        // to enable swipe to delete, need to create callback or edit inventory callback for item touch helper

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
