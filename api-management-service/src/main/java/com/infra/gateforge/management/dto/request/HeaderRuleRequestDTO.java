package com.infra.gateforge.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeaderRuleRequestDTO {
    @NotBlank
    @Pattern(regexp = "(?i)REQUEST|RESPONSE", message = "Direction must be REQUEST or RESPONSE")
    private String direction;

    @NotBlank
    @Pattern(regexp = "(?i)ADD|REMOVE", message = "Action must be ADD or REMOVE")
    private String action;

    @NotBlank
    @Size(max = 100)
    private String headerName;

    @Size(max = 100)
    private String headerValue;
}
