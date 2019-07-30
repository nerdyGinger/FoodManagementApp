package apps.nerdyginger.cleanplateclub.models;

import java.util.ArrayList;

public class Item {
    private int _ID;
    private String name;
    private int flavor;
    private int category;
<<<<<<< HEAD
=======
    private ArrayList<String> allergy;
>>>>>>> b4c9e1d1b2b961b0d93c0e970ce86a30e3df1126

    public int get_ID() {
        return _ID;
    }

    public String getName() {
        return name;
    }

    public int getFlavor() {
        return flavor;
    }

    public int getCategory() {
        return category;
    }

<<<<<<< HEAD
=======
    public ArrayList<String> getAllergy() {
        return allergy;
    }

>>>>>>> b4c9e1d1b2b961b0d93c0e970ce86a30e3df1126
    public void setName(String name) {
        this.name = name;
    }

    public void setFlavor(int flavor) {
        this.flavor = flavor;
    }

    public void setCategory(int category) {
        this.category = category;
    }

<<<<<<< HEAD
=======
    public void setAllergy(ArrayList<String> allergy) {
        this.allergy = allergy;
    }

>>>>>>> b4c9e1d1b2b961b0d93c0e970ce86a30e3df1126
    public void set_ID(int _ID) {
        this._ID = _ID;
    }

<<<<<<< HEAD
    public Item(int _ID, String name, int flavor, int category) {
=======
    public Item(int _ID, String name, int flavor, int category, ArrayList<String> allergy) {
>>>>>>> b4c9e1d1b2b961b0d93c0e970ce86a30e3df1126
        this._ID = _ID;
        this.name = name;
        this.flavor = flavor;
        this.category = category;
    }
}
