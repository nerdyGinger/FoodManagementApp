package apps.nerdyginger.pocketpantry.view_models;

public class RecipeInstructionsViewModel {
    private String instructionText;
    private boolean expanded;

    public RecipeInstructionsViewModel() {
        //empty constructor
    }

    public RecipeInstructionsViewModel(String instructionText, boolean expanded) {
        this.instructionText = instructionText;
        this.expanded = expanded;
    }

    public String getInstructionText() {
        return instructionText;
    }

    public void setInstructionText(String instructionText) {
        this.instructionText = instructionText;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
