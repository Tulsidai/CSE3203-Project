package odkbuilder.validation;

import java.util.ArrayList;
import odkbuilder.model.Form;

/*
 * The strategy interface. Every rule is its own class with this one
 * method.
 *
 * Better than one big validate() full of if statements. Adding a rule
 * later is one new class plus one line in FormValidator.
 * Nothing already written gets touched.
 */
public interface ValidationRule {

    ArrayList<ValidationError> check(Form form);

    // So the panel can say which rule complained.
    String getRuleName();
}
