package com.infra.gateforge.management.controller;

import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.entity.OrganisationStatus;
import com.infra.gateforge.management.repository.OrganisationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TenantRouteControllerTest {
    private static final String ROUTES = "/api/tenant/routes";
    private static final String VERSIONS = "/api/tenant/route-versions/";

    @Autowired MockMvc mvc;
    @Autowired OrganisationRepository organisations;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager entityManager;

    @Test
    void createsUpdatesVersionsAndDeletesARoute() throws Exception {
        Organisation organisation = createTenant();
        var tenant = token(organisation);
        String body = routeRequest(organisation.getSlug(), "get", "create");
        long id = createRoute(tenant, body);
        String url = ROUTES + "/" + id;
        reload();

        mvc.perform(get(ROUTES).with(tenant))
                .andExpect(jsonPath("$[0].apiRouteId").value(id));
        mvc.perform(get(url).with(tenant))
                .andExpect(jsonPath("$.allowedMethods[0]").value("GET"))
                .andExpect(jsonPath("$.headerRules[0].headerValue").value("create"));

        mvc.perform(put(url).with(tenant).contentType(MediaType.APPLICATION_JSON)
                        .content(routeRequest(organisation.getSlug(), "post", "update")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headerRules[0].routeHeaderRuleId").isNumber())
                .andExpect(jsonPath("$.versionId").value(2));
        reload();

        mvc.perform(patch(url + "/status").with(tenant).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"active":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.allowedMethods.length()").value(1))
                .andExpect(jsonPath("$.allowedMethods[0]").value("POST"))
                .andExpect(jsonPath("$.headerRules.length()").value(1))
                .andExpect(jsonPath("$.headerRules[0].headerValue").value("update"));
        reload();
        String versions = mvc.perform(get(VERSIONS + id).with(tenant))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].versionId").value(3))
                .andReturn().getResponse().getContentAsString();
        String firstSnapshot = objectMapper.readTree(versions).get(2).get("snapshot").asString();
        assertEquals("GET", objectMapper.readTree(firstSnapshot).get("allowedMethods").get(0).asString());
        reload();

        mvc.perform(delete(url).with(tenant)).andExpect(status().isNoContent());
        reload();
        mvc.perform(get(url).with(tenant)).andExpect(status().isNotFound());
        mvc.perform(get(ROUTES).with(tenant)).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void scopesAllRouteOperationsToTheAuthenticatedOrganisation() throws Exception {
        Organisation owner = createTenant();
        Organisation other = createTenant();
        long id = createRoute(token(owner), routeRequest(owner.getSlug(), "get", "original"));
        var tenant = token(other);
        String url = ROUTES + "/" + id;
        mvc.perform(get(ROUTES).with(tenant)).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get(url).with(tenant)).andExpect(status().isNotFound());
        mvc.perform(get(VERSIONS + id).with(tenant)).andExpect(status().isNotFound());
        mvc.perform(put(url).with(tenant).contentType(MediaType.APPLICATION_JSON)
                        .content(routeRequest(other.getSlug(), "put", "replacement")))
                .andExpect(status().isNotFound());
        mvc.perform(patch(url + "/status").with(tenant).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"active":false}
                                """))
                .andExpect(status().isNotFound());
        mvc.perform(delete(url).with(tenant)).andExpect(status().isNotFound());
        mvc.perform(post(ROUTES).with(tenant).contentType(MediaType.APPLICATION_JSON)
                        .content(routeRequest(owner.getSlug(), "get", "cross-tenant")))
                .andExpect(status().isBadRequest());
    }

    private long createRoute(RequestPostProcessor tenant, String body) throws Exception {
        String response = mvc.perform(post(ROUTES).with(tenant)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.versionId").value(1))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("apiRouteId").asLong();
    }

    private String routeRequest(String slug, String method, String value) {
        return """
                {"name":"Orders","pathPattern":"/%s/orders","targetBaseUrl":"https://orders.example",
                 "timeout":3000,"requestPerMin":60,"allowedMethods":["%s"],
                 "headerRules":[{"direction":"request","action":"add","headerName":"X-Stage","headerValue":"%s"}]}
                """.formatted(slug, method, value);
    }

    private Organisation createTenant() {
        String slug = UUID.randomUUID().toString();
        Organisation organisation = new Organisation();
        organisation.setName(slug);
        organisation.setSlug(slug);
        organisation.setStatus(OrganisationStatus.ACTIVE);
        organisation.setCreatedAt(LocalDateTime.now());
        organisation.setUpdatedAt(organisation.getCreatedAt());
        return organisations.save(organisation);
    }

    private RequestPostProcessor token(Organisation organisation) {
        return jwt().jwt(token -> token.claim("organisationId", organisation.getOrganisationId()))
                .authorities(new SimpleGrantedAuthority("TENANT_ADMIN"));
    }

    private void reload() {
        entityManager.flush();
        entityManager.clear();
    }
}
