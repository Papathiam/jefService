package com.jef.sn.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.jef.sn.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ServiceJefDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceJefDTO.class);
        ServiceJefDTO serviceJefDTO1 = new ServiceJefDTO();
        serviceJefDTO1.setId(1L);
        ServiceJefDTO serviceJefDTO2 = new ServiceJefDTO();
        assertThat(serviceJefDTO1).isNotEqualTo(serviceJefDTO2);
        serviceJefDTO2.setId(serviceJefDTO1.getId());
        assertThat(serviceJefDTO1).isEqualTo(serviceJefDTO2);
        serviceJefDTO2.setId(2L);
        assertThat(serviceJefDTO1).isNotEqualTo(serviceJefDTO2);
        serviceJefDTO1.setId(null);
        assertThat(serviceJefDTO1).isNotEqualTo(serviceJefDTO2);
    }
}
