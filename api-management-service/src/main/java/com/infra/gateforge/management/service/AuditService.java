package com.infra.gateforge.management.service;

import com.infra.gateforge.management.dto.response.AuditRecordResponseDTO;
import com.infra.gateforge.management.entity.ControlPlaneAuditRecord;
import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.repository.ControlPlaneAuditRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final ControlPlaneAuditRecordRepository auditRecordRepository;
    private final TenantContext tenantContext;

    public void record(
            Organisation organisation,
            String action,
            String resourceType,
            Long resourceId,
            long startedAt,
            String details
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ControlPlaneAuditRecord auditRecord = new ControlPlaneAuditRecord();
        auditRecord.setOrganisation(organisation);
        auditRecord.setActor(authentication == null ? "system" : authentication.getName());
        auditRecord.setAction(action);
        auditRecord.setResourceType(resourceType);
        auditRecord.setResourceId(resourceId.toString());
        auditRecord.setCorrelationId(getCorrelationId());
        auditRecord.setDurationMs(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt));
        auditRecord.setResult("SUCCESS");
        auditRecord.setDetails(details);
        auditRecord.setCreatedAt(LocalDateTime.now());
        auditRecordRepository.save(auditRecord);
    }

    @Transactional(readOnly = true)
    public List<AuditRecordResponseDTO> findForTenant() {
        Organisation organisation = tenantContext.getOrganisation();
        List<AuditRecordResponseDTO> auditRecordResponseDTOs = new ArrayList<>();
        for (ControlPlaneAuditRecord auditRecord :
                auditRecordRepository.findAllByOrganisationOrderByCreatedAtDescControlPlaneAuditRecordIdDesc(organisation)) {
            auditRecordResponseDTOs.add(AuditRecordResponseDTO.from(auditRecord));
        }
        return auditRecordResponseDTOs;
    }

    private String getCorrelationId() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            String correlationId = attributes.getRequest().getHeader("X-Correlation-ID");
            if (correlationId != null && correlationId.length() <= 100) {
                return correlationId;
            }
        }
        return null;
    }
}
