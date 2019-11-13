package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.adapters.BrowseRecipesItemAdapter;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeBoxDao;
import apps.nerdyginger.cleanplateclub.models.UserRecipeBoxItem;


public class HomeFragment extends Fragment {
    private BrowseRecipesItemAdapter adapter;

    public HomeFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        // Find views
        RecyclerView recipeSelection = view.findViewById(R.id.homeScheduledRecipes);
        TabHost tabHost = view.findViewById(R.id.homeTabHost);
        Button historyBtn = view.findViewById(R.id.homePastWeeksBtn);
        Button scheduleBtn = view.findViewById(R.id.homeScheduleNextWeekBtn);

        // Scheduled recipes horizontal RecyclerView
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recipeSelection.setLayoutManager(llm);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getContext(), adapter.getItemAtPosition(position).getRecipeName(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setTitle("Mark as Complete");
                dialogBuilder.setMessage("Mark the recipe '"+adapter.getItemAtPosition(position).getRecipeName()+"' as complete and remove the items from inventory?");
                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do the mathy stuff
                        // add to the today tab
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
            public boolean onLongClick(View view, int position) {
                return false;
            }
        };
        adapter = new BrowseRecipesItemAdapter("home", listener);
        recipeSelection.setAdapter(adapter);
        adapter.updateData(generateRecipes());

        // Set up tabs
        tabHost.setup();
        // Today's recipes tab
        TabHost.TabSpec spec = tabHost.newTabSpec("today");
        spec.setContent(R.id.homeTodayTab);
        spec.setIndicator("Today");
        tabHost.addTab(spec);
        // This week's recipes tab
        spec = tabHost.newTabSpec("this week");
        spec.setContent(R.id.homeThistWeekTab);
        spec.setIndicator("This Week");
        tabHost.addTab(spec);

        // Add button clicks
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),  "No history yet, sorry!", Toast.LENGTH_SHORT).show();
            }
        });
        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Unable to access scheduler (not built yet)", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private List<UserRecipeBoxItem> generateRecipes() {
        final List<UserRecipeBoxItem> items = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO: change to query schedule db table
                UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                UserRecipeBoxDao dao = db.getUserRecipeBoxDao();
                items.addAll(dao.getAllRecipes());
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Database Error", "Problem waiting for db thread to complete");
        }
        return items;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
