package uk.ac.newcastle.enterprisemiddleware.travelagent;

import javax.validation.ValidationException;

public class UniqueTravelAgentBookingException extends ValidationException {

    public UniqueTravelAgentBookingException(String message) {
        super(message);
    }

    public UniqueTravelAgentBookingException(String message, Throwable cause) { super(message, cause); }

    public UniqueTravelAgentBookingException(Throwable cause) { super(cause); }
}
