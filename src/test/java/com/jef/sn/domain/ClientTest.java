package com.jef.sn.domain;

import static com.jef.sn.domain.ClientTestSamples.*;
import static com.jef.sn.domain.MissionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jef.sn.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClientTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Client.class);
        Client client1 = getClientSample1();
        Client client2 = new Client();
        assertThat(client1).isNotEqualTo(client2);

        client2.setId(client1.getId());
        assertThat(client1).isEqualTo(client2);

        client2 = getClientSample2();
        assertThat(client1).isNotEqualTo(client2);
    }

    @Test
    void missionTest() {
        Client client = getClientRandomSampleGenerator();
        Mission missionBack = getMissionRandomSampleGenerator();

        client.addMission(missionBack);
        assertThat(client.getMissions()).containsOnly(missionBack);
        assertThat(missionBack.getClient()).isEqualTo(client);

        client.removeMission(missionBack);
        assertThat(client.getMissions()).doesNotContain(missionBack);
        assertThat(missionBack.getClient()).isNull();

        client.missions(new HashSet<>(Set.of(missionBack)));
        assertThat(client.getMissions()).containsOnly(missionBack);
        assertThat(missionBack.getClient()).isEqualTo(client);

        client.setMissions(new HashSet<>());
        assertThat(client.getMissions()).doesNotContain(missionBack);
        assertThat(missionBack.getClient()).isNull();
    }
}
