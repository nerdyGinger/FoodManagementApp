package apps.nerdyginger.pocketpantry.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.EmptyRecyclerView;
import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.helpers.ScheduleHelper;
import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.adapters.HistoryListAdapter;
import apps.nerdyginger.pocketpantry.dao.UserRecipeBoxDao;
import apps.nerdyginger.pocketpantry.models.HistoryComboItem;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;
import apps.nerdyginger.pocketpantry.models.UserSchedule;
import apps.nerdyginger.pocketpantry.view_models.ScheduleViewModel;

public class ScheduleHistoryDialog extends DialogFragment {
    private ScheduleHelper scheduleHelper;
    private ScheduleViewModel viewModel;
    private List<UserSchedule> completedList;
    private HistoryListAdapter adapter;

    public ScheduleHistoryDialog() {
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_schedule_history, container, false);

        // Initialize schedule helper
        scheduleHelper = new ScheduleHelper(getContext());

        // Find views
        EmptyRecyclerView rv = view.findViewById(R.id.historyRecycler);
        Button okBtn = view.findViewById(R.id.historyOkBtn);

        // Handle btn clicks
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Set up rv with data
        rv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        adapter = new HistoryListAdapter();
        rv.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        viewModel.getScheduleList().observe(getViewLifecycleOwner(), new Observer<List<UserSchedule>>() {
            @Override
            public void onChanged(List<UserSchedule> userSchedules) {
                completedList = new ArrayList<>();
                for (int i=0; i<userSchedules.size(); i++) {
                    if (userSchedules.get(i).isCompleted()) {
                        completedList.add(userSchedules.get(i));
                    }
                }
                List<HistoryComboItem> items = getHistoryItems(completedList);
                adapter.updateData(items);
            }
        });
        rv.setEmptyView(view.findViewById(R.id.historyEmpty));

        return view;
    }

    private String currentHeader = "";

    private List<HistoryComboItem> getHistoryItems(final List<UserSchedule> scheduleItems) {
        final List<HistoryComboItem> historyItems = new ArrayList<>();
        if (scheduleItems.size() == 0) {
            // just return empty list of no history
            return historyItems;
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                UserRecipeBoxDao boxDao = db.getUserRecipeBoxDao();
                for (int i=0; i<scheduleItems.size(); i++) {
                    HistoryComboItem comboItem = new HistoryComboItem();
                    UserSchedule scheduleItem = scheduleItems.get(i);
                    if ( ! currentHeader.equals(scheduleItem.getStartDate() + " - " + scheduleItem.getEndDate())) {
                        comboItem.setSectionHeader(true);
                        comboItem.setSectionDateRange(scheduleHelper.getWeekRange(scheduleItem.getDateCompleted()));
                        currentHeader = comboItem.getSectionDateRange();
                        historyItems.add(comboItem);
                        comboItem = new HistoryComboItem();
                    }
                    UserRecipeBoxItem boxItem = boxDao.getRecipeById(Integer.parseInt(scheduleItem.getRecipeBoxItemId()));
                    comboItem.setRecipeName(boxItem.getRecipeName());
                    comboItem.setCompletedDate(scheduleItem.getDateCompleted());
                    comboItem.setStartDate(scheduleItem.getStartDate());
                    comboItem.setEndDate(scheduleItem.getEndDate());
                    comboItem.setSectionHeader(false);
                    historyItems.add(comboItem);
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Database Error", "Problem waiting for db thread to complete");
        }
        return historyItems;
    }

}
