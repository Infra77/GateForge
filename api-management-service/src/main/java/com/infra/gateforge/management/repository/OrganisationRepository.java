package com.infra.gateforge.management.repository;

import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.entity.OrganisationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    Organisation[] findAllByStatusNot(OrganisationStatus organisationStatus);

    Organisation findByOrganisationIdAndStatusNot(Long id, OrganisationStatus organisationStatus);

    boolean existsByOrganisationIdAndStatusNot(Long id, OrganisationStatus status);
}
