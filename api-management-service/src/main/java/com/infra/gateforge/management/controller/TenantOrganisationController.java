package com.infra.gateforge.management.controller;

import com.infra.gateforge.management.dto.request.OrganisationRequestDTO;
import com.infra.gateforge.management.dto.response.OrganisationResponseDTO;
import com.infra.gateforge.management.service.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/tenant/organisation")
public class TenantOrganisationController {
    private final OrganisationService organisationService;

    public TenantOrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    @GetMapping
    public OrganisationResponseDTO getOwnOrganisation(JwtAuthenticationToken authentication) {
        return organisationService.findOwnOrganisation(organisationId(authentication));
    }

    @PutMapping
    public OrganisationResponseDTO updateOwnOrganisation(JwtAuthenticationToken authentication,
                                                        @Valid @RequestBody OrganisationRequestDTO request) {
        return organisationService.updateOwnOrganisation(organisationId(authentication), request);
    }

    private long organisationId(JwtAuthenticationToken authentication) {
        String claim = authentication.getToken().getClaimAsString("organisationId");
        try {
            return Long.parseLong(claim);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing or invalid organisation ID");
        }
    }
}
