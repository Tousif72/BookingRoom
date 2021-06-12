
package org.lagoontech.bookingroom.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class holding the results of the validation.
 * 
 * @author hannu
 * @see Validator#fullValidate(Object)
 */
public class ValidationResult {
    private final List<ObjectError> errors;

    public ValidationResult() {
        errors = new ArrayList<ObjectError>();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public ObjectError addError(ObjectError e) {
        errors.add(e);
        return e;
    }

    public void addAll(ValidationResult result) {
        errors.addAll(result.getErrors());
    }

    public List<ObjectError> getErrors() {
        return errors;
    }
}
