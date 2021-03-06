package be.ehb.exceptions;

/**
 * @author Patrick Van den Bussche/Guillaume Vandecasteele
 * @since 2017
 */
public class SchedulerNotFoundException extends AbstractSystemException {

    @Override
    public int getErrorCode() {
        return ErrorCodes.getSchedulerNotFound();
    }
}