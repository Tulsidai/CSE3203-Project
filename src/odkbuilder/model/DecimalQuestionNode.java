package odkbuilder.model;

// leaf. Same as IntegerQuestionNode, just a different word.
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
