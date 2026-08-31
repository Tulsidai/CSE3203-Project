package odkbuilder.validation;

import java.util.ArrayList;
import odkbuilder.model.Form;
import odkbuilder.model.FormNode;

// every row on the survey sheet needs a label, else the enumerator opens the form and
// sees a blank question staring back at them.
public class LabelRule implements ValidationRule {

    @Override
    public String getRuleName() {
        return "Label present";
    }

    @Override
    public ArrayList<ValidationError> check(Form form) {
        ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
        ArrayList<FormNode> nodes = form.collectAllNodes();

        for (int i = 0; i < nodes.size(); i++) {
            FormNode node = nodes.get(i);
            if (node.getLabel() == null || node.getLabel().trim().equals("")) {
                errors.add(new ValidationError(node,
                        "Label is empty. The enumerator would see a blank question."));
            }
        }
        return errors;
    }
}
