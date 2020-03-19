package apps.nerdyginger.pocketpantry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import apps.nerdyginger.pocketpantry.helpers.ImageHelper;

/*
 * Main hub for the action, handling the fragments for all of the other main pages and
 * controlling access to/from nav drawer activities.
 * Last edited: 3/18/20
 */
public class MainActivity extends AppCompatActivity {
    private String fragmentTag;
    private Fragment currentFragment;
    private DrawerLayout navLayout;
    private boolean drawerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if first run setup is needed
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                getBaseContext().getPackageName() + ".database_versions", MODE_PRIVATE);
        int version = prefs.getInt("imageStoreStatus", 0);
        if (version == 0) {
            // images have not yet been downloaded
            // NOTE: Other values can be set to "imageStoreStatus", so it can function as a version
            // number, but for now it works like a boolean
            // TODO-VER1.0: show a splashscreen/loading screen for loading time
            ImageHelper imageHelper = new ImageHelper(getApplicationContext());
            imageHelper.loadAllDbImages(); //this will also load the db, which is useful if it hasn't been loaded yet
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("imageStoreStatus", 1);
            editor.apply();
        }

        //Set up toolbar
        androidx.appcompat.widget.Toolbar toolBar = findViewById(R.id.toolbar);
        TextView title = findViewById(R.id.toolbarTitle);
        title.setText(R.string.app_name);
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

        //Set up bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomnav);
        Intent inIntent = getIntent();
        String page = inIntent.getStringExtra("page");
        if (savedInstanceState != null) {
            page = savedInstanceState.getString("FragmentTag");
        }

        if (page != null) {
            switch(page) {
                case "Inventory":
                    fragmentTag = "Inventory";
                    currentFragment = new InventoryFragment();
                    loadFragment(currentFragment);
                    bottomNav.setSelectedItemId(R.id.inventory);
                    break;
                case "Recipes":
                    fragmentTag = "Recipes";
                    currentFragment = new RecipesFragment();
                    loadFragment(currentFragment);
                    bottomNav.setSelectedItemId(R.id.recipes);
                    break;
                case "Lists":
                    fragmentTag = "Lists";
                    currentFragment = new ListsFragment();
                    loadFragment(currentFragment);
                    bottomNav.setSelectedItemId(R.id.lists);
                    break;
                case "Tips":
                    fragmentTag = "Tips";
                    currentFragment = new TipsFragment();
                    loadFragment(currentFragment);
                    bottomNav.setSelectedItemId(R.id.tips);
                    break;
                case "Home":
                    fragmentTag = "Home";
                    currentFragment = new HomeFragment();
                    loadFragment(currentFragment);
                    bottomNav.setSelectedItemId(R.id.home);
                    break;
            }
        } else {
            fragmentTag = "Home";
            currentFragment = new HomeFragment();
            loadFragment(currentFragment);
            bottomNav.setSelectedItemId(R.id.home);
        }

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.toString()) {
                    case "Home":
                        fragmentTag = "Home";
                        currentFragment = new HomeFragment();
                        loadFragment(currentFragment);
                        return true;
                    case "Tips":
                        fragmentTag = "Tips";
                        currentFragment = new TipsFragment();
                        loadFragment(currentFragment);
                        return true;
                    case "Recipes":
                        fragmentTag = "Recipes";
                        currentFragment = new RecipesFragment();
                        loadFragment(currentFragment);
                        return true;
                    case "Lists":
                        fragmentTag = "Lists";
                        currentFragment = new ListsFragment();
                        loadFragment(currentFragment);
                        return true;
                    case "Inventory":
                        fragmentTag = "Inventory";
                        currentFragment = new InventoryFragment();
                        loadFragment(currentFragment);
                        return true;
                }
                return true;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("FragmentTag", fragmentTag);
    }
}
