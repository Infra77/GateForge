package com.infra.gateforge.management.controller;

import com.infra.gateforge.management.dto.response.ApiRouteResponseDTO;
import com.infra.gateforge.management.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gateway/routes")
public class GatewayConfigurationController {
    private final RouteService routeService;

    public GatewayConfigurationController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<List<ApiRouteResponseDTO>> findActiveRoutes() {
        return ResponseEntity.status(HttpStatus.OK).body(routeService.findAllActiveForGateway());
    }

    @GetMapping(params = "organisationId")
    public ResponseEntity<List<ApiRouteResponseDTO>> findActiveRoutesByOrganisation(
            @RequestParam Long organisationId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(routeService.findActiveForOrganisation(organisationId));
    }
}
