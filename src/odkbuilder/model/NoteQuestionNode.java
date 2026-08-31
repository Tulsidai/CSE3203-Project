package odkbuilder.model;

/*
 * leaf of the composite.
 *
 * A note is just text to read, no answer.
 * Still a QuestionNode because on the survey sheet it is a row like
 * any other, and it can carry a relevant.
 */
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
