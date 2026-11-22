package com.infra.gateforge.management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ApiRouteRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 200)
    private String pathPattern;

    @NotBlank
    @Size(max = 200)
    private String targetBaseUrl;

    @Size(max = 100)
    @Pattern(regexp = "/.*", message = "pathPrefix must start with /")
    private String pathPrefix;

    @NotNull
    @Min(1)
    private Integer timeout;

    @NotNull
    @Min(1)
    private Integer requestPerMin;

    private boolean idempotent;
    private boolean active = true;

    @NotEmpty
    private Set<@NotBlank @Pattern(
            regexp = "(?i)GET|HEAD|POST|PUT|PATCH|DELETE|OPTIONS|TRACE",
            message = "Unsupported HTTP method"
    ) String> allowedMethods;

    @NotNull
    private List<@NotNull @Valid HeaderRuleRequestDTO> headerRules = new ArrayList<>();
}
