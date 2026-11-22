package com.infra.gateforge.management.controller;

import com.infra.gateforge.management.dto.request.OrganisationRequestDTO;
import com.infra.gateforge.management.dto.response.OrganisationResponseDTO;
import com.infra.gateforge.management.service.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant/organisation")
public class TenantOrganisationController {
    private final OrganisationService organisationService;

    public TenantOrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    @GetMapping
    public OrganisationResponseDTO getOwnOrganisation() {
        return organisationService.findOwnOrganisation();
    }

    @PutMapping
    public OrganisationResponseDTO updateOwnOrganisation(
            @Valid @RequestBody OrganisationRequestDTO request
    ) {
        return organisationService.updateOwnOrganisation(request);
    }
}
