package apps.nerdyginger.pocketpantry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class ContactActivity extends AppCompatActivity {
    private boolean messageSuccess;
    private TextInputEditText messageBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Set up submission button and message box
        final Button submitBtn = findViewById(R.id.contactSubmitBtn);
        submitBtn.setEnabled(false);
        messageBox = findViewById(R.id.contactMessage);
        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(messageBox.getText()).toString().equals("")) {
                    submitBtn.setEnabled(false);
                } else {
                    submitBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(Objects.requireNonNull(messageBox.getText()).toString());
                if (messageSuccess) {
                    submitBtn.setEnabled(false);
                }
            }
        });
    }

    public void sendEmail(String message) {
        BackgroundMail.newBuilder(this)
                .withUsername("gingerthenerd@gmail.com")
                .withPassword("Ju5tSh1n3*")
                .withMailTo("gingerthenerd@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Pocket Pantry: COMMENT")
                .withBody(message)
                .withSendingMessage("Sending...")
                .withOnSuccessCallback(new BackgroundMail.OnSendingCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                        messageBox.setText("");
                        messageSuccess = true;
                    }

                    @Override
                    public void onFail(Exception e) {
                        Toast.makeText(getApplicationContext(), "Unable to send email", Toast.LENGTH_SHORT).show();
                        Log.e("MAIL_DEBUG", e.toString());
                        messageSuccess = false;
                    }
                })
                .send();
    }
}
