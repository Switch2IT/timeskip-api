package be.ehb.factories;

import be.ehb.entities.mail.MailTemplateBean;
import be.ehb.entities.organizations.MembershipBean;
import be.ehb.entities.organizations.OrganizationBean;
import be.ehb.entities.projects.ActivityBean;
import be.ehb.entities.projects.ProjectBean;
import be.ehb.entities.projects.WorklogBean;
import be.ehb.entities.security.RoleBean;
import be.ehb.entities.users.UserBean;
import be.ehb.model.responses.*;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */
public class ResponseFactory {

    public static Response buildResponse(Response.Status httpCode, String headerName, String headerValue, Object entity) {
        Response.ResponseBuilder builder = Response.status(httpCode.getStatusCode());
        if (StringUtils.isNotEmpty(headerName) && StringUtils.isNotEmpty(headerValue)) {
            builder.header(headerName, headerValue);
        }
        if (entity != null) {
            builder.entity(entity);
        }
        return builder.type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    public static Response buildResponse(Response.Status httpCode) {
        return buildResponse(httpCode, null, null, null);
    }

    public static Response buildResponse(Response.Status httpCode, Object entity) {
        return buildResponse(httpCode, null, null, entity);
    }

    public static UserResponse createUserResponse(UserBean user) {
        UserResponse rval = null;
        if (user != null) {
            rval = new UserResponse();
            rval.setId(user.getId());
            rval.setFirstName(user.getFirstName());
            rval.setEmail(user.getEmail());
            rval.setLastName(user.getLastName());
            rval.setDefaultHoursPerDay(user.getDefaultHoursPerDay());
            rval.setWorkDays(user.getWorkdays());
            rval.setMemberships(createMembershipResponses(user.getMemberships()));
            rval.setAdmin(user.getAdmin());
        }
        return rval;
    }

    public static MembershipResponse createMembershipResponse(MembershipBean membership) {
        MembershipResponse rval = null;
        if (membership != null) {
            rval = new MembershipResponse();
            rval.setOrganizationId(membership.getOrganizationId());
            rval.setRole(membership.getRoleId());
        }
        return rval;
    }

    public static RoleResponse createRoleResponse(RoleBean role) {
        RoleResponse rval = null;
        if (role != null) {
            rval = new RoleResponse();
            rval.setId(role.getId());
            rval.setName(role.getName());
            rval.setDescription(role.getDescription());
            rval.setAutoGrant(role.getAutoGrant());
        }
        return rval;
    }

    public static OrganizationResponse createOrganizationResponse(OrganizationBean organization) {
        OrganizationResponse rval = null;
        if (organization != null) {
            rval = new OrganizationResponse();
            rval.setId(organization.getId());
            rval.setName(organization.getName());
            rval.setDescription(organization.getDescription());
        }
        return rval;
    }

    public static ProjectResponse createProjectResponse(ProjectBean project) {
        ProjectResponse rval = null;
        if (project != null) {
            rval = new ProjectResponse();
            rval.setId(project.getId());
            rval.setName(project.getName());
            rval.setDescription(project.getDescription());
            rval.setAllowOvertime(project.getAllowOvertime());
            rval.setBillOvertime(project.getBillOvertime());
            rval.setOrganization(createOrganizationResponse(project.getOrganization()));
        }
        return rval;
    }

    public static ActivityResponse createActivityResponse(ActivityBean activity) {
        ActivityResponse rval = null;
        if (activity != null) {
            rval = new ActivityResponse();
            rval.setId(activity.getId());
            rval.setName(activity.getName());
            rval.setDescription(activity.getDescription());
            rval.setBillable(activity.getBillable());
            rval.setProject(createProjectResponse(activity.getProject()));
        }
        return rval;
    }

    public static WorklogResponse createWorklogResponse(WorklogBean worklog) {
        WorklogResponse rval = null;
        if (worklog != null) {
            rval = new WorklogResponse();
            rval.setId(worklog.getId());
            rval.setActivity(createActivityResponse(worklog.getActivity()));
            rval.setConfirmed(worklog.getConfirmed());
            rval.setDay(worklog.getDay());
            rval.setLoggedMinutes(worklog.getLoggedMinutes());
            rval.setUserId(worklog.getUserId());
        }
        return rval;
    }

    public static MailTemplateResponse createMailTemplateResponse(MailTemplateBean template) {
        MailTemplateResponse rval = null;
        if (template != null) {
            rval = new MailTemplateResponse();
            rval.setTopic(template.getId().toString());
            rval.setSubject(template.getSubject());
            rval.setContent(template.getContent());
        }
        return rval;
    }

    public static DayOfMonthlyReminderResponse createDayOfMonthlyReminderResponse(Integer dayOfMonthlyReminder, Boolean lastDayOfMonth) {
        DayOfMonthlyReminderResponse rval = null;
        if (dayOfMonthlyReminder != null) {
            rval = new DayOfMonthlyReminderResponse();
            rval.setDayOfMonthlyReminder(dayOfMonthlyReminder);
        }
        if (lastDayOfMonth != null) {
            if (rval == null) rval = new DayOfMonthlyReminderResponse();
            rval.setLastDayOfMonth(lastDayOfMonth);
        }
        return rval;
    }

    private static List<MembershipResponse> createMembershipResponses(List<MembershipBean> memberships) {
        List<MembershipResponse> rval = null;
        if (memberships != null) {
            rval = memberships.stream().map(ResponseFactory::createMembershipResponse).collect(Collectors.toList());
        }
        return rval;
    }
}