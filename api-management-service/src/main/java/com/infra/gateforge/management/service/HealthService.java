package com.infra.gateforge.management.service;

import com.infra.gateforge.management.dto.response.HealthResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthService {
    private final JdbcTemplate jdbcTemplate;
    @Value("${info.service.name}")
    private String serviceName;

    @Value("${info.service.description}")
    private String serviceDescription;

    public HealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public HealthResponseDTO getHealth(){
        HealthResponseDTO healthResponseDTO=new HealthResponseDTO();

        healthResponseDTO.setServiceName(serviceName);
        healthResponseDTO.setServiceDescription(serviceDescription);
        healthResponseDTO.setHealthStatus("UP");

        String databaseStatus="DOWN";
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            if (Integer.valueOf(1).equals(result)) {
                databaseStatus = "UP";
            }
        } catch (DataAccessException ex) {
            databaseStatus = "DOWN";
        }
        healthResponseDTO.setDatabaseStatus(databaseStatus);

        return healthResponseDTO;
    }
}
