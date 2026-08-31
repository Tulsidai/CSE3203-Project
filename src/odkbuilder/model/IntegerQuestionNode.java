package odkbuilder.model;

public class IntegerQuestionNode extends FormNode {
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
