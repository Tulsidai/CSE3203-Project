package odkbuilder.model;

public class SelectOneQuestionNode extends SelectQuestionNode {
    public SelectOneQuestionNode(String name, String label) {
        super(name, label);
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
