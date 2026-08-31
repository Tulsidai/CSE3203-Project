package odkbuilder.model;

public class NoteQuestionNode extends QuestionNode {
    public NoteQuestionNode(String name, String label) {
        super(name, label);
    }

    @Override
    public String getXlsFormType() {
        return "note";
    }

    @Override
    public String getDisplayType() {
        return "Note";
    }
}
