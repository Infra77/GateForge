package com.infra.gateforge.management.dto.response;

import com.infra.gateforge.management.entity.RouteHeaderRule;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HeaderRuleResponseDTO {
    private Long routeHeaderRuleId;
    private String direction;
    private String action;
    private String headerName;
    private String headerValue;

    public static HeaderRuleResponseDTO from(RouteHeaderRule rule) {
        return new HeaderRuleResponseDTO(
                rule.getRouteHeaderRuleId(),
                rule.getDirection(),
                rule.getAction(),
                rule.getHeaderName(),
                rule.getHeaderValue()
        );
    }
}
