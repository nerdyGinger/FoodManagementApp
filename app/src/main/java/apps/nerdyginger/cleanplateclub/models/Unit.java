package apps.nerdyginger.cleanplateclub.models;

public class Unit {
    private int _ID;
    private String fullName;
    private String abbreviation;
    private int systemId;
    private String type;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Unit(int _ID, String fullName, String abbreviation, int systemId, String type) {
        this._ID = _ID;
        this.fullName = fullName;
        this.abbreviation = abbreviation;
        this.systemId = systemId;
        this.type = type;
    }
}
