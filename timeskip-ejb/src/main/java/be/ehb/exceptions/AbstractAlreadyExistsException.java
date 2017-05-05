package be.ehb.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */

abstract class AbstractAlreadyExistsException extends AbstractRestException {

    AbstractAlreadyExistsException() {
        super();
    }

    AbstractAlreadyExistsException(String message) {
        super(message);
    }

    AbstractAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    AbstractAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getHttpCode() {
        return Response.Status.CONFLICT.getStatusCode();
    }
}