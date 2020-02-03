package apps.nerdyginger.pocketpantry.view_models;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.UserCustomDatabase;
import apps.nerdyginger.pocketpantry.dao.ItemDao;
import apps.nerdyginger.pocketpantry.dao.UserItemDao;
import apps.nerdyginger.pocketpantry.dao.UserListItemDao;
import apps.nerdyginger.pocketpantry.models.Item;
import apps.nerdyginger.pocketpantry.models.UserItem;
import apps.nerdyginger.pocketpantry.models.UserListItem;

public class AddListItemDialog extends DialogFragment {
    private AutoCompleteTextView itemNameBox;
    private EditText itemQuantityBox;
    private Button cancelBtn;
    private Button addBtn;
    private Dictionary<String, Integer> readOnlyNameIdValues = new Hashtable<>();
    private Dictionary<String, Integer> userNameIdValues = new Hashtable<>();

    public AddListItemDialog() {
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_list_item, container, false);

        // Get views
        itemNameBox = view.findViewById(R.id.listDialogItemName);
        itemQuantityBox = view.findViewById(R.id.listDialogItemQuantity);
        cancelBtn = view.findViewById(R.id.listDialogCancelBtn);
        addBtn = view.findViewById(R.id.listDialogAddButton);

        // Add item name data adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_dropdown_item_1line, getItems());
        itemNameBox.setAdapter(adapter);

        // Handle button click events
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDb(itemNameBox.getText().toString(), itemQuantityBox.getText().toString());
                dismiss();
            }
        });

        return view;
    }

    private void saveToDb(final String itemName, final String quantity) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(getContext());
                UserListItemDao listDao = db.getUserListItemDao();
                UserListItem item = new UserListItem();
                item.setItemName(itemName);
                item.setQuantity(quantity);
                item.setChecked(false);
                Integer readonlyId = readOnlyNameIdValues.get(itemName);
                Integer userItemId = userNameIdValues.get(itemName);
                if (readonlyId != null) {
                    item.setUserAdded(false);
                    item.setItemID(readonlyId);
                } else if (userItemId != null) {
                    item.setUserAdded(true);
                    item.setItemID(userItemId);
                } else {
                    //item was not found at all, create new UserItem
                    UserItemDao userItemDao = db.getUserItemDao();
                    UserItem newItem = new UserItem();
                    newItem.setName(itemName);
                    userItemDao.insert(newItem);
                    item.setUserAdded(true);
                    item.setItemID(userItemDao.getItemIdFromName(itemName));
                }

                //save to lists db table
                listDao.insert(item);
            }
        });
        t.start();
        try {
            t.join();
        } catch(Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
    }

    private List<String> getItems() {
        final List<String> itemNames = new ArrayList<>();

        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    ItemDao dbDao = new ItemDao(getContext());
                    List<Item> dbItems = dbDao.getAllItems();
                    for (int i=0; i<dbItems.size(); i++) {
                        readOnlyNameIdValues.put(dbItems.get(i).getName(), dbItems.get(i).get_ID());
                        itemNames.add(dbItems.get(i).getName());
                    }
                    UserItemDao userDao = UserCustomDatabase.getDatabase(getContext()).getUserItemDao();
                    List<UserItem> userItems = userDao.getAllUserItems();
                    for (int i=0; i<userItems.size(); i++) {
                        userNameIdValues.put(userItems.get(i).getName(), userItems.get(i).get_ID());
                        itemNames.add(userItems.get(i).getName());
                    }
                }
            });
            t.start();
            t.join();
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }

        return itemNames;
    }
}
