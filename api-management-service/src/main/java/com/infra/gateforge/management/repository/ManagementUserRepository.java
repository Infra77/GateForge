package com.infra.gateforge.management.repository;

import com.infra.gateforge.management.entity.ManagementUser;
import com.infra.gateforge.management.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagementUserRepository extends JpaRepository<ManagementUser, Long> {
    Optional<ManagementUser> findByUsername(String username);

    ManagementUser findByOrganisationAndRole_RoleName(Organisation organisation, String roleName);
}
