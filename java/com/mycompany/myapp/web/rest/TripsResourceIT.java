package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Trips;
import com.mycompany.myapp.repository.TripsRepository;
import com.mycompany.myapp.service.dto.TripsDTO;
import com.mycompany.myapp.service.mapper.TripsMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TripsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TripsResourceIT {

    private static final String DEFAULT_TRIP_DESTINATION = "AAAAAAAAAA";
    private static final String UPDATED_TRIP_DESTINATION = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_TIME_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_TIME_TO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME_TO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DISCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DISCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_REVIEWS = "AAAAAAAAAA";
    private static final String UPDATED_REVIEWS = "BBBBBBBBBB";

    private static final String DEFAULT_PAY = "AAAAAAAAAA";
    private static final String UPDATED_PAY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/trips";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TripsRepository tripsRepository;

    @Autowired
    private TripsMapper tripsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTripsMockMvc;

    private Trips trips;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trips createEntity(EntityManager em) {
        Trips trips = new Trips()
            .tripDestination(DEFAULT_TRIP_DESTINATION)
            .city(DEFAULT_CITY)
            .date(DEFAULT_DATE)
            .timeFrom(DEFAULT_TIME_FROM)
            .timeTo(DEFAULT_TIME_TO)
            .discription(DEFAULT_DISCRIPTION)
            .reviews(DEFAULT_REVIEWS)
            .pay(DEFAULT_PAY);
        return trips;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trips createUpdatedEntity(EntityManager em) {
        Trips trips = new Trips()
            .tripDestination(UPDATED_TRIP_DESTINATION)
            .city(UPDATED_CITY)
            .date(UPDATED_DATE)
            .timeFrom(UPDATED_TIME_FROM)
            .timeTo(UPDATED_TIME_TO)
            .discription(UPDATED_DISCRIPTION)
            .reviews(UPDATED_REVIEWS)
            .pay(UPDATED_PAY);
        return trips;
    }

    @BeforeEach
    public void initTest() {
        trips = createEntity(em);
    }

    @Test
    @Transactional
    void createTrips() throws Exception {
        int databaseSizeBeforeCreate = tripsRepository.findAll().size();
        // Create the Trips
        TripsDTO tripsDTO = tripsMapper.toDto(trips);
        restTripsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tripsDTO)))
            .andExpect(status().isCreated());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeCreate + 1);
        Trips testTrips = tripsList.get(tripsList.size() - 1);
        assertThat(testTrips.getTripDestination()).isEqualTo(DEFAULT_TRIP_DESTINATION);
        assertThat(testTrips.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testTrips.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testTrips.getTimeFrom()).isEqualTo(DEFAULT_TIME_FROM);
        assertThat(testTrips.getTimeTo()).isEqualTo(DEFAULT_TIME_TO);
        assertThat(testTrips.getDiscription()).isEqualTo(DEFAULT_DISCRIPTION);
        assertThat(testTrips.getReviews()).isEqualTo(DEFAULT_REVIEWS);
        assertThat(testTrips.getPay()).isEqualTo(DEFAULT_PAY);
    }

    @Test
    @Transactional
    void createTripsWithExistingId() throws Exception {
        // Create the Trips with an existing ID
        trips.setId(1L);
        TripsDTO tripsDTO = tripsMapper.toDto(trips);

        int databaseSizeBeforeCreate = tripsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTripsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tripsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTrips() throws Exception {
        // Initialize the database
        tripsRepository.saveAndFlush(trips);

        // Get all the tripsList
        restTripsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trips.getId().intValue())))
            .andExpect(jsonPath("$.[*].tripDestination").value(hasItem(DEFAULT_TRIP_DESTINATION)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].timeFrom").value(hasItem(DEFAULT_TIME_FROM.toString())))
            .andExpect(jsonPath("$.[*].timeTo").value(hasItem(DEFAULT_TIME_TO.toString())))
            .andExpect(jsonPath("$.[*].discription").value(hasItem(DEFAULT_DISCRIPTION)))
            .andExpect(jsonPath("$.[*].reviews").value(hasItem(DEFAULT_REVIEWS)))
            .andExpect(jsonPath("$.[*].pay").value(hasItem(DEFAULT_PAY)));
    }

    @Test
    @Transactional
    void getTrips() throws Exception {
        // Initialize the database
        tripsRepository.saveAndFlush(trips);

        // Get the trips
        restTripsMockMvc
            .perform(get(ENTITY_API_URL_ID, trips.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(trips.getId().intValue()))
            .andExpect(jsonPath("$.tripDestination").value(DEFAULT_TRIP_DESTINATION))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.timeFrom").value(DEFAULT_TIME_FROM.toString()))
            .andExpect(jsonPath("$.timeTo").value(DEFAULT_TIME_TO.toString()))
            .andExpect(jsonPath("$.discription").value(DEFAULT_DISCRIPTION))
            .andExpect(jsonPath("$.reviews").value(DEFAULT_REVIEWS))
            .andExpect(jsonPath("$.pay").value(DEFAULT_PAY));
    }

    @Test
    @Transactional
    void getNonExistingTrips() throws Exception {
        // Get the trips
        restTripsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTrips() throws Exception {
        // Initialize the database
        tripsRepository.saveAndFlush(trips);

        int databaseSizeBeforeUpdate = tripsRepository.findAll().size();

        // Update the trips
        Trips updatedTrips = tripsRepository.findById(trips.getId()).get();
        // Disconnect from session so that the updates on updatedTrips are not directly saved in db
        em.detach(updatedTrips);
        updatedTrips
            .tripDestination(UPDATED_TRIP_DESTINATION)
            .city(UPDATED_CITY)
            .date(UPDATED_DATE)
            .timeFrom(UPDATED_TIME_FROM)
            .timeTo(UPDATED_TIME_TO)
            .discription(UPDATED_DISCRIPTION)
            .reviews(UPDATED_REVIEWS)
            .pay(UPDATED_PAY);
        TripsDTO tripsDTO = tripsMapper.toDto(updatedTrips);

        restTripsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tripsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tripsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeUpdate);
        Trips testTrips = tripsList.get(tripsList.size() - 1);
        assertThat(testTrips.getTripDestination()).isEqualTo(UPDATED_TRIP_DESTINATION);
        assertThat(testTrips.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testTrips.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTrips.getTimeFrom()).isEqualTo(UPDATED_TIME_FROM);
        assertThat(testTrips.getTimeTo()).isEqualTo(UPDATED_TIME_TO);
        assertThat(testTrips.getDiscription()).isEqualTo(UPDATED_DISCRIPTION);
        assertThat(testTrips.getReviews()).isEqualTo(UPDATED_REVIEWS);
        assertThat(testTrips.getPay()).isEqualTo(UPDATED_PAY);
    }

    @Test
    @Transactional
    void putNonExistingTrips() throws Exception {
        int databaseSizeBeforeUpdate = tripsRepository.findAll().size();
        trips.setId(count.incrementAndGet());

        // Create the Trips
        TripsDTO tripsDTO = tripsMapper.toDto(trips);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTripsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tripsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tripsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTrips() throws Exception {
        int databaseSizeBeforeUpdate = tripsRepository.findAll().size();
        trips.setId(count.incrementAndGet());

        // Create the Trips
        TripsDTO tripsDTO = tripsMapper.toDto(trips);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tripsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTrips() throws Exception {
        int databaseSizeBeforeUpdate = tripsRepository.findAll().size();
        trips.setId(count.incrementAndGet());

        // Create the Trips
        TripsDTO tripsDTO = tripsMapper.toDto(trips);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tripsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTripsWithPatch() throws Exception {
        // Initialize the database
        tripsRepository.saveAndFlush(trips);

        int databaseSizeBeforeUpdate = tripsRepository.findAll().size();

        // Update the trips using partial update
        Trips partialUpdatedTrips = new Trips();
        partialUpdatedTrips.setId(trips.getId());

        partialUpdatedTrips.city(UPDATED_CITY).timeTo(UPDATED_TIME_TO).reviews(UPDATED_REVIEWS);

        restTripsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrips.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTrips))
            )
            .andExpect(status().isOk());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeUpdate);
        Trips testTrips = tripsList.get(tripsList.size() - 1);
        assertThat(testTrips.getTripDestination()).isEqualTo(DEFAULT_TRIP_DESTINATION);
        assertThat(testTrips.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testTrips.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testTrips.getTimeFrom()).isEqualTo(DEFAULT_TIME_FROM);
        assertThat(testTrips.getTimeTo()).isEqualTo(UPDATED_TIME_TO);
        assertThat(testTrips.getDiscription()).isEqualTo(DEFAULT_DISCRIPTION);
        assertThat(testTrips.getReviews()).isEqualTo(UPDATED_REVIEWS);
        assertThat(testTrips.getPay()).isEqualTo(DEFAULT_PAY);
    }

    @Test
    @Transactional
    void fullUpdateTripsWithPatch() throws Exception {
        // Initialize the database
        tripsRepository.saveAndFlush(trips);

        int databaseSizeBeforeUpdate = tripsRepository.findAll().size();

        // Update the trips using partial update
        Trips partialUpdatedTrips = new Trips();
        partialUpdatedTrips.setId(trips.getId());

        partialUpdatedTrips
            .tripDestination(UPDATED_TRIP_DESTINATION)
            .city(UPDATED_CITY)
            .date(UPDATED_DATE)
            .timeFrom(UPDATED_TIME_FROM)
            .timeTo(UPDATED_TIME_TO)
            .discription(UPDATED_DISCRIPTION)
            .reviews(UPDATED_REVIEWS)
            .pay(UPDATED_PAY);

        restTripsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrips.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTrips))
            )
            .andExpect(status().isOk());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeUpdate);
        Trips testTrips = tripsList.get(tripsList.size() - 1);
        assertThat(testTrips.getTripDestination()).isEqualTo(UPDATED_TRIP_DESTINATION);
        assertThat(testTrips.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testTrips.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTrips.getTimeFrom()).isEqualTo(UPDATED_TIME_FROM);
        assertThat(testTrips.getTimeTo()).isEqualTo(UPDATED_TIME_TO);
        assertThat(testTrips.getDiscription()).isEqualTo(UPDATED_DISCRIPTION);
        assertThat(testTrips.getReviews()).isEqualTo(UPDATED_REVIEWS);
        assertThat(testTrips.getPay()).isEqualTo(UPDATED_PAY);
    }

    @Test
    @Transactional
    void patchNonExistingTrips() throws Exception {
        int databaseSizeBeforeUpdate = tripsRepository.findAll().size();
        trips.setId(count.incrementAndGet());

        // Create the Trips
        TripsDTO tripsDTO = tripsMapper.toDto(trips);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTripsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tripsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tripsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTrips() throws Exception {
        int databaseSizeBeforeUpdate = tripsRepository.findAll().size();
        trips.setId(count.incrementAndGet());

        // Create the Trips
        TripsDTO tripsDTO = tripsMapper.toDto(trips);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tripsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTrips() throws Exception {
        int databaseSizeBeforeUpdate = tripsRepository.findAll().size();
        trips.setId(count.incrementAndGet());

        // Create the Trips
        TripsDTO tripsDTO = tripsMapper.toDto(trips);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tripsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Trips in the database
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTrips() throws Exception {
        // Initialize the database
        tripsRepository.saveAndFlush(trips);

        int databaseSizeBeforeDelete = tripsRepository.findAll().size();

        // Delete the trips
        restTripsMockMvc
            .perform(delete(ENTITY_API_URL_ID, trips.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Trips> tripsList = tripsRepository.findAll();
        assertThat(tripsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
