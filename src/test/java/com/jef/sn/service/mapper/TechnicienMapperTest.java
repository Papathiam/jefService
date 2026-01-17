package com.jef.sn.service.mapper;

import static com.jef.sn.domain.TechnicienAsserts.*;
import static com.jef.sn.domain.TechnicienTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TechnicienMapperTest {

    private TechnicienMapper technicienMapper;

    @BeforeEach
    void setUp() {
        technicienMapper = new TechnicienMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTechnicienSample1();
        var actual = technicienMapper.toEntity(technicienMapper.toDto(expected));
        assertTechnicienAllPropertiesEquals(expected, actual);
    }
}
