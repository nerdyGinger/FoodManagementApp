package apps.nerdyginger.pocketpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {private DrawerLayout navLayout;
    private boolean drawerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        //Set up toolbar
        androidx.appcompat.widget.Toolbar toolBar = findViewById(R.id.toolbar);
        TextView title = findViewById(R.id.toolbarTitle);
        title.setText("Settings");
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}