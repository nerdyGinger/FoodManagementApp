package apps.nerdyginger.pocketpantry;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import java.util.Objects;

import apps.nerdyginger.pocketpantry.adapters.BrowseRecipesItemAdapter;
import apps.nerdyginger.pocketpantry.adapters.RecipesListAdapter;
import apps.nerdyginger.pocketpantry.dao.ItemDao;
import apps.nerdyginger.pocketpantry.dao.RecipeItemJoinDao;
import apps.nerdyginger.pocketpantry.dao.UnitDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeBoxDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeItemJoinDao;
import apps.nerdyginger.pocketpantry.dao.UserScheduleDao;
import apps.nerdyginger.pocketpantry.dialogs.ScheduleHistoryDialog;
import apps.nerdyginger.pocketpantry.dialogs.SchedulerDialog;
import apps.nerdyginger.pocketpantry.helpers.ItemQuantityHelper;
import apps.nerdyginger.pocketpantry.helpers.ScheduleHelper;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeItem;
import apps.nerdyginger.pocketpantry.models.RecipeItemJoin;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;
import apps.nerdyginger.pocketpantry.models.UserRecipeItemJoin;
import apps.nerdyginger.pocketpantry.models.UserSchedule;
import apps.nerdyginger.pocketpantry.view_models.ScheduleViewModel;

/*
 * The fragment controlling the home page, complete with schedule and history dashboard.
 * Last edited: 2/25/2020
 */
public class HomeFragment extends Fragment {
    private BrowseRecipesItemAdapter adapter;
    private ScheduleViewModel viewModel;
    private List<UserSchedule> currentList = new ArrayList<>();
    private List<BrowseRecipeItem> currentRecipeBoxList;
    private ScheduleHelper scheduleHelper;

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

        // Initialize schedule helper
        scheduleHelper = new ScheduleHelper(getContext());

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
                    SchedulerDialog dialog = new SchedulerDialog("present");
                    dialog.show(Objects.requireNonNull(getFragmentManager()), "open scheduler");
                } else {
                    buildConfirmationDialog(position).show();
                }
            }
            @Override
            public boolean onLongClick(View view, int position) {
                buildDeleteDialog(position).show();
                return false;
            }
        };
        adapter = new BrowseRecipesItemAdapter("home", listener);
        recipeSelection.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        viewModel.getScheduleList().observe(getViewLifecycleOwner(), new Observer<List<UserSchedule>>() {
            @Override
            public void onChanged(List<UserSchedule> userSchedules) {
                // Only add the uncompleted scheduled recipes from the current week date range
                currentList = new ArrayList<>();
                for (int i=0; i<userSchedules.size(); i++) {
                    if ( (!userSchedules.get(i).isCompleted()) &&
                        scheduleHelper.isInCurrentWeek(userSchedules.get(i))) {
                        currentList.add(userSchedules.get(i));
                    }
                }
                currentRecipeBoxList = getBrowseRecipes(currentList);
                adapter.updateData(currentRecipeBoxList);
            }
        });
        //adapter.updateData(getRecipes(viewModel.getScheduleList().getValue()));

        // Set up tabs
        setUpTabs(tabHost);

        // Add button clicks
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleHistoryDialog historyDialog = new ScheduleHistoryDialog();
                historyDialog.show(Objects.requireNonNull(getFragmentManager()), "open history");
            }
        });
        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchedulerDialog dialog = new SchedulerDialog("future");
                dialog.show(Objects.requireNonNull(getFragmentManager()), "open scheduler");
            }
        });

        return view;
    }

    private void subtractInventory(final int recipeId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                    RecipeItemJoinDao joinDao = new RecipeItemJoinDao(getContext());
                    UserInventoryItemDao inventoryDao = db.getUserInventoryDao();
                    ItemDao itemDao = new ItemDao(getContext());
                    List<RecipeItemJoin> joinInventoryItems = joinDao.getJoinItemsInRecipe(String.valueOf(recipeId));
                    ItemQuantityHelper quantityHelper = new ItemQuantityHelper(getContext());
                    for (int i=0; i<joinInventoryItems.size(); i++) {
                        RecipeItemJoin joinItem = joinInventoryItems.get(i);
                        String itemName = itemDao.getItemName(String.valueOf(joinItem.getItemId()));
                        if (quantityHelper.itemNameInInventory(itemName)) {
                            UserInventoryItem tempItem = inventoryDao.getInventoryItemById(joinItem.getItemId());
                            // Only subtract if the inventory item is quantified, too!
                            if (tempItem.isQuantify()) {
                                //TODO: (check preferences?)(***multiply by servings***)
                                tempItem = quantityHelper.subtractIngredient(joinItem, tempItem);
                                inventoryDao.update(tempItem);
                            }
                        }
                    }
                }catch (Exception e) {
                    Log.e("RD_ONLY_SUBTRACT_ERROR", e.toString());
                    Toast.makeText(getContext(), "Unable to perform inventory subtraction", Toast.LENGTH_SHORT).show();
                }
            }
        });
        t.start();
    }

    private void subtractUserRecipeInventory(final int recipeId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                    UserRecipeItemJoinDao joinDao = db.getUserRecipeItemJoinDao();
                    UserInventoryItemDao inventoryDao = db.getUserInventoryDao();
                    List<UserRecipeItemJoin> joinInventoryItems = joinDao.getJoinItemsInRecipe(recipeId);
                    ItemQuantityHelper quantityHelper = new ItemQuantityHelper(getContext());
                    for (int i = 0; i < joinInventoryItems.size(); i++) {
                        UserRecipeItemJoin joinItem = joinInventoryItems.get(i);
                        if (quantityHelper.itemNameInInventory(joinItem.itemName)) {
                            UserInventoryItem tempItem = inventoryDao.getInventoryItemById(joinItem.itemId);
                            // Only subtract if the inventory item is quantified, too!
                            if (tempItem.isQuantify()) {
                                //TODO: (check preferences?)(***multiply by servings***)
                                tempItem = quantityHelper.subtractUserRecipeIngredient(joinItem, tempItem);
                                inventoryDao.update(tempItem);

                                /*
                                Unit joinItemUnit = unitDao.getUnitByAbbrev(joinItem.unit);
                                Unit inventoryItemUnit = unitDao.getUnitByAbbrev(tempItem.getUnit());
                                Fraction joinItemQuantity = new Fraction().fromString(joinItem.quantity);
                                Fraction inventoryItemQuantity = new Fraction().fromString(tempItem.getQuantity());
                                if (joinItem.unit.equals("") && tempItem.getUnit().equals("") ||                  // if both units are empty
                                        joinItem.unit == null && tempItem.getUnit() == null ||                // ...or null
                                        joinItemUnit.getFullName().equals(inventoryItemUnit.getFullName()) ||
                                        joinItemUnit.getType().equals(inventoryItemUnit.getType())) { // ...or they have the same unit
                                    // ...then subtract as usual
                                    tempItem.setQuantity(quantityHelper.);
                                    tempItem.setQuantity(inventoryItemQuantity.subtract(joinItemQuantity).toString());
                                    inventoryDao.update(tempItem);
                                } else if (joinItemUnit.getType().equals(inventoryItemUnit.getType())) {
                                    //different units, but same types: convert and subtract
                                    UnitConversionDao conversionDao = new UnitConversionDao(getContext());
                                    Fraction used = conversionDao.convertUnitQuantity(
                                            /*convert recipe quantity... joinItemQuantity,
                                            /*  ...from recipe unit...   String.valueOf(joinItemUnit.get_ID()),
                                            /*  ...to inventory unit...  String.valueOf(inventoryItemUnit.get_ID()));
                                    //and now we can subtract like normal
                                    Fraction newAmount = inventoryItemQuantity.subtract(used);
                                    tempItem.setQuantity(newAmount.toString());
                                    inventoryDao.update(tempItem);
                                } //else {
                                //  different unit types, not possible: skip
                                //}

                                 */
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("USER_SUBTRACT_ERROR", e.toString());
                    Toast.makeText(getContext(), "Unable to perform inventory subtraction", Toast.LENGTH_SHORT).show();
                }
            }
        });
        t.start();
    }

    private void markAsComplete(final int recipeBoxId) {
        UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
        final UserScheduleDao scheduleDao = db.getUserScheduleDao();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserSchedule oldItem = scheduleDao.getScheduleItemByRecipeBoxId(recipeBoxId);
                oldItem.setCompleted(true);
                oldItem.setDateCompleted(scheduleHelper.getCurrentDate());
                scheduleDao.update(oldItem);
            }
        });
        t.start();
        //join?
    }

    private void deleteScheduleItem(final int recipeBoxId) {
        final UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserScheduleDao dao = db.getUserScheduleDao();
                viewModel.deleteItem(dao.getScheduleItemByRecipeBoxId(recipeBoxId));
            }
        });
        t.start();
    }

    private AlertDialog buildDeleteDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        dialogBuilder.setTitle("Delete Schedule Item?");
        dialogBuilder.setMessage("Are you sure you want to delete this schedule item?");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteScheduleItem(adapter.getItemAtPosition(position).getRecipeId());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //don't do anything
            }
        });
        return dialogBuilder.create();
    }

    private AlertDialog buildConfirmationDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        dialogBuilder.setTitle("Mark as Complete");
        dialogBuilder.setMessage("Mark the recipe '"+adapter.getItemAtPosition(position).getRecipeName()+"' as complete and remove the items from inventory?");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BrowseRecipeItem selected = adapter.getItemAtPosition(position);
                if (selected.isUserAdded()) {
                    subtractUserRecipeInventory(selected.getRecipeId());
                } else {
                    subtractInventory(selected.getRecipeId());
                }
                markAsComplete(selected.getRecipeBoxId());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // don't do anything
            }
        });
        return dialogBuilder.create();
    }

    private void setUpTabs(TabHost tabHost) {
        // Set up tabs
        tabHost.setup();

        // Today's recipes tab
        TabHost.TabSpec spec = tabHost.newTabSpec("today");
        spec.setContent(R.id.homeTodayTab);
        EmptyRecyclerView todayRv = tabHost.getTabContentView().findViewById(R.id.homeCompletedTodayRecipeRecycler);
        todayRv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        todayRv.setLayoutManager(llm);
        final RecipesListAdapter todayAdapter = new RecipesListAdapter();
        todayRv.setAdapter(todayAdapter);
        ScheduleViewModel todayModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        todayModel.getScheduleList().observe(getViewLifecycleOwner(), new Observer<List<UserSchedule>>() {
            @Override
            public void onChanged(List<UserSchedule> userSchedules) {
                List<UserSchedule> list = new ArrayList<>();
                for (int i=0; i<userSchedules.size(); i++) {
                    if (scheduleHelper.completedToday(userSchedules.get(i))) {
                        list.add(userSchedules.get(i));
                    }
                }
                List<UserRecipeBoxItem> itemList = getRecipes(list);
                todayAdapter.updateData(itemList);
            }
        });
        todayRv.setEmptyView(tabHost.getTabContentView().findViewById(R.id.homeTodayEmptyMessage));
        spec.setIndicator("Today");
        tabHost.addTab(spec);

        // This week's recipes tab
        spec = tabHost.newTabSpec("this week");
        spec.setContent(R.id.homeThisWeekTab);
        EmptyRecyclerView weekRv = tabHost.getTabContentView().findViewById(R.id.homeCompletedThisWeekRecipeRecycler);
        weekRv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm2 = new LinearLayoutManager(getContext());
        weekRv.setLayoutManager(llm2);
        final RecipesListAdapter weekAdapter = new RecipesListAdapter();
        weekRv.setAdapter(weekAdapter); //properly set up adapter to pull from LiveData view model
        ScheduleViewModel weekModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        weekModel.getScheduleList().observe(getViewLifecycleOwner(), new Observer<List<UserSchedule>>() {
            @Override
            public void onChanged(List<UserSchedule> userSchedules) {
                List<UserSchedule> list = new ArrayList<>();
                for (int i=0; i<userSchedules.size(); i++) {
                    if (scheduleHelper.isInCurrentWeek(userSchedules.get(i)) &&
                        userSchedules.get(i).isCompleted()) {
                        list.add(userSchedules.get(i));
                    }
                }
                List<UserRecipeBoxItem> itemList = getRecipes(list);
                weekAdapter.updateData(itemList);
            }
        });
        weekRv.setEmptyView(tabHost.getTabContentView().findViewById(R.id.homeThisWeekEmptyMessage));
        spec.setIndicator("This Week");
        tabHost.addTab(spec);
    }

    private List<UserRecipeBoxItem> getRecipes(final List<UserSchedule> scheduleItems) {
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

    private List<BrowseRecipeItem> getBrowseRecipes(final List<UserSchedule> scheduleItems) {
        final List<BrowseRecipeItem> boxItems = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                UserRecipeBoxDao boxDao = db.getUserRecipeBoxDao();
                for (int i=0; i<scheduleItems.size(); i++) {
                    UserRecipeBoxItem boxItem = boxDao.getRecipeById(Integer.parseInt(scheduleItems.get(i).getRecipeBoxItemId()));
                    BrowseRecipeItem temp = new BrowseRecipeItem();
                    temp.setCategory(boxItem.getCategory());
                    temp.setRecipeId(boxItem.getRecipeId());
                    temp.setRecipeName(boxItem.getRecipeName());
                    temp.setServings(boxItem.getServings());
                    temp.setUserAdded(boxItem.isUserAdded());
                    temp.setRecipeBoxId(boxItem.get_ID());
                    boxItems.add(temp);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
