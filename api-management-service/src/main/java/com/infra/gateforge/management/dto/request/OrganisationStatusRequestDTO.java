package com.infra.gateforge.management.dto.request;

import com.infra.gateforge.management.entity.OrganisationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrganisationStatusRequestDTO {

    @NotNull
    private OrganisationStatus organisationStatus;
}
