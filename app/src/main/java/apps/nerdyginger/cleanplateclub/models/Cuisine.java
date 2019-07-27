package apps.nerdyginger.cleanplateclub.models;

public class Cuisine {
    private int _ID;
    private String name;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cuisine(int _ID, String name) {
        this._ID = _ID;
        this.name = name;
    }
}
