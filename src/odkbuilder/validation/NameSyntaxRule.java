package odkbuilder.validation;

import java.util.ArrayList;
import odkbuilder.model.Form;
import odkbuilder.model.FormNode;

//XLSForm turns the name into an XML tag, and an XML tag cannot start with a digit or
//carry spaces. Same thing gets checked here first.
public class NameSyntaxRule implements ValidationRule {

    @Override
    public String getRuleName() {
        return "Name syntax";
    }

    @Override
    public ArrayList<ValidationError> check(Form form) {
        ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
        ArrayList<FormNode> nodes = form.collectAllNodes();

        for (int i = 0; i < nodes.size(); i++) {
            FormNode node = nodes.get(i);
            String name = node.getName();
            if (name == null || name.trim().equals("")) {
                errors.add(new ValidationError(node, "Name cannot be empty."));
                continue;
            }

            char first = name.charAt(0);
            if (!Character.isLetter(first) && first != '_') {
                errors.add(new ValidationError(node,
                        "Name must start with a letter or an underscore."));
            }

            //Break after the first bad character. One message per name
            //is enough, otherwise "a b c" floods the panel.
            for (int c = 0; c < name.length(); c++) {
                char ch = name.charAt(c);
                if (!Character.isLetterOrDigit(ch) && ch != '_') {
                    errors.add(new ValidationError(node,
                            "Name cannot contain '" + ch
                            + "'. Use letters, digits and underscores only."));
                    break;
                }
            }
        }
        return errors;
    }
}
