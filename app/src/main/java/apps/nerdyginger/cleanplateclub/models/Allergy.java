package apps.nerdyginger.cleanplateclub.models;

public class Allergy {
    private int _ID;
    private String name;

    public Allergy(int id, String name) {
        this._ID = id;
        this.name = name;
    }

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
}
