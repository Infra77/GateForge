package com.infra.gateforge.management.controller;

import com.infra.gateforge.management.dto.request.TenantAdminRequestDTO;
import com.infra.gateforge.management.dto.response.TenantAdminResponseDTO;
import com.infra.gateforge.management.service.TenantAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform/tenants")
public class PlatformTenantController {
    private final TenantAdminService tenantAdminService;

    public PlatformTenantController(TenantAdminService tenantAdminService) {
        this.tenantAdminService = tenantAdminService;
    }

    // Each organisation has one tenant admin, identified here by its organisation ID.
    @PostMapping("/{organisationId}")
    public ResponseEntity<TenantAdminResponseDTO> createTenantAdmin(
            @PathVariable Long organisationId,
            @Valid @RequestBody TenantAdminRequestDTO tenantAdminRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tenantAdminService.create(organisationId, tenantAdminRequestDTO));
    }

    @GetMapping("/{organisationId}")
    public ResponseEntity<TenantAdminResponseDTO> getTenantAdmin(@PathVariable Long organisationId) {
        return ResponseEntity.status(HttpStatus.OK).body(tenantAdminService.find(organisationId));
    }

    @PutMapping("/{organisationId}")
    public ResponseEntity<TenantAdminResponseDTO> updateTenantAdmin(
            @PathVariable Long organisationId,
            @Valid @RequestBody TenantAdminRequestDTO tenantAdminRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tenantAdminService.update(organisationId, tenantAdminRequestDTO));
    }

    @DeleteMapping("/{organisationId}")
    public ResponseEntity<Void> deleteTenantAdmin(@PathVariable Long organisationId) {
        tenantAdminService.delete(organisationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
