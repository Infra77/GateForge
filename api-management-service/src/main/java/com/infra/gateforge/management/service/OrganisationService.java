package com.infra.gateforge.management.service;

import com.infra.gateforge.management.dto.request.OrganisationRequestDTO;
import com.infra.gateforge.management.dto.request.OrganisationStatusRequestDTO;
import com.infra.gateforge.management.dto.response.OrganisationResponseDTO;
import com.infra.gateforge.management.entity.Organisation;
import com.infra.gateforge.management.entity.OrganisationStatus;
import com.infra.gateforge.management.exception.InvalidOrganisationStatusException;
import com.infra.gateforge.management.exception.OrganisationNotFoundException;
import com.infra.gateforge.management.repository.OrganisationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrganisationService {
    private final OrganisationRepository organisationRepository;

    public OrganisationService(
            OrganisationRepository organisationRepository
    ){
        this.organisationRepository=organisationRepository;
    }

    @Transactional
    public OrganisationResponseDTO create(OrganisationRequestDTO organisationRequestDTO){
        long startedAt=System.nanoTime();

        Organisation organisation=new Organisation();
        organisation.setName(organisationRequestDTO.getName());
        organisation.setSlug(organisationRequestDTO.getSlug());
        organisation.setStatus(OrganisationStatus.PENDING);
        organisation.setCreatedAt(LocalDateTime.now());
        organisation.setUpdatedAt(LocalDateTime.now());

        organisationRepository.save(organisation);

        // audit

        return OrganisationResponseDTO.from(organisation);
    }

    @Transactional(readOnly=true)
    public List<OrganisationResponseDTO> findAll(){
        List<OrganisationResponseDTO> organisationResponseDTOs=new ArrayList<>();

        for(Organisation organisation: organisationRepository.findAllByStatusNot(OrganisationStatus.DELETED)){
            organisationResponseDTOs.add(OrganisationResponseDTO.from(organisation));
        }
        return organisationResponseDTOs;
    }

    @Transactional(readOnly = true)
    public OrganisationResponseDTO find(Long id){
        Organisation organisation = organisationRepository.findByOrganisationIdAndStatusNot(id, OrganisationStatus.DELETED);
        if (organisation == null) {
            throw new OrganisationNotFoundException("Organisation not found");
        }
        return OrganisationResponseDTO.from(organisation);
    }

    @Transactional
    public OrganisationResponseDTO update(Long id, OrganisationRequestDTO organisationRequestDTO) {
        Organisation organisation = organisationRepository.findByOrganisationIdAndStatusNot(id, OrganisationStatus.DELETED);
        if (organisation == null) {
            throw new OrganisationNotFoundException("Organisation not found");
        }

        organisation.setName(organisationRequestDTO.getName());
        organisation.setSlug(organisationRequestDTO.getSlug());
        organisation.setUpdatedAt(LocalDateTime.now());
        return OrganisationResponseDTO.from(organisation);
    }

    @Transactional
    public OrganisationResponseDTO updateStatus(Long id, OrganisationStatusRequestDTO request) {
        long startedAt = System.nanoTime();

        Organisation organisation = organisationRepository.findByOrganisationIdAndStatusNot(id, OrganisationStatus.DELETED);

        if (organisation == null) {
            throw new OrganisationNotFoundException("Organisation not found");
        }

        OrganisationStatus newStatus = request.getOrganisationStatus();
        if (newStatus == OrganisationStatus.DELETED) {
            throw new InvalidOrganisationStatusException("Use DELETE to soft-delete an organisation");
        }

        organisation.setStatus(newStatus);
        organisation.setUpdatedAt(LocalDateTime.now());



        return OrganisationResponseDTO.from(organisation);
    }

    @Transactional
    public void delete(Long id) {
        Organisation organisation = organisationRepository.findByOrganisationIdAndStatusNot(id, OrganisationStatus.DELETED);

        if (organisation == null) {
            throw new OrganisationNotFoundException("Organisation not found");
        }

        LocalDateTime now = LocalDateTime.now();
        organisation.setStatus(OrganisationStatus.DELETED);
        organisation.setDeletedAt(now);
        organisation.setUpdatedAt(now);
    }
}
