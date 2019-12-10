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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class ContactActivity extends AppCompatActivity {
    private boolean messageSuccess;
    private TextInputEditText messageBox;
    private DrawerLayout navLayout;
    private boolean drawerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //Set up toolbar
        androidx.appcompat.widget.Toolbar toolBar = findViewById(R.id.toolbar);
        TextView title = findViewById(R.id.toolbarTitle);
        title.setText(getString(R.string.contact_title));
        ImageButton menuBtn = findViewById(R.id.toolbarNavButton);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerOpen) {
                    navLayout.closeDrawer(GravityCompat.START);
                    drawerOpen = false;
                } else {
                    navLayout.openDrawer(GravityCompat.START);
                    drawerOpen = true;
                }
            }
        });
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayShowTitleEnabled(false);

        //Set up navigation drawer
        navLayout = findViewById(R.id.navDrawerLayout);
        NavigationView navView = findViewById(R.id.navDrawerView);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getTitle().equals("Settings")) {
                    settingsClick();
                } else if (menuItem.getTitle().equals("About")) {
                    aboutClick();
                } else if (menuItem.getTitle().equals("Contact Us")) {
                    contactClick();
                }
                return false;
            }
        });

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

    private void settingsClick() {
        //open preferences
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
        navLayout.closeDrawer(GravityCompat.START);
        drawerOpen = false;
    }

    private void aboutClick() {
        //open about page
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
        navLayout.closeDrawer(GravityCompat.START);
        drawerOpen = false;
    }

    private void contactClick() {
        //open contact page
        Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
        startActivity(intent);
        navLayout.closeDrawer(GravityCompat.START);
        drawerOpen = false;
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
