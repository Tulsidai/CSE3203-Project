package odkbuilder.model;

public class TextQuestionNode extends FormNode {
    public TextQuestionNode(String name, String label) {
        super(name, label);
    }

    @Override
    public String getXlsFormType() {
        return "text";
    }
    @Override
    public String getDisplayType() {
        return "Text";
    }
}
