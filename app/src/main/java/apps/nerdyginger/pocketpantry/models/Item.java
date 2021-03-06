package apps.nerdyginger.pocketpantry.models;

public class Item {
    private int _ID;
    private String name;

    public int get_ID() {
        return _ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public Item(int _ID, String name, int flavor, int category) {
        this._ID = _ID;
        this.name = name;
    }
}
