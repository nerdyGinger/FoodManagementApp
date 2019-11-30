package apps.nerdyginger.pocketpantry.models;

public class Item {
    private int _ID;
    private String name;
    private int flavor;
    private int category;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setFlavor(int flavor) {
        this.flavor = flavor;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public Item(int _ID, String name, int flavor, int category) {
        this._ID = _ID;
        this.name = name;
        this.flavor = flavor;
        this.category = category;
    }
}
