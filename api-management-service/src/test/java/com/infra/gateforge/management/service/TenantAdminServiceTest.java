package com.infra.gateforge.management.service;

import com.infra.gateforge.management.dto.request.TenantAdminRequestDTO;
import com.infra.gateforge.management.entity.ManagementUser;
import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.entity.OrganisationStatus;
import com.infra.gateforge.management.entity.Role;
import com.infra.gateforge.management.repository.ManagementUserRepository;
import com.infra.gateforge.management.repository.OrganisationRepository;
import com.infra.gateforge.management.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantAdminServiceTest {
    @Test
    void platformAdminCanCreateADisabledAccountAndEnableItOnUpdate() {
        var managementUserRepository = mock(ManagementUserRepository.class);
        var organisationRepository = mock(OrganisationRepository.class);
        var roleRepository = mock(RoleRepository.class);
        var passwordEncoder = new BCryptPasswordEncoder(4);
        var tenantAdminService = new TenantAdminService(managementUserRepository, organisationRepository, roleRepository, passwordEncoder);

        Organisation organisation = new Organisation();
        organisation.setOrganisationId(42L);
        when(roleRepository.findByRoleName("TENANT_ADMIN")).thenReturn(new Role());
        when(organisationRepository.existsByOrganisationIdAndStatusNot(42L, OrganisationStatus.DELETED)).thenReturn(true);
        when(organisationRepository.getReferenceById(42L)).thenReturn(organisation);
        when(managementUserRepository.save(any(ManagementUser.class))).thenAnswer(call -> {
            ManagementUser managementUser = call.getArgument(0);
            managementUser.setManagementUserId(1L);
            return managementUser;
        });

        var tenantAdminRequestDTO = new TenantAdminRequestDTO();
        tenantAdminRequestDTO.setUsername("tenant-admin");
        tenantAdminRequestDTO.setPassword("first-password");
        assertTrue(tenantAdminRequestDTO.isEnabled());
        tenantAdminRequestDTO.setEnabled(false);
        assertFalse(tenantAdminService.create(42L, tenantAdminRequestDTO).isEnabled());

        var managementUserCaptor = org.mockito.ArgumentCaptor.forClass(ManagementUser.class);
        verify(managementUserRepository).save(managementUserCaptor.capture());
        ManagementUser managementUser = managementUserCaptor.getValue();
        assertTrue(passwordEncoder.matches("first-password", managementUser.getPasswordHash()));
        when(managementUserRepository.findByOrganisationAndRole_RoleName(organisation, "TENANT_ADMIN")).thenReturn(managementUser);

        tenantAdminRequestDTO.setEnabled(true);
        tenantAdminRequestDTO.setPassword("second-password");
        assertTrue(tenantAdminService.update(42L, tenantAdminRequestDTO).isEnabled());
        verify(managementUserRepository, times(1)).findByUsername("tenant-admin");
        assertTrue(passwordEncoder.matches("second-password", managementUser.getPasswordHash()));
    }
}
