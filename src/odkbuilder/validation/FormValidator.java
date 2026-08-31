package odkbuilder.validation;

import java.util.ArrayList;
import odkbuilder.model.Form;

public class FormValidator {
    private ArrayList<ValidationRule> rules = new ArrayList<ValidationRule>();

    public FormValidator() {
        rules.add(new NameSyntaxRule());
        rules.add(new UniqueNameRule());
    }

    public void addRule(ValidationRule rule) {
        rules.add(rule);
    }
    public ArrayList<ValidationRule> getRules() {
        return rules;
    }

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
