package com.jef.sn.service.impl;

import com.jef.sn.domain.Mission;
import com.jef.sn.repository.MissionRepository;
import com.jef.sn.service.MissionService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.jef.sn.domain.Mission}.
 */
@Service
@Transactional
public class MissionServiceImpl implements MissionService {

    private static final Logger LOG = LoggerFactory.getLogger(MissionServiceImpl.class);

    private final MissionRepository missionRepository;

    public MissionServiceImpl(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    @Override
    public Mission save(Mission mission) {
        LOG.debug("Request to save Mission : {}", mission);
        return missionRepository.save(mission);
    }

    @Override
    public Mission update(Mission mission) {
        LOG.debug("Request to update Mission : {}", mission);
        return missionRepository.save(mission);
    }

    @Override
    public Optional<Mission> partialUpdate(Mission mission) {
        LOG.debug("Request to partially update Mission : {}", mission);

        return missionRepository
            .findById(mission.getId())
            .map(existingMission -> {
                if (mission.getDescription() != null) {
                    existingMission.setDescription(mission.getDescription());
                }
                if (mission.getLatitude() != null) {
                    existingMission.setLatitude(mission.getLatitude());
                }
                if (mission.getLongitude() != null) {
                    existingMission.setLongitude(mission.getLongitude());
                }
                if (mission.getStatut() != null) {
                    existingMission.setStatut(mission.getStatut());
                }
                if (mission.getPrixPropose() != null) {
                    existingMission.setPrixPropose(mission.getPrixPropose());
                }
                if (mission.getDateCreation() != null) {
                    existingMission.setDateCreation(mission.getDateCreation());
                }

                return existingMission;
            })
            .map(missionRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Mission> findOne(Long id) {
        LOG.debug("Request to get Mission : {}", id);
        return missionRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Mission : {}", id);
        missionRepository.deleteById(id);
    }
}
