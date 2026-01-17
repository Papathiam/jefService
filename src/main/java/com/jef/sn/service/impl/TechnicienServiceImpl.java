package com.jef.sn.service.impl;

import com.jef.sn.domain.Technicien;
import com.jef.sn.repository.TechnicienRepository;
import com.jef.sn.service.TechnicienService;
import com.jef.sn.service.dto.TechnicienDTO;
import com.jef.sn.service.mapper.TechnicienMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.jef.sn.domain.Technicien}.
 */
@Service
@Transactional
public class TechnicienServiceImpl implements TechnicienService {

    private static final Logger LOG = LoggerFactory.getLogger(TechnicienServiceImpl.class);

    private final TechnicienRepository technicienRepository;

    private final TechnicienMapper technicienMapper;

    public TechnicienServiceImpl(TechnicienRepository technicienRepository, TechnicienMapper technicienMapper) {
        this.technicienRepository = technicienRepository;
        this.technicienMapper = technicienMapper;
    }

    @Override
    public TechnicienDTO save(TechnicienDTO technicienDTO) {
        LOG.debug("Request to save Technicien : {}", technicienDTO);
        Technicien technicien = technicienMapper.toEntity(technicienDTO);
        technicien = technicienRepository.save(technicien);
        return technicienMapper.toDto(technicien);
    }

    @Override
    public TechnicienDTO update(TechnicienDTO technicienDTO) {
        LOG.debug("Request to update Technicien : {}", technicienDTO);
        Technicien technicien = technicienMapper.toEntity(technicienDTO);
        technicien = technicienRepository.save(technicien);
        return technicienMapper.toDto(technicien);
    }

    @Override
    public Optional<TechnicienDTO> partialUpdate(TechnicienDTO technicienDTO) {
        LOG.debug("Request to partially update Technicien : {}", technicienDTO);

        return technicienRepository
            .findById(technicienDTO.getId())
            .map(existingTechnicien -> {
                technicienMapper.partialUpdate(existingTechnicien, technicienDTO);

                return existingTechnicien;
            })
            .map(technicienRepository::save)
            .map(technicienMapper::toDto);
    }

    public Page<TechnicienDTO> findAllWithEagerRelationships(Pageable pageable) {
        return technicienRepository.findAllWithEagerRelationships(pageable).map(technicienMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TechnicienDTO> findOne(Long id) {
        LOG.debug("Request to get Technicien : {}", id);
        return technicienRepository.findOneWithEagerRelationships(id).map(technicienMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Technicien : {}", id);
        technicienRepository.deleteById(id);
    }
}
