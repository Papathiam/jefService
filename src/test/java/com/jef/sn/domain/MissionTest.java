package com.jef.sn.domain;

import static com.jef.sn.domain.ClientTestSamples.*;
import static com.jef.sn.domain.MissionTestSamples.*;
import static com.jef.sn.domain.PaiementTestSamples.*;
import static com.jef.sn.domain.ServiceJefTestSamples.*;
import static com.jef.sn.domain.TechnicienTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jef.sn.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mission.class);
        Mission mission1 = getMissionSample1();
        Mission mission2 = new Mission();
        assertThat(mission1).isNotEqualTo(mission2);

        mission2.setId(mission1.getId());
        assertThat(mission1).isEqualTo(mission2);

        mission2 = getMissionSample2();
        assertThat(mission1).isNotEqualTo(mission2);
    }

    @Test
    void paiementTest() {
        Mission mission = getMissionRandomSampleGenerator();
        Paiement paiementBack = getPaiementRandomSampleGenerator();

        mission.setPaiement(paiementBack);
        assertThat(mission.getPaiement()).isEqualTo(paiementBack);

        mission.paiement(null);
        assertThat(mission.getPaiement()).isNull();
    }

    @Test
    void serviceJefTest() {
        Mission mission = getMissionRandomSampleGenerator();
        ServiceJef serviceJefBack = getServiceJefRandomSampleGenerator();

        mission.setServiceJef(serviceJefBack);
        assertThat(mission.getServiceJef()).isEqualTo(serviceJefBack);

        mission.serviceJef(null);
        assertThat(mission.getServiceJef()).isNull();
    }

    @Test
    void clientTest() {
        Mission mission = getMissionRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        mission.setClient(clientBack);
        assertThat(mission.getClient()).isEqualTo(clientBack);

        mission.client(null);
        assertThat(mission.getClient()).isNull();
    }

    @Test
    void technicienTest() {
        Mission mission = getMissionRandomSampleGenerator();
        Technicien technicienBack = getTechnicienRandomSampleGenerator();

        mission.setTechnicien(technicienBack);
        assertThat(mission.getTechnicien()).isEqualTo(technicienBack);

        mission.technicien(null);
        assertThat(mission.getTechnicien()).isNull();
    }
}
