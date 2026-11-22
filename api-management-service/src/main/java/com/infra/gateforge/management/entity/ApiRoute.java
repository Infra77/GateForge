package com.infra.gateforge.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="api_routes")
@Getter
@Setter
@NoArgsConstructor
public class ApiRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_route_id")
    private Long apiRouteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organisation_id",
            nullable = false
    )
    private Organisation organisation;

    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    @Column(
            name = "path_pattern",
            nullable = false,
            length = 200
    )
    private String pathPattern;

    @Column(
            name = "target_base_url",
            nullable = false,
            length = 200
    )
    private String targetBaseUrl;

    @Column(
            name = "path_prefix",
            length = 100
    )
    private String pathPrefix;

    @Column(
            nullable = false
    )
    private Integer timeout;

    @Column(
            name = "request_per_min",
            nullable = false
    )
    private Integer requestPerMin;

    @Column(
            nullable = false
    )
    private boolean idempotent;

    @Column(
            nullable = false
    )
    private boolean active = true;

    @Column(
            name = "version_id"
    )
    private Long versionId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "route_allowed_methods",
            joinColumns = @JoinColumn(name = "api_route_id")
    )
    @Column(
            name = "method",
            nullable = false,
            length = 10
    )
    private Set<String> allowedMethods = new HashSet<>();

    @OneToMany(
            mappedBy = "apiRoute",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RouteHeaderRule> headerRules = new ArrayList<>();
}
