package apps.nerdyginger.pocketpantry.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.ScheduleHelper;
import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.dao.UserRecipeBoxDao;
import apps.nerdyginger.pocketpantry.dao.UserScheduleDao;
import apps.nerdyginger.pocketpantry.models.UserSchedule;

// The schedule dialog opened with the '+' or the 'Add to Schedule' button on the
// Home screen. Has two modes, 'present' and 'future' work scheduling current
// week or future weeks
// Last edited: 2/17/2020

// TODO: figure out how to display previously added future schedule items
public class SchedulerDialog extends DialogFragment {
    private ScheduleHelper scheduleHelper;
    private AutoCompleteTextView recipeNameBox;
    private String recipeName;
    private UserSchedule newScheduleItem = new UserSchedule();
    private String MODE;   // "present" or "future"
    private String selectedDate;
    private String selectedStart;
    private String selectedEnd;

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

        // Initialize schedule helper
        scheduleHelper = new ScheduleHelper(getContext());

        // Find views
        final TextView title = view.findViewById(R.id.schedulerDateTitle);
        recipeNameBox = view.findViewById(R.id.schedulerRecipeName);
        Button cancelBtn = view.findViewById(R.id.schedulerCancelBtn);
        Button addBtn = view.findViewById(R.id.schedulerAddBtn);
        CalendarView calendar = view.findViewById(R.id.schedulerCalendar);

        // Set title date range
        title.setText(scheduleHelper.getCurrentWeekDateRange());

        // Set up the calendar
        selectedDate = scheduleHelper.getCurrentDate();
        selectedStart = scheduleHelper.getWeekStartDate(selectedDate);
        selectedEnd = scheduleHelper.getWeekEndDate(selectedDate);
        if (MODE.equals("present")) {
            calendar.setVisibility(View.GONE);
        } else {
            calendar.setVisibility(View.VISIBLE);
            calendar.setDate(scheduleHelper.convertDateToLong(scheduleHelper.getCurrentDate()));
            calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year; // Jan = 0, adjust month accordingly
                    selectedStart = scheduleHelper.getWeekStartDate(selectedDate);
                    selectedEnd = scheduleHelper.getWeekEndDate(selectedDate);
                    title.setText(scheduleHelper.getWeekRange(selectedDate));
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
