package com.infra.gateforge.management.service;

import com.infra.gateforge.management.dto.request.TenantAdminRequestDTO;
import com.infra.gateforge.management.dto.response.TenantAdminResponseDTO;
import com.infra.gateforge.management.entity.ManagementUser;
import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.entity.OrganisationStatus;
import com.infra.gateforge.management.entity.Role;
import com.infra.gateforge.management.repository.ManagementUserRepository;
import com.infra.gateforge.management.repository.OrganisationRepository;
import com.infra.gateforge.management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TenantAdminService {
    private static final String TENANT_ADMIN = "TENANT_ADMIN";

    private final ManagementUserRepository managementUserRepository;
    private final OrganisationRepository organisationRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TenantAdminResponseDTO create(Long organisationId, TenantAdminRequestDTO tenantAdminRequestDTO) {
        if (!organisationRepository.existsByOrganisationIdAndStatusNot(organisationId, OrganisationStatus.DELETED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation not found");
        }
        Organisation organisation = organisationRepository.getReferenceById(organisationId);
        if (managementUserRepository.findByOrganisationAndRole_RoleName(organisation, TENANT_ADMIN) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Organisation already has a tenant admin");
        }

        Role role = roleRepository.findByRoleName(TENANT_ADMIN);
        if (role == null) {
            throw new IllegalStateException("TENANT_ADMIN role is missing");
        }

        String username = tenantAdminRequestDTO.getUsername();
        if (managementUserRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
        }

        ManagementUser managementUser = new ManagementUser();
        managementUser.setOrganisation(organisation);
        managementUser.setRole(role);
        managementUser.setUsername(username);
        managementUser.setPasswordHash(passwordEncoder.encode(tenantAdminRequestDTO.getPassword()));
        managementUser.setEnabled(tenantAdminRequestDTO.isEnabled());
        managementUser = managementUserRepository.save(managementUser);

        return TenantAdminResponseDTO.from(managementUser);
    }

    @Transactional(readOnly = true)
    public TenantAdminResponseDTO find(Long organisationId) {
        if (!organisationRepository.existsByOrganisationIdAndStatusNot(organisationId, OrganisationStatus.DELETED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation not found");
        }
        Organisation organisation = organisationRepository.getReferenceById(organisationId);
        ManagementUser managementUser = managementUserRepository.findByOrganisationAndRole_RoleName(organisation, TENANT_ADMIN);
        if (managementUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant admin not found");
        }

        return TenantAdminResponseDTO.from(managementUser);
    }

    @Transactional
    public TenantAdminResponseDTO update(Long organisationId, TenantAdminRequestDTO tenantAdminRequestDTO) {
        if (!organisationRepository.existsByOrganisationIdAndStatusNot(organisationId, OrganisationStatus.DELETED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation not found");
        }
        Organisation organisation = organisationRepository.getReferenceById(organisationId);
        ManagementUser managementUser = managementUserRepository.findByOrganisationAndRole_RoleName(organisation, TENANT_ADMIN);
        if (managementUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant admin not found");
        }

        // An admin can keep their own username when updating the account.
        String username = tenantAdminRequestDTO.getUsername();
        if (!username.equals(managementUser.getUsername()) && managementUserRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
        }

        managementUser.setUsername(username);
        managementUser.setPasswordHash(passwordEncoder.encode(tenantAdminRequestDTO.getPassword()));
        managementUser.setEnabled(tenantAdminRequestDTO.isEnabled());

        return TenantAdminResponseDTO.from(managementUser);
    }

    @Transactional
    public void delete(Long organisationId) {
        if (!organisationRepository.existsByOrganisationIdAndStatusNot(organisationId, OrganisationStatus.DELETED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation not found");
        }
        Organisation organisation = organisationRepository.getReferenceById(organisationId);
        ManagementUser managementUser = managementUserRepository.findByOrganisationAndRole_RoleName(organisation, TENANT_ADMIN);
        if (managementUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant admin not found");
        }

        managementUserRepository.delete(managementUser);
    }
}
