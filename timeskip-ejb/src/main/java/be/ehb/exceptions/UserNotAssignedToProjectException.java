package be.ehb.exceptions;

import javax.ws.rs.core.Response;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */
public class UserNotAssignedToProjectException extends AbstractUserException {

    public UserNotAssignedToProjectException(String message) {
        super(message);
    }

    public UserNotAssignedToProjectException(Throwable cause) {
        super(cause);
    }

    @Override
    public Response.Status getHttpCode() {
        return Response.Status.PRECONDITION_FAILED;
    }

    @Override
    public int getErrorCode() {
        return ErrorCodes.USER_NOT_ASSIGNED_TO_PROJECT;
    }
}