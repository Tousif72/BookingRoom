

package org.lagoontech.bookingroom.validation;

/**
 * @author hannu
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = -8754467148756239632L;
    private final ValidationResult errors;

    public ValidationException(ValidationResult errors, String detailMessage) {
        super(detailMessage);
        this.errors = errors;
    }

    public ValidationResult getErrors() {
        return errors;
    }
}
