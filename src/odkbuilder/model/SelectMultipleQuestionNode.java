package odkbuilder.model;

// Leaf. Only differs from SelectOne by one word.
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
