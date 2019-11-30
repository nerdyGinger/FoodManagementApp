package apps.nerdyginger.pocketpantry.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="schedule")
public class UserSchedule {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    private String startDate;
    private String endDate;
    private String recipeBoxItemId;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRecipeBoxItemId() {
        return recipeBoxItemId;
    }

    public void setRecipeBoxItemId(String recipeBoxItemId) {
        this.recipeBoxItemId = recipeBoxItemId;
    }
}
