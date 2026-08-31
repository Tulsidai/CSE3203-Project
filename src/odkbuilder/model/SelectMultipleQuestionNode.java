package odkbuilder.model;

public class SelectMultipleQuestionNode extends SelectQuestionNode {
    public SelectMultipleQuestionNode(String name, String label, String listName) {
        super(name, label, listName);
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
