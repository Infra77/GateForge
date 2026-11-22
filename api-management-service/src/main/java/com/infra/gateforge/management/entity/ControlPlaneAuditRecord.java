package com.infra.gateforge.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="control_plane_audit_records")
@Getter
@Setter
@NoArgsConstructor
public class ControlPlaneAuditRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "control_plane_audit_record_id"
    )
    private Long controlPlaneAuditRecordId;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "organisation_id"
    )
    private Organisation organisation;

    @Column(
            nullable = false,
            length = 100
    )
    private String actor;

    @Column(
            nullable = false,
            length = 100
    )
    private String action;

    @Column(
            name = "resource_type",
            nullable = false,
            length = 100
    )
    private String resourceType;

    @Column(
            name = "resource_id",
            nullable = false,
            length = 100
    )
    private String resourceId;

    @Column(
            name = "correlation_id",
            length = 100
    )
    private String correlationId;

    @Column(
            name = "duration_ms",
            nullable = false
    )
    private Long durationMs;

    @Column(
            nullable = false,
            length = 20
    )
    private String result;

    @Column(
            length = 2000
    )
    private String details;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;
}
