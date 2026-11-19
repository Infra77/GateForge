package com.infra.gateforge.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String tokenType;
    private long expiresInSeconds;
    private String role;
    private Long organisationId;
}
