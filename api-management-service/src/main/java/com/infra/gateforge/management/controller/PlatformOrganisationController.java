package com.infra.gateforge.management.controller;

import com.infra.gateforge.management.dto.request.OrganisationRequestDTO;
import com.infra.gateforge.management.dto.request.OrganisationStatusRequestDTO;
import com.infra.gateforge.management.dto.request.TenantAdminRequestDTO;
import com.infra.gateforge.management.dto.response.OrganisationResponseDTO;
import com.infra.gateforge.management.dto.response.TenantAdminResponseDTO;
import com.infra.gateforge.management.service.OrganisationService;
import com.infra.gateforge.management.service.TenantAdminService;
import jakarta.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform")
public class PlatformOrganisationController {
    private final OrganisationService organisationService;
    private final TenantAdminService tenantAdminService;

    public PlatformOrganisationController(OrganisationService organisationService, TenantAdminService tenantAdminService){
        this.organisationService=organisationService;
        this.tenantAdminService = tenantAdminService;
    }

    // CRUD on Organisations

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException exception) {
        String constraintName = exception.getConstraintName();
        if (constraintName != null) {
            // MySQL can prefix the key name with the table name.
            String key = constraintName.substring(constraintName.lastIndexOf('.') + 1);
            if (key.equals("uk_organisations_name")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Organisation name already exists");
            }
            if (key.equals("uk_organisations_slug")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Organisation slug already exists");
            }
        }
        throw exception;
    }

    // Create Organisation
    @PostMapping("/organisations")
    public ResponseEntity<OrganisationResponseDTO> createOrganisation(
            @Valid @RequestBody OrganisationRequestDTO organisationRequestDTO
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(organisationService.create(organisationRequestDTO));
    }

    // Read All
    @GetMapping("/organisations")
    public ResponseEntity<List<OrganisationResponseDTO>> findAllOrganisations(){
        return ResponseEntity.status(HttpStatus.OK).body(organisationService.findAll());
    }

    // Read One
    @GetMapping("/organisations/{id}")
    public ResponseEntity<OrganisationResponseDTO> findOrganisation(
            @PathVariable Long id
    ){
        return ResponseEntity.status(HttpStatus.OK).body(organisationService.find(id));
    }

    // Update
    @PutMapping("/organisations/{id}")
    public ResponseEntity<OrganisationResponseDTO> updateOrganisation(
            @PathVariable Long id,
            @Valid @RequestBody OrganisationRequestDTO organisationRequestDTO
    ){
        return ResponseEntity.status(HttpStatus.OK).body(organisationService.update(id, organisationRequestDTO));
    }

    // Patch
    @PatchMapping("/organisations/{id}")
    public ResponseEntity<OrganisationResponseDTO> updateOrganisationStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrganisationStatusRequestDTO organisationStatusRequestDTO
    ){
        return ResponseEntity.status(HttpStatus.OK).body(organisationService.updateStatus(id, organisationStatusRequestDTO));
    }

    // Delete
    @DeleteMapping("/organisations/{id}")
    public ResponseEntity<Void> deleteOrganisation(
            @PathVariable Long id
    ){
        organisationService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    // Each organisation has one tenant admin, identified here by its organisation ID.
    @PostMapping("/{organisationId}/tenant-admin")
    public ResponseEntity<TenantAdminResponseDTO> createTenantAdmin(
            @PathVariable Long organisationId,
            @Valid @RequestBody TenantAdminRequestDTO tenantAdminRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tenantAdminService.create(organisationId, tenantAdminRequestDTO));
    }

    @GetMapping("/{organisationId}/tenant-admin")
    public ResponseEntity<TenantAdminResponseDTO> getTenantAdmin(@PathVariable Long organisationId) {
        return ResponseEntity.status(HttpStatus.OK).body(tenantAdminService.find(organisationId));
    }

    @PutMapping("/{organisationId}/tenant-admin")
    public ResponseEntity<TenantAdminResponseDTO> updateTenantAdmin(
            @PathVariable Long organisationId,
            @Valid @RequestBody TenantAdminRequestDTO tenantAdminRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tenantAdminService.update(organisationId, tenantAdminRequestDTO));
    }

    @DeleteMapping("/{organisationId}/tenant-admin")
    public ResponseEntity<Void> deleteTenantAdmin(@PathVariable Long organisationId) {
        tenantAdminService.delete(organisationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
