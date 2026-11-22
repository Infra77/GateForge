package com.infra.gateforge.management.dto.response;

import com.infra.gateforge.management.entity.ApiRoute;
import com.infra.gateforge.management.entity.RouteHeaderRule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class ApiRouteResponseDTO {
    private Long apiRouteId;
    private Long organisationId;
    private String name;
    private String pathPattern;
    private String targetBaseUrl;
    private String pathPrefix;
    private Integer timeout;
    private Integer requestPerMin;
    private boolean idempotent;
    private boolean active;
    private Long versionId;
    private Set<String> allowedMethods;
    private List<HeaderRuleResponseDTO> headerRules;

    public static ApiRouteResponseDTO from(ApiRoute apiRoute) {
        List<HeaderRuleResponseDTO> rules = new ArrayList<>();
        for (RouteHeaderRule rule : apiRoute.getHeaderRules()) {
            rules.add(HeaderRuleResponseDTO.from(rule));
        }
        return new ApiRouteResponseDTO(
                apiRoute.getApiRouteId(),
                apiRoute.getOrganisation().getOrganisationId(),
                apiRoute.getName(),
                apiRoute.getPathPattern(),
                apiRoute.getTargetBaseUrl(),
                apiRoute.getPathPrefix(),
                apiRoute.getTimeout(),
                apiRoute.getRequestPerMin(),
                apiRoute.isIdempotent(),
                apiRoute.isActive(),
                apiRoute.getVersionId(),
                Set.copyOf(apiRoute.getAllowedMethods()),
                rules
        );
    }
}
