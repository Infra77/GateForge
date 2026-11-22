package com.infra.gateforge.management.dto.response;

import com.infra.gateforge.management.entity.RouteConfigVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RouteVersionResponseDTO {
    private Long apiRouteId;
    private Long versionId;
    private String snapshot;

    public static RouteVersionResponseDTO from(RouteConfigVersion version) {
        return new RouteVersionResponseDTO(
                version.getApiRoute().getApiRouteId(),
                version.getVersionId(),
                version.getSnapshot()
        );
    }
}
