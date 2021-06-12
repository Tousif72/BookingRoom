

package org.lagoontech.bookingroom.validation;

/**
 * @author hannu
 */
public interface Validator<T> {
    /**
     * Validates the object.
     * 
     * @param object The object to validate
     * @return The result of validation
     */
    ValidationResult fullValidate(T object);
}
