package odkbuilder.model;

public class NoteQuestionNode extends FormNode {
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
