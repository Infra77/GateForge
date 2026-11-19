package com.infra.gateforge.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name="management_users"
)
public class ManagementUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name="management_user_id"
    )
    private Long managementUserId;

    @ManyToOne(
            fetch=FetchType.LAZY
    )
    @JoinColumn(
            name="organisation_id"
    )
    private Organisation organisation;

    @ManyToOne(
            fetch=FetchType.EAGER
    )
    @JoinColumn(
            name="role_id",
            nullable=false
    )
    private Role role;

    @JoinColumn(
            nullable=false
    )
    private String username;

    @JoinColumn(
            name="password_hash",
            nullable = false
    )
    private String passwordHash;

    @JoinColumn(
            nullable=false
    )
    private Boolean enabled;
}
