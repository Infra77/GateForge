package com.infra.gateforge.analytics.dto.response;

import lombok.Data;

@Data
public class HealthResponseDTO {
    private String serviceName;
    private String serviceDescription;
    private String healthStatus;

    public HealthResponseDTO(String serviceName, String serviceDescription, String healthStatus) {
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.healthStatus = healthStatus;
    }
}
