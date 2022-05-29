package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HostMapperTest {

    private HostMapper hostMapper;

    @BeforeEach
    public void setUp() {
        hostMapper = new HostMapperImpl();
    }
}
