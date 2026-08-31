package odkbuilder.model;

public class DecimalQuestionNode extends QuestionNode {
    public DecimalQuestionNode(String name, String label) {
        super(name, label);
    }

    @Override
    public String getXlsFormType() {
        return "decimal";
    }
    @Override
    public String getDisplayType() {
        return "Decimal";
    }
}
