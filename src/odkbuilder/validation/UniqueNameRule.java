package odkbuilder.validation;

import java.util.ArrayList;
import odkbuilder.model.Form;
import odkbuilder.model.FormNode;

/*
 * Two questions with the same name will overwrite one another answers
 * in the collected data, so this is probaly the most important rule of
 * the set. Two questions named author, one answer, and a field team
 * that already gone home.
 *
 * Unique across the WHOLE form, not just inside one group, because
 * XLSForm flattens groups when it builds the data instance.
 */
public class UniqueNameRule implements ValidationRule {

    @Override
    public String getRuleName() {
        return "Unique names";
    }

    // TODO: double loop, so O(n squared). Fine for 40 questions, would want a HashSet
    // for anything bigger.
    @Override
    public ArrayList<ValidationError> check(Form form) {
        ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
        ArrayList<FormNode> nodes = form.collectAllNodes();
        ArrayList<String> alreadyReported = new ArrayList<String>();
        for (int i = 0; i < nodes.size(); i++) {
            String name = nodes.get(i).getName();
            if (name == null || name.trim().equals("")) {
                continue; // empty name is NameSyntaxRule work, not this one
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
