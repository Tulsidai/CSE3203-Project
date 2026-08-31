package odkbuilder.model;

// leaf.
public class SelectOneQuestionNode extends SelectQuestionNode {

    public SelectOneQuestionNode(String name, String label, String listName) {
        super(name, label, listName);
    }

    @Override
    protected String getSelectKeyword() {
        return "select_one";
    }
    @Override
    public String getDisplayType() {
        return "Select One";
    }
}
