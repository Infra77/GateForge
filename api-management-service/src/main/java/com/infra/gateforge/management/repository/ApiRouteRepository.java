package com.infra.gateforge.management.repository;

import com.infra.gateforge.management.entity.ApiRoute;
import com.infra.gateforge.management.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiRouteRepository extends JpaRepository<ApiRoute, Long> {
    List<ApiRoute> findAllByOrganisation(Organisation organisation);

    Optional<ApiRoute> findByApiRouteIdAndOrganisation(Long apiRouteId, Organisation organisation);
}
