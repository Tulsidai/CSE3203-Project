package odkbuilder.model;

// A leaf of the composite. Nothing can go inside a text question.
public class TextQuestionNode extends QuestionNode {

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
