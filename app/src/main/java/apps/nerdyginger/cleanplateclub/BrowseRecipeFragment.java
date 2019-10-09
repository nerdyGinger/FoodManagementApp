package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.nerdyginger.cleanplateclub.adapters.BrowseRecipesCategoryAdapter;


public class BrowseRecipeFragment extends Fragment {
    private Context context;

    public BrowseRecipeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_browse_recipe, container, false);

        RecyclerView categoriesRv = view.findViewById(R.id.browseRecipesRecycler);
        categoriesRv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRv.setLayoutManager(llm);
        categoriesRv.setAdapter(new BrowseRecipesCategoryAdapter());


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
