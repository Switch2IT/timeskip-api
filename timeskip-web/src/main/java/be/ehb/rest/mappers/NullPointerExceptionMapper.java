package be.ehb.rest.mappers;

import be.ehb.exceptions.ErrorCodes;
import be.ehb.factories.ResponseFactory;
import be.ehb.model.responses.ErrorResponse;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */
@Provider
@ApplicationScoped
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

    /**
     * @see ExceptionMapper#toResponse(Throwable)
     */
    @Override
    public Response toResponse(NullPointerException data) {

        ErrorResponse error = new ErrorResponse();
        if (StringUtils.isNotEmpty(data.getMessage())) {
            error.setMessage(data.getMessage());
        } else {
            //TODO - Use resource bundle for internationalization
            error.setMessage("Missing input");
            throw data;
        }
        error.setHttpCode(Response.Status.BAD_REQUEST.getStatusCode());
        error.setErrorCode(ErrorCodes.INVALID_INPUT);
        return ResponseFactory.buildResponse(Response.Status.BAD_REQUEST, "X-Timeskip-Error", "true", error);
    }
}