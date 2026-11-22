package com.infra.gateforge.management.repository;

import com.infra.gateforge.management.entity.ApiRoute;
import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.entity.OrganisationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiRouteRepository extends JpaRepository<ApiRoute, Long> {
    List<ApiRoute> findAllByOrganisation(Organisation organisation);

    List<ApiRoute> findAllByActiveTrueAndOrganisationStatus(OrganisationStatus status);

    List<ApiRoute> findAllByOrganisation_OrganisationIdAndActiveTrueAndOrganisationStatus(
            Long organisationId, OrganisationStatus status
    );

    Optional<ApiRoute> findByApiRouteIdAndOrganisation(Long apiRouteId, Organisation organisation);
}
