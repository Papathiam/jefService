package com.jef.sn.domain;

import static com.jef.sn.domain.MissionTestSamples.*;
import static com.jef.sn.domain.ServiceJefTestSamples.*;
import static com.jef.sn.domain.TechnicienTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jef.sn.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServiceJefTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceJef.class);
        ServiceJef serviceJef1 = getServiceJefSample1();
        ServiceJef serviceJef2 = new ServiceJef();
        assertThat(serviceJef1).isNotEqualTo(serviceJef2);

        serviceJef2.setId(serviceJef1.getId());
        assertThat(serviceJef1).isEqualTo(serviceJef2);

        serviceJef2 = getServiceJefSample2();
        assertThat(serviceJef1).isNotEqualTo(serviceJef2);
    }

    @Test
    void missionsTest() {
        ServiceJef serviceJef = getServiceJefRandomSampleGenerator();
        Mission missionBack = getMissionRandomSampleGenerator();

        serviceJef.addMissions(missionBack);
        assertThat(serviceJef.getMissions()).containsOnly(missionBack);
        assertThat(missionBack.getServiceJef()).isEqualTo(serviceJef);

        serviceJef.removeMissions(missionBack);
        assertThat(serviceJef.getMissions()).doesNotContain(missionBack);
        assertThat(missionBack.getServiceJef()).isNull();

        serviceJef.missions(new HashSet<>(Set.of(missionBack)));
        assertThat(serviceJef.getMissions()).containsOnly(missionBack);
        assertThat(missionBack.getServiceJef()).isEqualTo(serviceJef);

        serviceJef.setMissions(new HashSet<>());
        assertThat(serviceJef.getMissions()).doesNotContain(missionBack);
        assertThat(missionBack.getServiceJef()).isNull();
    }

    @Test
    void techniciensTest() {
        ServiceJef serviceJef = getServiceJefRandomSampleGenerator();
        Technicien technicienBack = getTechnicienRandomSampleGenerator();

        serviceJef.addTechniciens(technicienBack);
        assertThat(serviceJef.getTechniciens()).containsOnly(technicienBack);
        assertThat(technicienBack.getServiceJefs()).containsOnly(serviceJef);

        serviceJef.removeTechniciens(technicienBack);
        assertThat(serviceJef.getTechniciens()).doesNotContain(technicienBack);
        assertThat(technicienBack.getServiceJefs()).doesNotContain(serviceJef);

        serviceJef.techniciens(new HashSet<>(Set.of(technicienBack)));
        assertThat(serviceJef.getTechniciens()).containsOnly(technicienBack);
        assertThat(technicienBack.getServiceJefs()).containsOnly(serviceJef);

        serviceJef.setTechniciens(new HashSet<>());
        assertThat(serviceJef.getTechniciens()).doesNotContain(technicienBack);
        assertThat(technicienBack.getServiceJefs()).doesNotContain(serviceJef);
    }
}
