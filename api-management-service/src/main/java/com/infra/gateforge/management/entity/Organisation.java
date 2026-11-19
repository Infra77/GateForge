package com.infra.gateforge.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="organisations")
@Getter
@Setter
@NoArgsConstructor
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name="organisation_id"
    )
    Long organisationId;

    String name;

    @Column(
            unique=true,
            nullable = false,
            name="slug"
    )
    String slug;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false
    )
    private OrganisationStatus status;

    @Column(
            nullable = false,
            name="created_at"
    )
    private LocalDateTime createdAt;

    @Column(
            name="updated_at"
    )
    private LocalDateTime updatedAt;

    @Column(
            name="deleted_at"
    )
    private LocalDateTime deletedAt;
}
