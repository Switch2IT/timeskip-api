package be.ehb.exceptions;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */
public class DefaultConfigNotFoundException extends AbstractSystemException {

    @Override
    public int getErrorCode() {
        return ErrorCodes.getSystemError();
    }
}