package com.infra.gateforge.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name="route_config_version")
@Getter
@Setter
@NoArgsConstructor
public class RouteConfigVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "route_config_version_id"
    )
    private Long routeConfigVersionId;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "api_route_id",
            nullable = false
    )
    private ApiRoute apiRoute;

    @Column(
            name = "version_id",
            nullable = false
    )
    private Long versionId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            nullable = false
    )
    private String snapshot;
}
