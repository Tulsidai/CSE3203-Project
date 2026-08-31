package odkbuilder.model;

public class SelectMultipleQuestionNode extends SelectQuestionNode {
    public SelectMultipleQuestionNode(String name, String label) {
        super(name, label);
    }

    @Override
    protected String getSelectKeyword() {
        return "select_multiple";
    }
    @Override
    public String getDisplayType() {
        return "Select Multiple";
    }
}
