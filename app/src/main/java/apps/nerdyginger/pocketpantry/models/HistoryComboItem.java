package apps.nerdyginger.pocketpantry.models;

public class HistoryComboItem {
    private String recipeName;
    private String startDate;
    private String endDate;
    private String completedDate;

    private boolean isSectionHeader;
    private String sectionDateRange;

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
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

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public boolean isSectionHeader() {
        return isSectionHeader;
    }

    public void setSectionHeader(boolean sectionHeader) {
        isSectionHeader = sectionHeader;
    }

    public String getSectionDateRange() {
        return sectionDateRange;
    }

    public void setSectionDateRange(String sectionDateRange) {
        this.sectionDateRange = sectionDateRange;
    }
}
