package com.infra.gateforge.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteStatusRequestDTO {
    @NotNull
    private Boolean active;
}
