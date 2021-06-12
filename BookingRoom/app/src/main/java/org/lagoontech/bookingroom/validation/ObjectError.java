
package org.lagoontech.bookingroom.validation;

/**
 * Class for modeling an error on the object.
 * 
 * @author hannu
 */
public class ObjectError {
    private final Object target;
    private final String code;
    private final String message;

    public ObjectError(Object target, String code, String message) {
        this.target = target;
        this.code = code;
        this.message = message;
    }

    public Object getTarget() {
        return target;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
