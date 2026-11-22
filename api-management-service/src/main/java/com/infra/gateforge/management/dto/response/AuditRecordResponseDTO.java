package com.infra.gateforge.management.dto.response;

import com.infra.gateforge.management.entity.ControlPlaneAuditRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AuditRecordResponseDTO {
    private Long controlPlaneAuditRecordId;
    private String actor;
    private String action;
    private String resourceType;
    private String resourceId;
    private String correlationId;
    private Long durationMs;
    private String result;
    private String details;
    private LocalDateTime createdAt;

    public static AuditRecordResponseDTO from(ControlPlaneAuditRecord auditRecord) {
        return new AuditRecordResponseDTO(
                auditRecord.getControlPlaneAuditRecordId(),
                auditRecord.getActor(),
                auditRecord.getAction(),
                auditRecord.getResourceType(),
                auditRecord.getResourceId(),
                auditRecord.getCorrelationId(),
                auditRecord.getDurationMs(),
                auditRecord.getResult(),
                auditRecord.getDetails(),
                auditRecord.getCreatedAt()
        );
    }
}
