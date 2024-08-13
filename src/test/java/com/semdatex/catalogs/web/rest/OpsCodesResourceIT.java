package com.semdatex.catalogs.web.rest;

import static com.semdatex.catalogs.domain.OpsCodesAsserts.*;
import static com.semdatex.catalogs.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semdatex.catalogs.IntegrationTest;
import com.semdatex.catalogs.domain.OpsCodes;
import com.semdatex.catalogs.repository.OpsCodesRepository;
import com.semdatex.catalogs.service.dto.OpsCodesDTO;
import com.semdatex.catalogs.service.mapper.OpsCodesMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OpsCodesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OpsCodesResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ops-codes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OpsCodesRepository opsCodesRepository;

    @Autowired
    private OpsCodesMapper opsCodesMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOpsCodesMockMvc;

    private OpsCodes opsCodes;

    private OpsCodes insertedOpsCodes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OpsCodes createEntity(EntityManager em) {
        OpsCodes opsCodes = new OpsCodes().code(DEFAULT_CODE).description(DEFAULT_DESCRIPTION);
        return opsCodes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OpsCodes createUpdatedEntity(EntityManager em) {
        OpsCodes opsCodes = new OpsCodes().code(UPDATED_CODE).description(UPDATED_DESCRIPTION);
        return opsCodes;
    }

    @BeforeEach
    public void initTest() {
        opsCodes = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedOpsCodes != null) {
            opsCodesRepository.delete(insertedOpsCodes);
            insertedOpsCodes = null;
        }
    }

    @Test
    @Transactional
    void createOpsCodes() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OpsCodes
        OpsCodesDTO opsCodesDTO = opsCodesMapper.toDto(opsCodes);
        var returnedOpsCodesDTO = om.readValue(
            restOpsCodesMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(opsCodesDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OpsCodesDTO.class
        );

        // Validate the OpsCodes in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOpsCodes = opsCodesMapper.toEntity(returnedOpsCodesDTO);
        assertOpsCodesUpdatableFieldsEquals(returnedOpsCodes, getPersistedOpsCodes(returnedOpsCodes));

        insertedOpsCodes = returnedOpsCodes;
    }

    @Test
    @Transactional
    void createOpsCodesWithExistingId() throws Exception {
        // Create the OpsCodes with an existing ID
        opsCodes.setId(1L);
        OpsCodesDTO opsCodesDTO = opsCodesMapper.toDto(opsCodes);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOpsCodesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(opsCodesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OpsCodes in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOpsCodes() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList
        restOpsCodesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(opsCodes.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getOpsCodes() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get the opsCodes
        restOpsCodesMockMvc
            .perform(get(ENTITY_API_URL_ID, opsCodes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(opsCodes.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getOpsCodesByIdFiltering() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        Long id = opsCodes.getId();

        defaultOpsCodesFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOpsCodesFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOpsCodesFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOpsCodesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList where code equals to
        defaultOpsCodesFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllOpsCodesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList where code in
        defaultOpsCodesFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllOpsCodesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList where code is not null
        defaultOpsCodesFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllOpsCodesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList where code contains
        defaultOpsCodesFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllOpsCodesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList where code does not contain
        defaultOpsCodesFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllOpsCodesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList where description equals to
        defaultOpsCodesFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOpsCodesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList where description in
        defaultOpsCodesFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllOpsCodesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList where description is not null
        defaultOpsCodesFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllOpsCodesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList where description contains
        defaultOpsCodesFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOpsCodesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        // Get all the opsCodesList where description does not contain
        defaultOpsCodesFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    private void defaultOpsCodesFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOpsCodesShouldBeFound(shouldBeFound);
        defaultOpsCodesShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOpsCodesShouldBeFound(String filter) throws Exception {
        restOpsCodesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(opsCodes.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restOpsCodesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOpsCodesShouldNotBeFound(String filter) throws Exception {
        restOpsCodesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOpsCodesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOpsCodes() throws Exception {
        // Get the opsCodes
        restOpsCodesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOpsCodes() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the opsCodes
        OpsCodes updatedOpsCodes = opsCodesRepository.findById(opsCodes.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOpsCodes are not directly saved in db
        em.detach(updatedOpsCodes);
        updatedOpsCodes.code(UPDATED_CODE).description(UPDATED_DESCRIPTION);
        OpsCodesDTO opsCodesDTO = opsCodesMapper.toDto(updatedOpsCodes);

        restOpsCodesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, opsCodesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(opsCodesDTO))
            )
            .andExpect(status().isOk());

        // Validate the OpsCodes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOpsCodesToMatchAllProperties(updatedOpsCodes);
    }

    @Test
    @Transactional
    void putNonExistingOpsCodes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        opsCodes.setId(longCount.incrementAndGet());

        // Create the OpsCodes
        OpsCodesDTO opsCodesDTO = opsCodesMapper.toDto(opsCodes);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOpsCodesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, opsCodesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(opsCodesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OpsCodes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOpsCodes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        opsCodes.setId(longCount.incrementAndGet());

        // Create the OpsCodes
        OpsCodesDTO opsCodesDTO = opsCodesMapper.toDto(opsCodes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOpsCodesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(opsCodesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OpsCodes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOpsCodes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        opsCodes.setId(longCount.incrementAndGet());

        // Create the OpsCodes
        OpsCodesDTO opsCodesDTO = opsCodesMapper.toDto(opsCodes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOpsCodesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(opsCodesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OpsCodes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOpsCodesWithPatch() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the opsCodes using partial update
        OpsCodes partialUpdatedOpsCodes = new OpsCodes();
        partialUpdatedOpsCodes.setId(opsCodes.getId());

        partialUpdatedOpsCodes.code(UPDATED_CODE).description(UPDATED_DESCRIPTION);

        restOpsCodesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOpsCodes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOpsCodes))
            )
            .andExpect(status().isOk());

        // Validate the OpsCodes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOpsCodesUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOpsCodes, opsCodes), getPersistedOpsCodes(opsCodes));
    }

    @Test
    @Transactional
    void fullUpdateOpsCodesWithPatch() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the opsCodes using partial update
        OpsCodes partialUpdatedOpsCodes = new OpsCodes();
        partialUpdatedOpsCodes.setId(opsCodes.getId());

        partialUpdatedOpsCodes.code(UPDATED_CODE).description(UPDATED_DESCRIPTION);

        restOpsCodesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOpsCodes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOpsCodes))
            )
            .andExpect(status().isOk());

        // Validate the OpsCodes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOpsCodesUpdatableFieldsEquals(partialUpdatedOpsCodes, getPersistedOpsCodes(partialUpdatedOpsCodes));
    }

    @Test
    @Transactional
    void patchNonExistingOpsCodes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        opsCodes.setId(longCount.incrementAndGet());

        // Create the OpsCodes
        OpsCodesDTO opsCodesDTO = opsCodesMapper.toDto(opsCodes);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOpsCodesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, opsCodesDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(opsCodesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OpsCodes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOpsCodes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        opsCodes.setId(longCount.incrementAndGet());

        // Create the OpsCodes
        OpsCodesDTO opsCodesDTO = opsCodesMapper.toDto(opsCodes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOpsCodesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(opsCodesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OpsCodes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOpsCodes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        opsCodes.setId(longCount.incrementAndGet());

        // Create the OpsCodes
        OpsCodesDTO opsCodesDTO = opsCodesMapper.toDto(opsCodes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOpsCodesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(opsCodesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OpsCodes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOpsCodes() throws Exception {
        // Initialize the database
        insertedOpsCodes = opsCodesRepository.saveAndFlush(opsCodes);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the opsCodes
        restOpsCodesMockMvc
            .perform(delete(ENTITY_API_URL_ID, opsCodes.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return opsCodesRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected OpsCodes getPersistedOpsCodes(OpsCodes opsCodes) {
        return opsCodesRepository.findById(opsCodes.getId()).orElseThrow();
    }

    protected void assertPersistedOpsCodesToMatchAllProperties(OpsCodes expectedOpsCodes) {
        assertOpsCodesAllPropertiesEquals(expectedOpsCodes, getPersistedOpsCodes(expectedOpsCodes));
    }

    protected void assertPersistedOpsCodesToMatchUpdatableProperties(OpsCodes expectedOpsCodes) {
        assertOpsCodesAllUpdatablePropertiesEquals(expectedOpsCodes, getPersistedOpsCodes(expectedOpsCodes));
    }
}
