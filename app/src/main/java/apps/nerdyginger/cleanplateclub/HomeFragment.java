package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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
import apps.nerdyginger.cleanplateclub.dao.UnitConversionDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.dao.UserInventoryItemDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeBoxDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeItemJoinDao;
import apps.nerdyginger.cleanplateclub.dao.UserScheduleDao;
import apps.nerdyginger.cleanplateclub.models.Unit;
import apps.nerdyginger.cleanplateclub.models.UserInventoryItem;
import apps.nerdyginger.cleanplateclub.models.UserRecipeBoxItem;
import apps.nerdyginger.cleanplateclub.models.UserRecipeItemJoin;
import apps.nerdyginger.cleanplateclub.models.UserSchedule;
import apps.nerdyginger.cleanplateclub.view_models.ScheduleViewModel;


public class HomeFragment extends Fragment {
    private BrowseRecipesItemAdapter adapter;
    private ScheduleViewModel viewModel;
    private List<UserSchedule> currentList;
    private List<UserRecipeBoxItem> currentRecipeBoxList;

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

        // Scheduled recipes horizontal RecyclerView, add confirmation dialog to onClick
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recipeSelection.setLayoutManager(llm);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (position == adapter.getItemCount() - 1) {
                    //add button click
                    SchedulerDialog dialog = new SchedulerDialog();
                    dialog.show(getFragmentManager(), "open scheduler");
                } else {
                    buildConfirmationDialog(position).show();
                }
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        };
        adapter = new BrowseRecipesItemAdapter("home", listener);
        recipeSelection.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        viewModel.getScheduleList().observe(getViewLifecycleOwner(), new Observer<List<UserSchedule>>() {
            @Override
            public void onChanged(List<UserSchedule> userSchedules) {
                currentList = userSchedules;
                currentRecipeBoxList = generateRecipes(userSchedules);
                adapter.updateData(currentRecipeBoxList);
            }
        });
        //adapter.updateData(generateRecipes(viewModel.getScheduleList().getValue()));

        // Set up tabs
        setUpTabs(tabHost);

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
                SchedulerDialog dialog = new SchedulerDialog();
                dialog.show(getFragmentManager(), "open scheduler");
                //Toast.makeText(getContext(), "Unable to access scheduler (not built yet)", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void subtractInventory(final int recipeId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                UserRecipeItemJoinDao joinDao = db.getUserRecipeItemJoinDao();
                UserInventoryItemDao inventoryDao = db.getUserInventoryDao();
                UnitDao unitDao = new UnitDao(getContext());
                List<UserRecipeItemJoin> joinInventoryItems = joinDao.getJoinItemsInRecipe(recipeId);
                for (int i=0; i<joinInventoryItems.size(); i++) {
                    UserRecipeItemJoin joinItem = joinInventoryItems.get(i);
                    if (joinItem.inInventory) {
                        UserInventoryItem tempItem = inventoryDao.getInventoryItemById(joinItem.itemId);
                        // Only subtract if the inventory item is quantified, too!
                        if (tempItem.isQuantify()) {
                            Unit joinItemUnit = unitDao.getUnitByAbbrev(joinItem.unit);
                            Unit inventoryItemUnit = unitDao.getUnitByAbbrev(tempItem.getUnit());
                            Fraction joinItemQuantity = new Fraction().fromString(joinItem.quantity);
                            Fraction inventoryItemQuantity = new Fraction().fromString(tempItem.getQuantity());
                            if (joinItem.unit.equals("") && tempItem.getUnit().equals("") ||                  // if both units are empty
                                        joinItem.unit == null && tempItem.getUnit() == null ||                // ...or null
                                        joinItemUnit.getFullName().equals(inventoryItemUnit.getFullName())) { // ...or they have the same unit
                                // ...then subtract as usual
                                //TODO: (check preferences?)(***multiply by servings***)
                                tempItem.setQuantity(inventoryItemQuantity.subtract(joinItemQuantity).toString());
                                inventoryDao.update(tempItem);
                            } else if (joinItemUnit.getType().equals(inventoryItemUnit.getType())) {
                                //different units, but same types: convert and subtract
                                UnitConversionDao conversionDao = new UnitConversionDao(getContext());
                                Log.e("CONVERSION_DEBUG", "BEFORE: " + joinItemQuantity.toString());
                                Fraction used = conversionDao.convertUnitQuantity(
                                        /*convert recipe quantity...*/ joinItemQuantity,
                                        /*  ...from recipe unit...  */ String.valueOf(joinItemUnit.get_ID()),
                                        /*  ...to inventory unit... */ String.valueOf(inventoryItemUnit.get_ID()));
                                //and now we can subtract like normal
                                Log.e("CONVERSION_DEBUG", "AFTER: " + used.toString());
                                Fraction newAmount = inventoryItemQuantity.subtract(used);
                                tempItem.setQuantity(newAmount.toString());
                                inventoryDao.update(tempItem);
                            } //else {
                                //different unit types, not possible: skip
                            //}
                        }
                    }
                }
            }
        });
        t.start();
        //join?
    }

    private AlertDialog buildConfirmationDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Mark as Complete");
        dialogBuilder.setMessage("Mark the recipe '"+adapter.getItemAtPosition(position).getRecipeName()+"' as complete and remove the items from inventory?");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                subtractInventory(adapter.getItemAtPosition(position).getRecipeId());
                //adapter.deleteItem(position);
                viewModel.deleteItem(currentList.get(position));
                // add to the today tab
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // don't do anything
            }
        });
        return  dialogBuilder.create();
    }

    private void setUpTabs(TabHost tabHost) {
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
    }

    private List<UserRecipeBoxItem> generateRecipes(final List<UserSchedule> scheduleItems) {
        final List<UserRecipeBoxItem> boxItems = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                UserRecipeBoxDao boxDao = db.getUserRecipeBoxDao();
                for (int i=0; i<scheduleItems.size(); i++) {
                    boxItems.add(boxDao.getRecipeById(Integer.parseInt(scheduleItems.get(i).getRecipeBoxItemId())));
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Database Error", "Problem waiting for db thread to complete");
        }
        return boxItems;
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
