package com.infra.gateforge.management.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HealthResponseDTO {
    private String serviceName;
    private String serviceDescription;
    private String healthStatus;
    private String databaseStatus;

    public HealthResponseDTO(String serviceName, String serviceDescription, String healthStatus, String sqlStatus) {
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.healthStatus = healthStatus;
        this.databaseStatus = sqlStatus;
    }
}
