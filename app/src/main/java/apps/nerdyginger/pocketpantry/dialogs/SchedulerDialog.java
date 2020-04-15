package apps.nerdyginger.pocketpantry.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.EmptyRecyclerView;
import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.helpers.ScheduleHelper;
import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.adapters.HistoryListAdapter;
import apps.nerdyginger.pocketpantry.dao.UserRecipeBoxDao;
import apps.nerdyginger.pocketpantry.dao.UserScheduleDao;
import apps.nerdyginger.pocketpantry.models.HistoryComboItem;
import apps.nerdyginger.pocketpantry.models.UserSchedule;
import apps.nerdyginger.pocketpantry.view_models.ScheduleViewModel;

// The schedule dialog opened with the '+' or the 'Add to Schedule' button on the
// Home screen. Has two modes, 'present' and 'future' work scheduling current
// week or future weeks
// Last edited: 2/25/2020
public class SchedulerDialog extends DialogFragment {
    private ScheduleHelper scheduleHelper;
    private AutoCompleteTextView recipeNameBox;
    private CalendarView calendar;
    private RelativeLayout rvContainer;
    private UserSchedule newScheduleItem = new UserSchedule();
    private String selectedDate, selectedStart, selectedEnd, recipeName;
    private Context context;
    private List<UserSchedule> currentList = new ArrayList<>();
    private List<HistoryComboItem> currentHistoryList = new ArrayList<>();
    private HistoryListAdapter adapter;

    private String MODE;   // "present" or "future"

    public SchedulerDialog(String mode) {
        MODE = mode;
    }

    public SchedulerDialog() {
        MODE = "present";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_scheduler, container, false);
        context = getContext();

        // Initialize schedule helper
        scheduleHelper = new ScheduleHelper(getContext());

        // Find views
        final TextView title = view.findViewById(R.id.schedulerDateTitle); //TODO: change to a button (?) to de-clutter title space
        recipeNameBox = view.findViewById(R.id.schedulerRecipeName);
        Button cancelBtn = view.findViewById(R.id.schedulerCancelBtn);
        Button addBtn = view.findViewById(R.id.schedulerAddBtn);
        calendar = view.findViewById(R.id.schedulerCalendar);
        rvContainer = view.findViewById(R.id.schedulerRecyclerContainer);
        EmptyRecyclerView rv = view.findViewById(R.id.schedulerRecycler);

        // Set title date range
        title.setText(scheduleHelper.getCurrentWeekDateRange());

        // Set up the calendar/recycler; dependent on mode and view toggle status
        selectedDate = scheduleHelper.getCurrentDate();
        selectedStart = scheduleHelper.getWeekStartDate(selectedDate);
        selectedEnd = scheduleHelper.getWeekEndDate(selectedDate);
        if (MODE.equals("present")) {
            //just scheduling current week
            calendar.setVisibility(View.GONE);
            rvContainer.setVisibility(View.GONE);
            title.setClickable(false);
        } else {
            //looking to the future
            calendar.setVisibility(View.GONE);
            calendar.setDate(scheduleHelper.convertDateToLong(scheduleHelper.getCurrentDate()));
            calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year; // Jan = 0, adjust month accordingly
                    selectedStart = scheduleHelper.getWeekStartDate(selectedDate);
                    selectedEnd = scheduleHelper.getWeekEndDate(selectedDate);
                    checkForSchedules();
                    title.setText(scheduleHelper.getWeekRange(selectedDate));
                    toggleCalendar();
                }
            });
            rv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(context);
            rv.setLayoutManager(llm);
            adapter = new HistoryListAdapter();
            rv.setAdapter(adapter);
            ScheduleViewModel viewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
            viewModel.getScheduleList().observe(getViewLifecycleOwner(), new Observer<List<UserSchedule>>() {
                @Override
                public void onChanged(List<UserSchedule> userSchedules) {
                    currentList = userSchedules;
                    checkForSchedules();
                }
            });
            rv.setEmptyView(view.findViewById(R.id.schedulerRecyclerEmptyMessage));
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleCalendar();
                }
            });
        }

        // Add adapter to recipeName field
        ArrayAdapter<String> recipeNamesAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_dropdown_item_1line, getRecipeNames());
        recipeNameBox.setAdapter(recipeNamesAdapter);

        // Add listener to recipeName field
        recipeNameBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                recipeName = recipeNameBox.getText().toString();
            }
        });

        // Add button clicks
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addToDb()) {
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Please enter valid recipe name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void checkForSchedules() {
        List<UserSchedule> tempList = new ArrayList<>();
        for (int i=0; i<currentList.size(); i++) {
            if ( (!currentList.get(i).isCompleted()) &&
                    scheduleHelper.isInWeek(selectedStart, selectedEnd, currentList.get(i))) {
                tempList.add(currentList.get(i));
            }
        }
        getHistoryItems(tempList);
        adapter.updateData(currentHistoryList);
    }

    // Toggle calendar visibility
    private void toggleCalendar() {
        calendar.setVisibility(calendar.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        rvContainer.setVisibility(rvContainer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    // Gets the recipe names from the user recipe box
    // ASSUMPTION: User will only want to add recipes to schedule that are in their recipe box
    private List<String> getRecipeNames() {
        final List<String> names = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                UserRecipeBoxDao dao = db.getUserRecipeBoxDao();
                names.addAll(dao.getAllRecipeNames());
            }
        });
        t.start();
        try {
            t.join();
            return names;
        } catch (Exception e) {
            Log.e("Database Error", "Problem waiting for db thread to complete");
            return names;
        }
    }

    private void getHistoryItems(final List<UserSchedule> scheduleItems) {
        final List<HistoryComboItem> historyItems = new ArrayList<>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserRecipeBoxDao dao = UserCustomDatabase.getDatabase(context).getUserRecipeBoxDao();
                for (int i=0; i<scheduleItems.size(); i++) {
                    HistoryComboItem histItem = new HistoryComboItem();
                    UserSchedule scheduleItem = scheduleItems.get(i);
                    histItem.setStartDate(scheduleItem.getStartDate());
                    histItem.setEndDate(scheduleItem.getEndDate());
                    histItem.setRecipeName(dao.getRecipeById(Integer.parseInt(scheduleItem.getRecipeBoxItemId())).getRecipeName());
                    historyItems.add(histItem);
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Database Error", "Problem waiting for db thread to complete");
        }
        currentHistoryList = historyItems;
    }


    private boolean addToDb() {
        recipeName = recipeNameBox.getText().toString();
        newScheduleItem.setScheduleDate(selectedDate);
        newScheduleItem.setStartDate(selectedStart);
        newScheduleItem.setEndDate(selectedEnd);
        newScheduleItem.setDateAdded(scheduleHelper.getCurrentDate());
        newScheduleItem.setCompleted(false);
        final boolean[] valid = {false};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                UserScheduleDao dao = db.getUserScheduleDao();
                UserRecipeBoxDao boxDao = db.getUserRecipeBoxDao();
                List<String> names = boxDao.getAllRecipeNames();
                if (names.contains(recipeName)) {
                    newScheduleItem.setRecipeBoxItemId(String.valueOf(boxDao.getRecipeIdByName(recipeName)));
                    dao.insert(newScheduleItem);
                    valid[0] = true;
                }
            }
        });
        t.start();
        try {
            t.join();
            return valid[0];
        } catch (Exception e) {
            Log.e("Database Error", "Problem waiting for db thread to complete");
            return valid[0];
        }
    }
}
