package be.ehb.exceptions;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */
class ErrorCodes {

    // IDP RELATED

    static final int IDP_ERROR = 4000;

    // ORGANIZATION RELATED

    static final int UNAUTHORIZED_FOR_ORGANIZATION = 1003;
    static final int ORGANIZATION_NOT_FOUND = 1004;
    static final int ORGANIZATION_ALREADY_EXISTS = 1005;

    // PROJECT RELATED

    static final int PROJECT_NOT_FOUND = 5004;

    // ROLE RELATED

    static final int ROLE_NOT_FOUND = 3001;

    // USER RELATED

    static final int USER_NOT_FOUND = 2001;
    static final int JWT_VALIDATION_ERROR = 2002;

    // SYSTEM RELATED

    static final int SYSTEM_ERROR = -1;

}