package odkbuilder.validation;

import java.util.ArrayList;
import odkbuilder.model.ChoiceList;
import odkbuilder.model.Form;
import odkbuilder.model.FormNode;
import odkbuilder.model.SelectQuestionNode;

/*
 * Three different things can go wrong, reported separate so the user knows
 * which one it is:
 *   1. no list name at all
 *   2. names a list that does not exist on the form
 *   3. list exists but empty
 */
public class ChoiceListRule implements ValidationRule {

    @Override
    public String getRuleName() {
        return "Choice lists";
    }

    @Override
    public ArrayList<ValidationError> check(Form form) {
        ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
        ArrayList<FormNode> nodes = form.collectAllNodes();

        for (int i = 0; i < nodes.size(); i++) {
            FormNode node = nodes.get(i);

            //Only select questions matter here, and the tree hands over every node there is.
            if (!(node instanceof SelectQuestionNode)) {
                continue;
            }
            SelectQuestionNode sq = (SelectQuestionNode) node;
            String listName = sq.getListName();

            if (listName == null || listName.trim().equals("")) {
                errors.add(new ValidationError(node,
                        "This select question has no choice list assigned."));
                continue;
            }

            ChoiceList list = form.findChoiceList(listName);
            if (list == null) {
                errors.add(new ValidationError(node,
                        "Choice list '" + listName + "' does not exist on this form."));
            } else if (list.getChoices().isEmpty()) {
                errors.add(new ValidationError(node,
                        "Choice list '" + listName + "' has no choices in it."));
            }
        }
        return errors;
    }
}
