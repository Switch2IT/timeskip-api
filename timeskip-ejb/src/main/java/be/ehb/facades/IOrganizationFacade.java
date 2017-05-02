package be.ehb.facades;

import be.ehb.model.organizations.OrganizationDTO;

import java.util.List;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */
public interface IOrganizationFacade {

    /**
     * Retrieve a list of all organizations
     * @return
     */
    List<OrganizationDTO> listOrganizations();

    /**
     * Get an organization by id
     * @param organizationId
     * @return
     */
    OrganizationDTO getOrganization(String organizationId);

    /**
     * Get or create an organization
     * @param organization
     * @return
     */
    OrganizationDTO createOrganization(OrganizationDTO organization);

    /**
     * Update an organization
     * @param organization
     * @return
     */
    OrganizationDTO updateOrganization(OrganizationDTO organization);

    /**
     * Delete an organization
     * @param organizationId
     */
    void deleteOrganization(String organizationId);

}