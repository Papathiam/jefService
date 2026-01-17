package com.jef.sn.service.mapper;

import static com.jef.sn.domain.ServiceJefAsserts.*;
import static com.jef.sn.domain.ServiceJefTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceJefMapperTest {

    private ServiceJefMapper serviceJefMapper;

    @BeforeEach
    void setUp() {
        serviceJefMapper = new ServiceJefMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getServiceJefSample1();
        var actual = serviceJefMapper.toEntity(serviceJefMapper.toDto(expected));
        assertServiceJefAllPropertiesEquals(expected, actual);
    }
}
