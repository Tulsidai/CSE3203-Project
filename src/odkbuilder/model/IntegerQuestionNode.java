package odkbuilder.model;

// Leaf.
public class IntegerQuestionNode extends QuestionNode {

    public IntegerQuestionNode(String name, String label) {
        super(name, label);
    }

    @Override
    public String getXlsFormType() {
        return "integer";
    }

    @Override
    public String getDisplayType() {
        return "Integer";
    }
}
