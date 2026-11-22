package com.infra.gateforge.management.repository;

import com.infra.gateforge.management.entity.ApiRoute;
import com.infra.gateforge.management.entity.RouteConfigVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteConfigVersionRepository extends JpaRepository<RouteConfigVersion, Long> {
    List<RouteConfigVersion> findAllByApiRouteOrderByVersionIdDesc(ApiRoute apiRoute);
}
