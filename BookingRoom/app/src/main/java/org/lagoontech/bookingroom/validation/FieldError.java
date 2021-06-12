

package org.lagoontech.bookingroom.validation;

/**
 * Class for modeling an error on a certain field in an object.
 * 
 * @author hannu
 */
public class FieldError extends ObjectError {
    private final String field;

    public FieldError(Object target, String field, String code, String message) {
        super(target, code, message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
