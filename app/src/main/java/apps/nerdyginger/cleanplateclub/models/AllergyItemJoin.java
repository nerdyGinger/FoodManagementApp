package apps.nerdyginger.cleanplateclub.models;

public class AllergyItemJoin {
    private int allergyId;
    private int itemId;

    public int getAllergyId() {
        return allergyId;
    }

    public void setAllergyId(int allergyId) {
        this.allergyId = allergyId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public AllergyItemJoin(int allergyId, int itemId) {
        this.allergyId = allergyId;
        this.itemId = itemId;
    }
}
