package odkbuilder.model;

public class DateQuestionNode extends FormNode {
    public DateQuestionNode(String name, String label) {
        super(name, label);
    }

    @Override
    public String getXlsFormType() {
        return "date";
    }

    @Override
    public String getDisplayType() {
        return "Date";
    }
}
