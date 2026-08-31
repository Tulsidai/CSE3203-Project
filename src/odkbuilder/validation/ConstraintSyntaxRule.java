package odkbuilder.validation;

import java.util.ArrayList;
import odkbuilder.model.Form;
import odkbuilder.model.FormNode;
import odkbuilder.model.QuestionNode;

/*
 * Not a whole XPath parser, that is way outside the scope here.
 *
 * It checks what a non-technical user actually gets wrong typing an
 * expresion by hand: brackets that do not balance, an odd number of
 * quotes, and a constraint message with no constraint.
 */
public class ConstraintSyntaxRule implements ValidationRule {

    @Override
    public String getRuleName() {
        return "Constraint syntax";
    }

    @Override
    public ArrayList<ValidationError> check(Form form) {
        ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
        ArrayList<FormNode> nodes = form.collectAllNodes();
        for (int i = 0; i < nodes.size(); i++) {
            FormNode node = nodes.get(i);
            if (!(node instanceof QuestionNode)) {
                continue;
            }
            QuestionNode q = (QuestionNode) node;
            checkExpression(q, q.getConstraint(), "Constraint", errors);
            checkExpression(q, q.getRelevant(), "Relevance", errors);
            boolean hasConstraint = q.getConstraint() != null
                    && !q.getConstraint().trim().equals("");
            boolean hasMessage = q.getConstraintMessage() != null
                    && !q.getConstraintMessage().trim().equals("");

            if (hasMessage && !hasConstraint) {
                errors.add(new ValidationError(node,
                        "There is a constraint message but no constraint, so the "
                        + "message will never be shown."));
            }
        }
        return errors;
    }

    //Counts brackets and quotes. Its own method because constraint and relevance are
    //both XPath and get the exact same treatment.
    private void checkExpression(FormNode node, String expr, String what,
            ArrayList<ValidationError> errors) {

        if (expr == null || expr.trim().equals("")) {
            return;
        }
        int open = 0;
        int quotes = 0;

        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);

            if (ch == '(') {
                open++;
            } else if (ch == ')') {
                open--;
                if (open < 0) {
                    //Closed one too many. No point counting further.
                    errors.add(new ValidationError(node,
                            what + " has a closing bracket with no opening bracket."));
                    return;
                }
            } else if (ch == '\'' || ch == '"') {
                quotes++;
            }
        }
        if (open > 0) {
            errors.add(new ValidationError(node,
                    what + " has " + open + " unclosed bracket(s)."));
        }
        if (quotes % 2 != 0) {
            errors.add(new ValidationError(node,
                    what + " has an odd number of quote marks."));
        }

        /*
         * In XLSForm a constraint talks about its own answer with a full
         * stop. People coming from Excel forget it every time.
         *
         * TODO: fires wrong on something like "regex(., '^[0-9]+$')" typed
         * without the dot. Low priority, it only warns.
         */
        if (what.equals("Constraint") && expr.indexOf('.') < 0) {
            errors.add(new ValidationError(node,
                    "Constraint does not mention '.', which is how XLSForm "
                    + "refers to this question's own answer."));
        }
    }
}
