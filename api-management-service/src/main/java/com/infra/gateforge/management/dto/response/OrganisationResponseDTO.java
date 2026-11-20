package com.infra.gateforge.management.dto.response;

import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.entity.OrganisationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrganisationResponseDTO {

    private Long id;
    private String name;
    private String slug;
    private OrganisationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrganisationResponseDTO from(Organisation organisation) {
        return new OrganisationResponseDTO(
                organisation.getOrganisationId(),
                organisation.getName(),
                organisation.getSlug(),
                organisation.getStatus(),
                organisation.getCreatedAt(),
                organisation.getUpdatedAt()
        );
    }
}
