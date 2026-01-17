package com.jef.sn.service.impl;

import com.jef.sn.domain.Paiement;
import com.jef.sn.repository.PaiementRepository;
import com.jef.sn.service.PaiementService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.jef.sn.domain.Paiement}.
 */
@Service
@Transactional
public class PaiementServiceImpl implements PaiementService {

    private static final Logger LOG = LoggerFactory.getLogger(PaiementServiceImpl.class);

    private final PaiementRepository paiementRepository;

    public PaiementServiceImpl(PaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    @Override
    public Paiement save(Paiement paiement) {
        LOG.debug("Request to save Paiement : {}", paiement);
        return paiementRepository.save(paiement);
    }

    @Override
    public Paiement update(Paiement paiement) {
        LOG.debug("Request to update Paiement : {}", paiement);
        return paiementRepository.save(paiement);
    }

    @Override
    public Optional<Paiement> partialUpdate(Paiement paiement) {
        LOG.debug("Request to partially update Paiement : {}", paiement);

        return paiementRepository
            .findById(paiement.getId())
            .map(existingPaiement -> {
                if (paiement.getMontantTotal() != null) {
                    existingPaiement.setMontantTotal(paiement.getMontantTotal());
                }
                if (paiement.getCommission() != null) {
                    existingPaiement.setCommission(paiement.getCommission());
                }
                if (paiement.getMontantTechnicien() != null) {
                    existingPaiement.setMontantTechnicien(paiement.getMontantTechnicien());
                }
                if (paiement.getMoyenPaiement() != null) {
                    existingPaiement.setMoyenPaiement(paiement.getMoyenPaiement());
                }
                if (paiement.getStatut() != null) {
                    existingPaiement.setStatut(paiement.getStatut());
                }
                if (paiement.getReference() != null) {
                    existingPaiement.setReference(paiement.getReference());
                }

                return existingPaiement;
            })
            .map(paiementRepository::save);
    }

    /**
     *  Get all the paiements where Mission is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Paiement> findAllWhereMissionIsNull() {
        LOG.debug("Request to get all paiements where Mission is null");
        return StreamSupport.stream(paiementRepository.findAll().spliterator(), false)
            .filter(paiement -> paiement.getMission() == null)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paiement> findOne(Long id) {
        LOG.debug("Request to get Paiement : {}", id);
        return paiementRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Paiement : {}", id);
        paiementRepository.deleteById(id);
    }
}
