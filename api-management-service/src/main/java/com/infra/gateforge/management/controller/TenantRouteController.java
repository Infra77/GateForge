package com.infra.gateforge.management.controller;

import com.infra.gateforge.management.dto.request.ApiRouteRequestDTO;
import com.infra.gateforge.management.dto.request.RouteStatusRequestDTO;
import com.infra.gateforge.management.dto.response.ApiRouteResponseDTO;
import com.infra.gateforge.management.dto.response.RouteVersionResponseDTO;
import com.infra.gateforge.management.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
public class TenantRouteController {
    private final RouteService routeService;

    public TenantRouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/routes")
    public ResponseEntity<ApiRouteResponseDTO> createRoute(
            @Valid @RequestBody ApiRouteRequestDTO apiRouteRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.create(apiRouteRequestDTO));
    }

    @GetMapping("/routes")
    public ResponseEntity<List<ApiRouteResponseDTO>> findAllRoutes() {
        return ResponseEntity.status(HttpStatus.OK).body(routeService.findAll());
    }

    @GetMapping("/routes/{apiRouteId}")
    public ResponseEntity<ApiRouteResponseDTO> findRoute(@PathVariable Long apiRouteId) {
        return ResponseEntity.status(HttpStatus.OK).body(routeService.find(apiRouteId));
    }

    @PutMapping("/routes/{apiRouteId}")
    public ResponseEntity<ApiRouteResponseDTO> updateRoute(
            @PathVariable Long apiRouteId,
            @Valid @RequestBody ApiRouteRequestDTO apiRouteRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(routeService.update(apiRouteId, apiRouteRequestDTO));
    }

    @PatchMapping("/routes/{apiRouteId}/status")
    public ResponseEntity<ApiRouteResponseDTO> updateRouteStatus(
            @PathVariable Long apiRouteId,
            @Valid @RequestBody RouteStatusRequestDTO routeStatusRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(routeService.updateStatus(apiRouteId, routeStatusRequestDTO));
    }

    @DeleteMapping("/routes/{apiRouteId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long apiRouteId) {
        routeService.delete(apiRouteId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/route-versions/{apiRouteId}")
    public ResponseEntity<List<RouteVersionResponseDTO>> findRouteVersions(@PathVariable Long apiRouteId) {
        return ResponseEntity.status(HttpStatus.OK).body(routeService.findVersions(apiRouteId));
    }
}
