package com.infra.gateforge.management.dto.response;

import com.infra.gateforge.management.entity.ManagementUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TenantAdminResponseDTO {
    private final Long id;
    private final Long organisationId;
    private final String username;
    private final boolean enabled;

    public static TenantAdminResponseDTO from(ManagementUser managementUser) {
        return new TenantAdminResponseDTO(
                managementUser.getManagementUserId(),
                managementUser.getOrganisation().getOrganisationId(),
                managementUser.getUsername(),
                managementUser.getEnabled()
        );
    }
}
