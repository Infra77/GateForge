package com.infra.gateforge.management.service;

import com.infra.gateforge.management.dto.request.HeaderRuleRequestDTO;
import com.infra.gateforge.management.dto.request.ApiRouteRequestDTO;
import com.infra.gateforge.management.dto.request.RouteStatusRequestDTO;
import com.infra.gateforge.management.dto.response.ApiRouteResponseDTO;
import com.infra.gateforge.management.dto.response.RouteVersionResponseDTO;
import com.infra.gateforge.management.entity.ApiRoute;
import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.entity.OrganisationStatus;
import com.infra.gateforge.management.entity.RouteConfigVersion;
import com.infra.gateforge.management.entity.RouteHeaderRule;
import com.infra.gateforge.management.exception.InvalidRouteException;
import com.infra.gateforge.management.exception.RouteNotFoundException;
import com.infra.gateforge.management.repository.ApiRouteRepository;
import com.infra.gateforge.management.repository.RouteConfigVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class RouteService {
    private final ApiRouteRepository apiRouteRepository;
    private final RouteConfigVersionRepository routeConfigVersionRepository;
    private final TenantContext tenantContext;
    private final ObjectMapper objectMapper;
    private final AuditService auditService;

    public RouteService(
            ApiRouteRepository apiRouteRepository,
            RouteConfigVersionRepository routeConfigVersionRepository,
            TenantContext tenantContext,
            ObjectMapper objectMapper,
            AuditService auditService
    ) {
        this.apiRouteRepository = apiRouteRepository;
        this.routeConfigVersionRepository = routeConfigVersionRepository;
        this.tenantContext = tenantContext;
        this.objectMapper = objectMapper;
        this.auditService = auditService;
    }

    @Transactional
    public ApiRouteResponseDTO create(ApiRouteRequestDTO apiRouteRequestDTO) {
        long startedAt = System.nanoTime();
        ApiRoute apiRoute = new ApiRoute();
        apiRoute.setOrganisation(tenantContext.getOrganisation());
        applyRequest(apiRoute, apiRouteRequestDTO);
        ApiRouteResponseDTO apiRouteResponseDTO = saveRouteAndVersion(apiRoute);
        auditService.record(apiRoute.getOrganisation(), "CREATE_ROUTE", "API_ROUTE",
                apiRoute.getApiRouteId(), startedAt, "Route configuration version 1 created");
        return apiRouteResponseDTO;
    }

    @Transactional(readOnly = true)
    public List<ApiRouteResponseDTO> findAll() {
        Organisation organisation = tenantContext.getOrganisation();
        return toRouteResponses(apiRouteRepository.findAllByOrganisation(organisation));
    }

    @Transactional(readOnly = true)
    public List<ApiRouteResponseDTO> findAllActiveForGateway() {
        return toRouteResponses(apiRouteRepository.findAllByActiveTrueAndOrganisationStatus(OrganisationStatus.ACTIVE));
    }

    @Transactional(readOnly = true)
    public List<ApiRouteResponseDTO> findActiveForOrganisation(Long organisationId) {
        List<ApiRoute> apiRoutes = apiRouteRepository.findAllByOrganisation_OrganisationIdAndActiveTrueAndOrganisationStatus(
                organisationId, OrganisationStatus.ACTIVE
        );
        return toRouteResponses(apiRoutes);
    }

    private List<ApiRouteResponseDTO> toRouteResponses(List<ApiRoute> apiRoutes) {
        List<ApiRouteResponseDTO> apiRouteResponseDTOs = new ArrayList<>();
        for (ApiRoute apiRoute : apiRoutes) {
            apiRouteResponseDTOs.add(ApiRouteResponseDTO.from(apiRoute));
        }
        return apiRouteResponseDTOs;
    }

    @Transactional(readOnly = true)
    public ApiRouteResponseDTO find(Long apiRouteId) {
        return ApiRouteResponseDTO.from(getOwnedRoute(apiRouteId));
    }

    @Transactional
    public ApiRouteResponseDTO update(Long apiRouteId, ApiRouteRequestDTO apiRouteRequestDTO) {
        long startedAt = System.nanoTime();
        ApiRoute apiRoute = getOwnedRoute(apiRouteId);
        applyRequest(apiRoute, apiRouteRequestDTO);
        ApiRouteResponseDTO apiRouteResponseDTO = saveRouteAndVersion(apiRoute);
        auditService.record(apiRoute.getOrganisation(), "UPDATE_ROUTE", "API_ROUTE",
                apiRouteId, startedAt, "Route configuration version " + apiRouteResponseDTO.getVersionId() + " created");
        return apiRouteResponseDTO;
    }

    @Transactional
    public ApiRouteResponseDTO updateStatus(Long apiRouteId, RouteStatusRequestDTO routeStatusRequestDTO) {
        long startedAt = System.nanoTime();
        ApiRoute apiRoute = getOwnedRoute(apiRouteId);
        apiRoute.setActive(routeStatusRequestDTO.getActive());
        ApiRouteResponseDTO apiRouteResponseDTO = saveRouteAndVersion(apiRoute);
        String action = apiRoute.isActive() ? "ACTIVATE_ROUTE" : "DEACTIVATE_ROUTE";
        auditService.record(apiRoute.getOrganisation(), action, "API_ROUTE",
                apiRouteId, startedAt, "Route configuration version " + apiRouteResponseDTO.getVersionId() + " created");
        return apiRouteResponseDTO;
    }

    @Transactional
    public void delete(Long apiRouteId) {
        long startedAt = System.nanoTime();
        ApiRoute apiRoute = getOwnedRoute(apiRouteId);
        apiRouteRepository.delete(apiRoute);
        auditService.record(apiRoute.getOrganisation(), "DELETE_ROUTE", "API_ROUTE",
                apiRouteId, startedAt, "Route deleted");
    }

    @Transactional(readOnly = true)
    public List<RouteVersionResponseDTO> findVersions(Long apiRouteId) {
        ApiRoute apiRoute = getOwnedRoute(apiRouteId);
        List<RouteVersionResponseDTO> routeVersionResponseDTOs = new ArrayList<>();
        for (RouteConfigVersion routeConfigVersion : routeConfigVersionRepository.findAllByApiRouteOrderByVersionIdDesc(apiRoute)) {
            routeVersionResponseDTOs.add(RouteVersionResponseDTO.from(routeConfigVersion));
        }
        return routeVersionResponseDTOs;
    }

    private ApiRoute getOwnedRoute(Long apiRouteId) {
        return apiRouteRepository.findByApiRouteIdAndOrganisation(apiRouteId, tenantContext.getOrganisation())
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));
    }

    private void applyRequest(ApiRoute apiRoute, ApiRouteRequestDTO apiRouteRequestDTO) {
        String tenantPrefix = "/" + apiRoute.getOrganisation().getSlug() + "/";
        if (!apiRouteRequestDTO.getPathPattern().startsWith(tenantPrefix)) {
            throw new InvalidRouteException("Path Pattern must start with " + tenantPrefix);
        }
        validateTargetBaseUrl(apiRouteRequestDTO.getTargetBaseUrl());

        apiRoute.setName(apiRouteRequestDTO.getName());
        apiRoute.setPathPattern(apiRouteRequestDTO.getPathPattern());
        apiRoute.setTargetBaseUrl(apiRouteRequestDTO.getTargetBaseUrl());
        apiRoute.setPathPrefix(apiRouteRequestDTO.getPathPrefix());
        apiRoute.setTimeout(apiRouteRequestDTO.getTimeout());
        apiRoute.setRequestPerMin(apiRouteRequestDTO.getRequestPerMin());
        apiRoute.setIdempotent(apiRouteRequestDTO.isIdempotent());
        apiRoute.setActive(apiRouteRequestDTO.isActive());

        apiRoute.getAllowedMethods().clear();
        for (String method : apiRouteRequestDTO.getAllowedMethods()) {
            apiRoute.getAllowedMethods().add(method.toUpperCase(Locale.ROOT));
        }

        apiRoute.getHeaderRules().clear();
        for (HeaderRuleRequestDTO headerRuleRequestDTO : apiRouteRequestDTO.getHeaderRules()) {
            if ("ADD".equalsIgnoreCase(headerRuleRequestDTO.getAction())
                    && (headerRuleRequestDTO.getHeaderValue() == null || headerRuleRequestDTO.getHeaderValue().isBlank())) {
                throw new InvalidRouteException("ADD header rules require a headerValue");
            }
            RouteHeaderRule headerRule = new RouteHeaderRule();
            headerRule.setApiRoute(apiRoute);
            headerRule.setDirection(headerRuleRequestDTO.getDirection().toUpperCase(Locale.ROOT));
            headerRule.setAction(headerRuleRequestDTO.getAction().toUpperCase(Locale.ROOT));
            headerRule.setHeaderName(headerRuleRequestDTO.getHeaderName());
            headerRule.setHeaderValue(headerRuleRequestDTO.getHeaderValue());
            LocalDateTime now = LocalDateTime.now();
            headerRule.setCreatedAt(now);
            headerRule.setUpdatedAt(now);
            apiRoute.getHeaderRules().add(headerRule);
        }
    }

    private void validateTargetBaseUrl(String targetBaseUrl) {
        try {
            URI uri = new URI(targetBaseUrl);
            if (uri.getHost() != null && ("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme()))) {
                return;
            }
        } catch (URISyntaxException ex) {
            throw new InvalidRouteException("Invalid targetBaseUrl");
        }
        throw new InvalidRouteException("targetBaseUrl must be an HTTP or HTTPS URL with a host");
    }

    private ApiRouteResponseDTO saveRouteAndVersion(ApiRoute apiRoute) {
        apiRoute.setVersionId(apiRoute.getVersionId() == null ? 1L : apiRoute.getVersionId() + 1);
        ApiRoute savedApiRoute = apiRouteRepository.save(apiRoute);
        ApiRouteResponseDTO apiRouteResponseDTO = ApiRouteResponseDTO.from(savedApiRoute);

        RouteConfigVersion routeConfigVersion = new RouteConfigVersion();
        routeConfigVersion.setApiRoute(savedApiRoute);
        routeConfigVersion.setVersionId(savedApiRoute.getVersionId());
        routeConfigVersion.setSnapshot(objectMapper.writeValueAsString(apiRouteResponseDTO));
        routeConfigVersionRepository.save(routeConfigVersion);
        return apiRouteResponseDTO;
    }
}
