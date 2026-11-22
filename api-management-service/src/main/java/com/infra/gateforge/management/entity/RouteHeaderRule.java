package com.infra.gateforge.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="route_header_rules")
@Getter
@Setter
@NoArgsConstructor
public class RouteHeaderRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "route_header_rule_id"
    )
    private Long routeHeaderRuleId;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "api_route_id", nullable = false
    )
    private ApiRoute apiRoute;

    @Column(
            nullable = false,
            length = 20
    )
    private String direction;

    @Column(
            nullable = false,
            length = 20
    )
    private String action;

    @Column(
            name = "header_name",
            nullable = false,
            length = 100
    )
    private String headerName;

    @Column(
            name = "header_value",
            length = 100
    )
    private String headerValue;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;
}
