package odkbuilder.validation;

import java.util.ArrayList;
import odkbuilder.model.Form;

/*
 * The context object. Holds every rule and runs the lot of them.
 * A form has to satisfy all of them at once, not pick one.
 *
 * Outside this class nobody calls anything but validate(), so rules
 * can be added or dropped without touching the ViewModel or the view.
 */
public class FormValidator {

    private ArrayList<ValidationRule> rules = new ArrayList<ValidationRule>();

    public FormValidator() {
        rules.add(new NameSyntaxRule());
        rules.add(new UniqueNameRule());
        rules.add(new LabelRule());
        rules.add(new ChoiceListRule());
        rules.add(new ConstraintSyntaxRule());
    }

    public void addRule(ValidationRule rule) {
        rules.add(rule);
    }
    public ArrayList<ValidationRule> getRules() {
        return rules;
    }

    // every rule, every time. One flat list back.
    public ArrayList<ValidationError> validate(Form form) {
        ArrayList<ValidationError> all = new ArrayList<ValidationError>();
        for (int i = 0; i < rules.size(); i++) {
            ArrayList<ValidationError> found = rules.get(i).check(form);
            for (int j = 0; j < found.size(); j++) {
                all.add(found.get(j));
            }
        }
        return all;
    }
}
