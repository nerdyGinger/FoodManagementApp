package apps.nerdyginger.cleanplateclub.models;

public class UnitConversion {
    private int _ID;
    private int fromUnitId;
    private int toUnitId;
    private String unitType;
    private int fromOffset;
    private int multiplicand;
    private int denominator;
    private int toOffset;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public int getFromUnitId() {
        return fromUnitId;
    }

    public void setFromUnitId(int fromUnitId) {
        this.fromUnitId = fromUnitId;
    }

    public int getToUnitId() {
        return toUnitId;
    }

    public void setToUnitId(int toUnitId) {
        this.toUnitId = toUnitId;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public int getFromOffset() {
        return fromOffset;
    }

    public void setFromOffset(int fromOffset) {
        this.fromOffset = fromOffset;
    }

    public int getMultiplicand() {
        return multiplicand;
    }

    public void setMultiplicand(int multiplicand) {
        this.multiplicand = multiplicand;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

    public int getToOffset() {
        return toOffset;
    }

    public void setToOffset(int toOffset) {
        this.toOffset = toOffset;
    }

    public UnitConversion(int _ID, int fromUnitId, int toUnitId, String unitType, int fromOffset, int multiplicand, int denominator, int toOffset) {
        this._ID = _ID;
        this.fromUnitId = fromUnitId;
        this.toUnitId = toUnitId;
        this.unitType = unitType;
        this.fromOffset = fromOffset;
        this.multiplicand = multiplicand;
        this.denominator = denominator;
        this.toOffset = toOffset;
    }
}
