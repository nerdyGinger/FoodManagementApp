package apps.nerdyginger.cleanplateclub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import apps.nerdyginger.cleanplateclub.dao.UserRecipeBoxDao;
import apps.nerdyginger.cleanplateclub.dao.UserScheduleDao;
import apps.nerdyginger.cleanplateclub.models.UserSchedule;

public class SchedulerDialog extends DialogFragment {
    private String currentDate;
    private String currentWeekStartDate;
    private String currentWeekEndDate;
    private AutoCompleteTextView recipeNameBox;
    private String recipeName;
    private UserSchedule newScheduleItem = new UserSchedule();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scheduler_dialog, container, false);

        // Find views
        TextView title = view.findViewById(R.id.schedulerDateTitle); //TODO: change to dropdown(?) to select week
        recipeNameBox = view.findViewById(R.id.schedulerRecipeName);
        Button cancelBtn = view.findViewById(R.id.schedulerCancelBtn);
        Button addBtn = view.findViewById(R.id.schedulerAddBtn);

        // Set title date range
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        currentDate = format.format(c.getTime());
        title.setText(getCurrentWeek(c, format));

        // Add adapter to recipeName field
        ArrayAdapter<String> recipeNamesAdapter = new ArrayAdapter<>(getContext(),
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

    private String getCurrentWeek(Calendar c, SimpleDateFormat dateFormat) {
        c.setFirstDayOfWeek(Calendar.SUNDAY); //TODO: change to query preferences for first day of week
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int offset;
        if (dayOfWeek == 1) {
            offset = -6;
        } else {
            offset = 2 - dayOfWeek;
        }
        c.add(Calendar.DAY_OF_YEAR, offset);
        currentWeekStartDate = dateFormat.format(c.getTime());
        newScheduleItem.setStartDate(currentWeekStartDate);
        c.add(Calendar.DAY_OF_YEAR, 6);
        currentWeekEndDate = dateFormat.format(c.getTime());
        newScheduleItem.setEndDate(currentWeekEndDate);
        return currentWeekStartDate + " - " + currentWeekEndDate;
    }

    private boolean addToDb() {
        recipeName = recipeNameBox.getText().toString();
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
