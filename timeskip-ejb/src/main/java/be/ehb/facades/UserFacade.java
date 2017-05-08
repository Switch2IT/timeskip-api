package be.ehb.facades;

import be.ehb.entities.users.PaygradeBean;
import be.ehb.entities.users.UserBean;
import be.ehb.factories.ExceptionFactory;
import be.ehb.factories.ResponseFactory;
import be.ehb.model.requests.JWTParseRequest;
import be.ehb.model.requests.NewUserRequest;
import be.ehb.model.requests.UpdateCurrentUserRequest;
import be.ehb.model.requests.UpdateUserRequest;
import be.ehb.model.responses.TokenClaimsResponse;
import be.ehb.model.responses.UserResponse;
import be.ehb.security.ISecurityContext;
import be.ehb.security.JWTConstants;
import be.ehb.security.JWTValidation;
import be.ehb.security.idp.IIdpClient;
import be.ehb.storage.IStorageService;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@Default
public class UserFacade implements IUserFacade, Serializable {

    private static final Logger log = LoggerFactory.getLogger(UserFacade.class);

    @Inject
    private JWTValidation jwtValidation;
    @Inject
    private IStorageService storage;
    @Inject
    private ISecurityContext securityContext;
    @Inject
    private IManagementFacade managementFacade;
    @Inject
    private IIdpClient idpClient;

    @Override
    public List<UserResponse> listUsers() {
        return storage.listUsers().stream().map(ResponseFactory::createUserResponse).collect(Collectors.toList());
    }

    @Override
    public UserBean get(String userId) {
        return storage.getUser(userId);
    }

    @Override
    public UserResponse getUser(String userId) {
        return ResponseFactory.createUserResponse(storage.getUser(userId));
    }

    @Override
    public TokenClaimsResponse parseJWT(JWTParseRequest jwt) {
        TokenClaimsResponse rVal = null;
        if (StringUtils.isNotEmpty(jwt.getJwt())) {
            try {
                JwtClaims claims = jwtValidation
                        .getUnvalidatedContext(jwt.getJwt()).getJwtClaims();
                TokenClaimsResponse user = new TokenClaimsResponse();
                user.setUserInfo(claims.getClaimsMap());
                rVal = user;
            } catch (InvalidJwtException | MalformedClaimException e) {
                log.error("Failed to parse JWT: {}", jwt);
                e.printStackTrace();

            }
        }
        return rVal;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void initNewUser(JwtClaims claims) {
        log.info("Init new user with attributes:{}", claims);
        try {
            //create user
            UserBean newUser = new UserBean();
            newUser.setId(claims.getSubject());
            if (claims.hasClaim(JWTConstants.GIVEN_NAME) && claims.hasClaim(JWTConstants.SURNAME)) {
                newUser.setFirstName(claims.getStringClaimValue(JWTConstants.GIVEN_NAME));
                newUser.setLastName(claims.getStringClaimValue(JWTConstants.SURNAME));
            } else {
                throw ExceptionFactory.jwtValidationException("Missing claims");
            }
            if (claims.hasClaim(JWTConstants.EMAIL)) {
                newUser.setEmail(claims.getStringClaimValue(JWTConstants.EMAIL));
            }
            newUser.setAdmin(false);

            storage.createUser(newUser);
        } catch (MalformedClaimException e) {
            log.error("Invalid claims in JWT: {}", e.getMessage());
            throw ExceptionFactory.unauthorizedException();
        }
    }

    @Override
    public UserResponse getCurrentUser() {
        return ResponseFactory.createUserResponse(get(securityContext.getCurrentUser()));
    }

    @Override
    public UserResponse createUser(NewUserRequest request) {
        UserBean newUser = new UserBean();
        newUser.setAdmin(false);
        newUser.setEmail(request.getEmail());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setDefaultHoursPerDay(request.getDefaultHoursPerDay());
        newUser.setWorkdays(request.getWorkDays());

        //Create the user on the IDP and get the ID
        newUser = idpClient.createUser(newUser);
        storage.createUser(newUser);
        String userId = newUser.getId();

        //Create the memberships
        request.getMemberships().forEach(memReq -> managementFacade.updateOrCreateMembership(userId, memReq.getOrganizationId(), memReq.getRole()));
        return ResponseFactory.createUserResponse(newUser);
    }

    @Override
    public UserResponse updateCurrentUser(UpdateCurrentUserRequest request) {
        String currentUser = securityContext.getCurrentUser();
        boolean changed = false;
        if (StringUtils.isEmpty(currentUser)) {
            throw ExceptionFactory.noUserContextException();
        }
        UserBean cUser = storage.getUser(currentUser);
        changed = updateEmailIfChanged(cUser, request.getEmail());
        if (StringUtils.isNotEmpty(request.getFirstName()) && !request.getFirstName().equals(cUser.getFirstName())) {
            cUser.setFirstName(request.getFirstName());
            changed = true;
        }
        if (StringUtils.isNotEmpty(request.getLastName()) && !request.getLastName().equals(cUser.getLastName())) {
            cUser.setFirstName(request.getFirstName());
            changed = true;
        }
        if (changed) {
            return ResponseFactory.createUserResponse(storage.updateUser(cUser));
        } else return null;
    }

    @Override
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        UserBean user = storage.getUser(userId);
        boolean changed = false;
        changed = updateEmailIfChanged(user, request.getEmail());
        if (StringUtils.isNotEmpty(request.getFirstName()) && !request.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(request.getFirstName());
            changed = true;
        }
        if (StringUtils.isNotEmpty(request.getLastName()) && !request.getLastName().equals(user.getLastName())) {
            user.setFirstName(request.getFirstName());
            changed = true;
        }
        if (request.getPaygradeId() != null && !request.getPaygradeId().equals(user.getPaygrade().getId())) {
            PaygradeBean paygrade = storage.getPaygrade(request.getPaygradeId());
            user.setPaygrade(paygrade);
            changed = true;
        }
        if (request.getDefaultHoursPerDay() != null && !request.getDefaultHoursPerDay().equals(user.getDefaultHoursPerDay())) {
            user.setDefaultHoursPerDay(request.getDefaultHoursPerDay());
            changed = true;
        }
        if (request.getWorkDays() != null && request.getWorkDays().isEmpty() && !new HashSet<>(request.getWorkDays()).equals(new HashSet<>(user.getWorkdays()))) {
            user.setWorkdays(request.getWorkDays());
            changed = true;
        }
        if (request.getDefaultActivity() != null && !request.getDefaultActivity().equals(user.getDefaultActivity())) {
            storage.getActivity(request.getDefaultActivity());
            user.setDefaultActivity(request.getDefaultActivity());
            changed = true;
        }
        if (changed) {
            return ResponseFactory.createUserResponse(storage.updateUser(user));
        } else return null;
    }

    private boolean updateEmailIfChanged(UserBean user, String requestEmail) {
        if (StringUtils.isNotEmpty(requestEmail) && !requestEmail.equals(user.getEmail())) {
            if (storage.findUserByEmail(requestEmail) != null)
                throw ExceptionFactory.userAlreadyExists(requestEmail);
            user.setEmail(requestEmail);
            return true;
        }
        return false;
    }
}