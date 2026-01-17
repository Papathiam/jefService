package com.jef.sn.domain;

import static com.jef.sn.domain.DocumentTestSamples.*;
import static com.jef.sn.domain.MissionTestSamples.*;
import static com.jef.sn.domain.ServiceJefTestSamples.*;
import static com.jef.sn.domain.TechnicienTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jef.sn.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TechnicienTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Technicien.class);
        Technicien technicien1 = getTechnicienSample1();
        Technicien technicien2 = new Technicien();
        assertThat(technicien1).isNotEqualTo(technicien2);

        technicien2.setId(technicien1.getId());
        assertThat(technicien1).isEqualTo(technicien2);

        technicien2 = getTechnicienSample2();
        assertThat(technicien1).isNotEqualTo(technicien2);
    }
}
