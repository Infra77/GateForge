package com.infra.gateforge.management.controller;

import com.infra.gateforge.management.dto.response.HealthResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HeathController {
    private String serviceName;
    private String serviceDescription;

    public HeathController(
            @Value("${info.service.name}") String serviceName,
            @Value("${info.service.description}") String serviceDescription
    ){
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
    }

    @GetMapping
    ResponseEntity<HealthResponseDTO> health(){
        return ResponseEntity.status(HttpStatus.OK).body(new HealthResponseDTO(serviceName, serviceDescription, "Active"));
    }
}
