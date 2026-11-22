package com.infra.gateforge.management.repository;

import com.infra.gateforge.management.entity.ControlPlaneAuditRecord;
import com.infra.gateforge.management.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ControlPlaneAuditRecordRepository extends JpaRepository<ControlPlaneAuditRecord, Long> {
    List<ControlPlaneAuditRecord> findAllByOrganisationOrderByCreatedAtDescControlPlaneAuditRecordIdDesc(
            Organisation organisation
    );
}
