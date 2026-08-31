package odkbuilder.validation;

import java.util.ArrayList;
import odkbuilder.model.Form;
import odkbuilder.model.FormNode;

public class UniqueNameRule implements ValidationRule {
    @Override
    public String getRuleName() {
        return "Unique names";
    }

    @Override
    public ArrayList<ValidationError> check(Form form) {
        ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
        ArrayList<FormNode> nodes = form.collectAllNodes();
        ArrayList<String> alreadyReported = new ArrayList<String>();
        for (int i = 0; i < nodes.size(); i++) {
            String name = nodes.get(i).getName();
            if (name == null || name.trim().equals("")) {
                continue;
            }
            for (int j = i + 1; j < nodes.size(); j++) {
                String other = nodes.get(j).getName();
                if (name.equals(other) && !alreadyReported.contains(name)) {
                    errors.add(new ValidationError(nodes.get(j),
                            "The name '" + name + "' is already used by another "
                            + "question. Names must be unique in the whole form."));
                    alreadyReported.add(name);
                }
            }
        }
        return errors;
    }
}
