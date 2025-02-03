package uk.ac.newcastle.enterprisemiddleware.taxi;

import javax.validation.ValidationException;

public class UniqueRegistrationException extends ValidationException {

    public UniqueRegistrationException(String message) {
        super(message);
    }

    public UniqueRegistrationException(String message, Throwable cause) { super(message, cause); }

    public UniqueRegistrationException(Throwable cause) { super(cause); }
}
