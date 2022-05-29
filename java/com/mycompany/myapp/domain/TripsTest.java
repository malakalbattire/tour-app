package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TripsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trips.class);
        Trips trips1 = new Trips();
        trips1.setId(1L);
        Trips trips2 = new Trips();
        trips2.setId(trips1.getId());
        assertThat(trips1).isEqualTo(trips2);
        trips2.setId(2L);
        assertThat(trips1).isNotEqualTo(trips2);
        trips1.setId(null);
        assertThat(trips1).isNotEqualTo(trips2);
    }
}
