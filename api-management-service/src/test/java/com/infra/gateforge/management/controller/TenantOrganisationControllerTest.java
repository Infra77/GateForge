package com.infra.gateforge.management.controller;

import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.entity.OrganisationStatus;
import com.infra.gateforge.management.repository.OrganisationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TenantOrganisationControllerTest {
    private static final String URL = "/api/tenant/organisation";

    @Autowired MockMvc mvc;
    @Autowired OrganisationRepository repository;

    @Test
    void readsAndUpdatesTheOrganisationFromTheToken() throws Exception {
        Organisation own = createOrganisation(OrganisationStatus.ACTIVE);
        Organisation other = createOrganisation(OrganisationStatus.ACTIVE);
        String otherId = other.getOrganisationId().toString();
        String otherSlug = other.getSlug();

        mvc.perform(get(URL).with(tenant(own)).param("organisationId", otherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(own.getOrganisationId()));

        mvc.perform(put(URL).with(tenant(own)).param("organisationId", otherId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated tenant name","slug":"updated-tenant-slug"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(own.getOrganisationId()));

        mvc.perform(get(URL).with(tenant(own)))
                .andExpect(jsonPath("$.name").value("Updated tenant name"))
                .andExpect(jsonPath("$.slug").value("updated-tenant-slug"));
        mvc.perform(get(URL).with(tenant(other)))
                .andExpect(jsonPath("$.slug").value(otherSlug));
    }

    @Test
    void requiresATenantAdminWithAnActiveOrganisation() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isUnauthorized());
        mvc.perform(get(URL).with(jwt().authorities(new SimpleGrantedAuthority("PLATFORM_ADMIN"))))
                .andExpect(status().isForbidden());
        mvc.perform(get(URL).with(jwt().authorities(new SimpleGrantedAuthority("TENANT_ADMIN"))))
                .andExpect(status().isForbidden());

        Organisation suspended = createOrganisation(OrganisationStatus.SUSPENDED);
        mvc.perform(get(URL).with(tenant(suspended))).andExpect(status().isForbidden());
        mvc.perform(put(URL).with(tenant(suspended)).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Suspended tenant","slug":"suspended-tenant"}
                                """))
                .andExpect(status().isForbidden());
    }

    private RequestPostProcessor tenant(Organisation organisation) {
        return jwt().jwt(token -> token.claim("organisationId", organisation.getOrganisationId()))
                .authorities(new SimpleGrantedAuthority("TENANT_ADMIN"));
    }

    private Organisation createOrganisation(OrganisationStatus status) {
        String slug = UUID.randomUUID().toString();
        Organisation organisation = new Organisation();
        organisation.setName(slug);
        organisation.setSlug(slug);
        organisation.setStatus(status);
        organisation.setCreatedAt(LocalDateTime.now());
        organisation.setUpdatedAt(LocalDateTime.now());
        return repository.save(organisation);
    }
}
