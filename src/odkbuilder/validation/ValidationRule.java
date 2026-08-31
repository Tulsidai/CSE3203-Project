package odkbuilder.validation;

import java.util.ArrayList;
import odkbuilder.model.Form;

public interface ValidationRule {
    ArrayList<ValidationError> check(Form form);

    String getRuleName();
}
