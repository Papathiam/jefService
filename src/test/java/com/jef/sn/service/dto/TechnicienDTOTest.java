package com.jef.sn.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.jef.sn.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TechnicienDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TechnicienDTO.class);
        TechnicienDTO technicienDTO1 = new TechnicienDTO();
        technicienDTO1.setId(1L);
        TechnicienDTO technicienDTO2 = new TechnicienDTO();
        assertThat(technicienDTO1).isNotEqualTo(technicienDTO2);
        technicienDTO2.setId(technicienDTO1.getId());
        assertThat(technicienDTO1).isEqualTo(technicienDTO2);
        technicienDTO2.setId(2L);
        assertThat(technicienDTO1).isNotEqualTo(technicienDTO2);
        technicienDTO1.setId(null);
        assertThat(technicienDTO1).isNotEqualTo(technicienDTO2);
    }
}
