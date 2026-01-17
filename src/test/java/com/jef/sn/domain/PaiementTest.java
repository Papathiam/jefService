package com.jef.sn.domain;

import static com.jef.sn.domain.MissionTestSamples.*;
import static com.jef.sn.domain.PaiementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jef.sn.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaiementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Paiement.class);
        Paiement paiement1 = getPaiementSample1();
        Paiement paiement2 = new Paiement();
        assertThat(paiement1).isNotEqualTo(paiement2);

        paiement2.setId(paiement1.getId());
        assertThat(paiement1).isEqualTo(paiement2);

        paiement2 = getPaiementSample2();
        assertThat(paiement1).isNotEqualTo(paiement2);
    }

    @Test
    void missionTest() {
        Paiement paiement = getPaiementRandomSampleGenerator();
        Mission missionBack = getMissionRandomSampleGenerator();

        paiement.setMission(missionBack);
        assertThat(paiement.getMission()).isEqualTo(missionBack);
        assertThat(missionBack.getPaiement()).isEqualTo(paiement);

        paiement.mission(null);
        assertThat(paiement.getMission()).isNull();
        assertThat(missionBack.getPaiement()).isNull();
    }
}
