package apps.nerdyginger.cleanplateclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
                                                               RecipesFragment.OnFragmentInteractionListener,
                                                               InventoryFragment.OnFragmentInteractionListener,
                                                               ListsFragment.OnFragmentInteractionListener,
                                                               TipsFragment.OnFragmentInteractionListener,
                                                               AddInventoryDialog.AddInventoryDialogListener {
    private String fragmentTag;

    //This is the main activity (fittingly named, ya?) that handles the interactions from the
    //main tabbed screens
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomnav);


        //Direct children pages back to the right parent
        Intent inIntent = getIntent();
        String page = inIntent.getStringExtra("page");
        if (savedInstanceState != null) {
            page = savedInstanceState.getString("FragmentTag");
        }

        if (page != null) {
            switch(page) {
                case "Inventory":
                    fragmentTag = "Inventory";
                    loadFragment(new InventoryFragment());
                    bottomNav.setSelectedItemId(R.id.inventory);
                    break;
                case "Recipes":
                    fragmentTag = "Recipes";
                    loadFragment(new RecipesFragment());
                    bottomNav.setSelectedItemId(R.id.recipes);
                    break;
                case "Lists":
                    fragmentTag = "Lists";
                    loadFragment(new ListsFragment());
                    bottomNav.setSelectedItemId(R.id.lists);
                    break;
                case "Tips":
                    fragmentTag = "Tips";
                    loadFragment(new TipsFragment());
                    bottomNav.setSelectedItemId(R.id.tips);
                    break;
                case "Home":
                    fragmentTag = "Home";
                    loadFragment(new HomeFragment());
                    bottomNav.setSelectedItemId(R.id.home);
                    break;
            }
        } else {
            fragmentTag = "Home";
            loadFragment(new HomeFragment());
            bottomNav.setSelectedItemId(R.id.home);
        }

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.toString()) {
                    case "Home":
                        fragmentTag = "Home";
                        loadFragment(new HomeFragment());
                        return true;
                    case "Tips":
                        fragmentTag = "Tips";
                        loadFragment(new TipsFragment());
                        return true;
                    case "Recipes":
                        fragmentTag = "Recipes";
                        loadFragment(new RecipesFragment());
                        return true;
                    case "Lists":
                        fragmentTag = "Lists";
                        loadFragment(new ListsFragment());
                        return true;
                    case "Inventory":
                        fragmentTag = "Inventory";
                        loadFragment(new InventoryFragment());
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("FragmentTag", fragmentTag);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {    }

    @Override
    public void onAddInventoryDialogNegativeClick(AddInventoryDialog dialog) {
        dialog.dismiss();
    }

    @Override
    public void onAddInventoryDialogPositiveClick(AddInventoryDialog dialog, String itemName, int quantity,
                                                  String unitName, int stockLevel) {
        InventoryFragment frag = new InventoryFragment();
        frag.addItem(getApplicationContext(), itemName, quantity, unitName, stockLevel);
        dialog.dismiss();
    }
}
