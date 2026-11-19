package com.infra.gateforge.management.service;

import com.infra.gateforge.management.dto.request.LoginRequestDTO;
import com.infra.gateforge.management.dto.response.LoginResponseDTO;
import com.infra.gateforge.management.entity.ManagementUser;
import com.infra.gateforge.management.entity.OrganisationStatus;
import com.infra.gateforge.management.exception.InactiveOrganisationException;
import com.infra.gateforge.management.exception.InvalidCredentialsException;
import com.infra.gateforge.management.exception.UserNotEnabledException;
import com.infra.gateforge.management.repository.ManagementUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthenticationService {
    private final ManagementUserRepository managementUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;

    public AuthenticationService(
            ManagementUserRepository managementUserRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.expiry}") long expirationSeconds
    ){
        this.managementUserRepository=managementUserRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtEncoder=jwtEncoder;
        this.expirationSeconds=expirationSeconds;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){
        ManagementUser managementUser=managementUserRepository.findByUsername(loginRequestDTO.getUsername()).orElseThrow(()-> new InvalidCredentialsException("Invalid Username Or Password"));

        if(!managementUser.getEnabled()){
            throw new UserNotEnabledException("User Not Enabled");
        }

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), managementUser.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid Username Or Password");
        }

        Long organisationId=managementUser.getOrganisation()==null? null: managementUser.getOrganisation().getOrganisationId();

        if(organisationId!=null && managementUser.getOrganisation().getStatus()!=OrganisationStatus.ACTIVE){
            throw new InactiveOrganisationException("Organisation Is Inactive");
        }

        Instant issuedAt= Instant.now();
        String role=managementUser.getRole().getRoleName();

        JwtClaimsSet.Builder jwtClaimSetBuilder=JwtClaimsSet.builder()
                .issuer("gateforge-api-management")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(expirationSeconds))
                .subject(managementUser.getUsername())
                .claim("role", role);


        if(organisationId!=null){
            jwtClaimSetBuilder.claim("organisationId", organisationId);
        }

        JwsHeader jwsHeader=JwsHeader.with(MacAlgorithm.HS256).build();

        String token=jwtEncoder.encode(
                JwtEncoderParameters.from(jwsHeader, jwtClaimSetBuilder.build())
        ).getTokenValue();

        return new LoginResponseDTO(
                token,
                "Bearer",
                expirationSeconds,
                role,
                organisationId
        );
    }
}
