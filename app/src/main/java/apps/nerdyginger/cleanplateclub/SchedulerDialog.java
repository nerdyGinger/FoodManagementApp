package apps.nerdyginger.cleanplateclub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SchedulerDialog extends DialogFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scheduler_dialog, container, false);

        // Find views
        TextView title = view.findViewById(R.id.schedulerDateTitle); //TODO: change to dropdown(?) to select week
        AutoCompleteTextView recipeName = view.findViewById(R.id.schedulerRecipeName);
        Button cancelBtn = view.findViewById(R.id.schedulerCancelBtn);
        Button addBtn = view.findViewById(R.id.schedulerAddBtn);

        // Set title date range
        //DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        //LocalDateTime now = LocalDateTime.now();
        //String today



        return view;
    }

}
