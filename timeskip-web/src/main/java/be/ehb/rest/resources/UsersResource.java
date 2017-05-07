package be.ehb.rest.resources;

import be.ehb.facades.IOrganizationFacade;
import be.ehb.facades.IUserFacade;
import be.ehb.factories.ResponseFactory;
import be.ehb.model.requests.JWTParseRequest;
import be.ehb.model.requests.NewUserRequest;
import be.ehb.model.requests.UpdateCurrentUserWorklogRequestList;
import be.ehb.model.responses.ErrorResponse;
import be.ehb.model.responses.TokenClaimsResponse;
import be.ehb.model.responses.UserResponse;
import be.ehb.model.responses.WorklogResponse;
import com.google.common.base.Preconditions;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */
@Api(value = "/users", description = "Users-related endpoints")
@Path("/users")
@ApplicationScoped
public class UsersResource {

    @Inject
    private IUserFacade userFacade;
    @Inject
    private IOrganizationFacade orgFacade;

    @ApiOperation(value = "Parse a JWT",
            notes = "Parse a JWT and return the user info from the claims")
    @ApiResponses({
            @ApiResponse(code = 200, response = TokenClaimsResponse.class, message = "User info"),
            @ApiResponse(code = 400, response = ErrorResponse.class, message = "Error occurred")
    })
    @POST
    @Path("/jwt/parse")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response parseJwt(@ApiParam(value = "JWT") JWTParseRequest jwt) {
        //TODO - Use resource bundle for internationalization
        Preconditions.checkNotNull(jwt, "Request must be provided");
        Preconditions.checkArgument(StringUtils.isNotEmpty(jwt.getJwt()), "JWT String required");
        return ResponseFactory.buildResponse(OK, userFacade.parseJWT(jwt));
    }

    @ApiOperation(value = "Get current user",
            notes = "Get the current user")
    @ApiResponses({
            @ApiResponse(code = 200, response = UserResponse.class, message = "User info"),
            @ApiResponse(code = 400, response = ErrorResponse.class, message = "Error occurred")
    })
    @GET
    @Path("/current")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser() {
        return ResponseFactory.buildResponse(OK, userFacade.getCurrentUser());
    }

    @ApiOperation(value = "List users",
            notes = "Retrieve a list of all users.")
    @ApiResponses({
            @ApiResponse(code = 200, responseContainer = "List", response = UserResponse.class, message = "User list"),
            @ApiResponse(code = 400, response = ErrorResponse.class, message = "Error occurred")
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsers() {
        return ResponseFactory.buildResponse(OK, userFacade.listUsers());
    }

    @ApiOperation(value = "Create a user",
            notes = "Create a user")
    @ApiResponses({
            @ApiResponse(code = 201, response = UserResponse.class, message = "Created"),
            @ApiResponse(code = 400, response = ErrorResponse.class, message = "Error occurred")
    })
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@ApiParam NewUserRequest request) {
        Preconditions.checkNotNull(request, "Request body must be provided");
        Preconditions.checkArgument(StringUtils.isNotEmpty(request.getFirstName()), "\"name\" must be provided");
        Preconditions.checkArgument(StringUtils.isNotEmpty(request.getLastName()), "\"surname\" must be provided");
        Preconditions.checkArgument(StringUtils.isNotEmpty(request.getEmail()), "\"email\" must be provided");
        Preconditions.checkNotNull(request.getMemberships(), "\"memberships\" must be provided");
        Preconditions.checkArgument(!request.getMemberships().isEmpty(), "At least one membership must be provided");
        request.getMemberships().forEach(membership -> {
            Preconditions.checkArgument(StringUtils.isNotEmpty(membership.getRole()), "\"role\" must be provdided");
            Preconditions.checkNotNull(membership.getOrganizationId(), "\"organizationId\" must be provided");
        });
        return ResponseFactory.buildResponse(CREATED, userFacade.createUser(request));
    }

    @ApiOperation(value = "Update current user worklogs",
            notes = "Update a list of the current user's worklogs. Use this endpoint if you need to confirm many worklogs at the same time")
    @ApiResponses({
            @ApiResponse(code = 200, responseContainer = "List", response = WorklogResponse.class, message = "Updated"),
            @ApiResponse(code = 400, response = ErrorResponse.class, message = "Error occurred")
    })
    @PUT
    @Path("/current/worklogs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCurrentUserWorklogs(@ApiParam UpdateCurrentUserWorklogRequestList request) {
        Preconditions.checkNotNull(request, "Request body must be provided");
        return ResponseFactory.buildResponse(OK, orgFacade.updateCurrentUserWorklogs(request));
    }
}