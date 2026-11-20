package com.infra.gateforge.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationRequestDTO {

    @NotBlank
    @Size(max=150)
    private String name;

    @NotBlank
    @Size(max=100)
    @Pattern(
            regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
            message = "slug must contain lowercase letters or digits, with optional single hyphens between groups"
    )
    private String slug;
}
