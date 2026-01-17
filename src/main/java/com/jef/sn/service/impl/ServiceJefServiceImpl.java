package com.jef.sn.service.impl;

import com.jef.sn.domain.ServiceJef;
import com.jef.sn.repository.ServiceJefRepository;
import com.jef.sn.service.ServiceJefService;
import com.jef.sn.service.dto.ServiceJefDTO;
import com.jef.sn.service.mapper.ServiceJefMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.jef.sn.domain.ServiceJef}.
 */
@Service
@Transactional
public class ServiceJefServiceImpl implements ServiceJefService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceJefServiceImpl.class);

    private final ServiceJefRepository serviceJefRepository;

    private final ServiceJefMapper serviceJefMapper;

    public ServiceJefServiceImpl(ServiceJefRepository serviceJefRepository, ServiceJefMapper serviceJefMapper) {
        this.serviceJefRepository = serviceJefRepository;
        this.serviceJefMapper = serviceJefMapper;
    }

    @Override
    public ServiceJefDTO save(ServiceJefDTO serviceJefDTO) {
        LOG.debug("Request to save ServiceJef : {}", serviceJefDTO);
        ServiceJef serviceJef = serviceJefMapper.toEntity(serviceJefDTO);
        serviceJef = serviceJefRepository.save(serviceJef);
        return serviceJefMapper.toDto(serviceJef);
    }

    @Override
    public ServiceJefDTO update(ServiceJefDTO serviceJefDTO) {
        LOG.debug("Request to update ServiceJef : {}", serviceJefDTO);
        ServiceJef serviceJef = serviceJefMapper.toEntity(serviceJefDTO);
        serviceJef = serviceJefRepository.save(serviceJef);
        return serviceJefMapper.toDto(serviceJef);
    }

    @Override
    public Optional<ServiceJefDTO> partialUpdate(ServiceJefDTO serviceJefDTO) {
        LOG.debug("Request to partially update ServiceJef : {}", serviceJefDTO);

        return serviceJefRepository
            .findById(serviceJefDTO.getId())
            .map(existingServiceJef -> {
                serviceJefMapper.partialUpdate(existingServiceJef, serviceJefDTO);

                return existingServiceJef;
            })
            .map(serviceJefRepository::save)
            .map(serviceJefMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceJefDTO> findOne(Long id) {
        LOG.debug("Request to get ServiceJef : {}", id);
        return serviceJefRepository.findById(id).map(serviceJefMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ServiceJef : {}", id);
        serviceJefRepository.deleteById(id);
    }
}
