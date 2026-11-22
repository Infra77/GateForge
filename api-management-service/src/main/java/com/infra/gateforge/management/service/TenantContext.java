package com.infra.gateforge.management.service;

import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.entity.OrganisationStatus;
import com.infra.gateforge.management.exception.TenantAccessDeniedException;
import com.infra.gateforge.management.repository.OrganisationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TenantContext {
    private final OrganisationRepository organisationRepository;

    public TenantContext(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    public Organisation getOrganisation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            throw new TenantAccessDeniedException("Tenant identity is missing");
        }

        Long organisationId = jwtAuthenticationToken.getToken().getClaim("organisationId");
        if (organisationId == null) {
            throw new TenantAccessDeniedException("Tenant identity is missing");
        }

        Organisation organisation = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new TenantAccessDeniedException("Organisation does not exist"));
        if (organisation.getStatus() != OrganisationStatus.ACTIVE) {
            throw new TenantAccessDeniedException("Organisation is not active");
        }
        return organisation;
    }
}
