package apps.nerdyginger.pocketpantry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ListsFragment.OnFragmentInteractionListener,
                                                               TipsFragment.OnFragmentInteractionListener {
    private String fragmentTag;
    private Fragment currentFragment;
    private DrawerLayout navLayout;
    private boolean drawerOpen = false;

    //This is the main activity (fittingly named, ya?) that handles the interactions from the
    //main tabbed screens
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_SHORT).show();
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

    void setCurrentFragment(Fragment fragment) {
        currentFragment = fragment;
    }

    private void settingsClick() {
        //open preferences
    }

    private void aboutClick() {
        //open about page
    }

    private void contactClick() {
        //open contact page
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("FragmentTag", fragmentTag);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {    }
}
