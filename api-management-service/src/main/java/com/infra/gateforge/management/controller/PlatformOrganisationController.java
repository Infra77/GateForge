package com.infra.gateforge.management.controller;

import com.infra.gateforge.management.dto.request.OrganisationRequestDTO;
import com.infra.gateforge.management.dto.request.OrganisationStatusRequestDTO;
import com.infra.gateforge.management.dto.response.OrganisationResponseDTO;
import com.infra.gateforge.management.service.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform/organisations")
public class PlatformOrganisationController {
    private final OrganisationService organisationService;

    public PlatformOrganisationController(OrganisationService organisationService){
        this.organisationService=organisationService;
    }

    // CRUD on Organisations

    // Create Organisation
    @PostMapping
    public ResponseEntity<OrganisationResponseDTO> createOrganisation(
            @Valid @RequestBody OrganisationRequestDTO organisationRequestDTO
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(organisationService.create(organisationRequestDTO));
    }

    // Read All
    @GetMapping
    public ResponseEntity<List<OrganisationResponseDTO>> findAllOrganisations(){
        return ResponseEntity.status(HttpStatus.OK).body(organisationService.findAll());
    }

    // Read One
    @GetMapping("/{id}")
    public ResponseEntity<OrganisationResponseDTO> findOrganisation(
            @PathVariable Long id
    ){
        return ResponseEntity.status(HttpStatus.OK).body(organisationService.find(id));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<OrganisationResponseDTO> updateOrganisation(
            @PathVariable Long id,
            @Valid @RequestBody OrganisationRequestDTO organisationRequestDTO
    ){
        return ResponseEntity.status(HttpStatus.OK).body(organisationService.update(id, organisationRequestDTO));
    }

    // Patch
    @PatchMapping("/{id}")
    public ResponseEntity<OrganisationResponseDTO> updateOrganisationStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrganisationStatusRequestDTO organisationStatusRequestDTO
    ){
        return ResponseEntity.status(HttpStatus.OK).body(organisationService.updateStatus(id, organisationStatusRequestDTO));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganisation(
            @PathVariable Long id
    ){
        organisationService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
