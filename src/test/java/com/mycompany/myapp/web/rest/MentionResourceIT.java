package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MentionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Mention;
import com.mycompany.myapp.repository.MentionRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MentionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MentionResourceIT {

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mentions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MentionRepository mentionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMentionMockMvc;

    private Mention mention;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mention createEntity(EntityManager em) {
        Mention mention = new Mention().userName(DEFAULT_USER_NAME).body(DEFAULT_BODY);
        return mention;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mention createUpdatedEntity(EntityManager em) {
        Mention mention = new Mention().userName(UPDATED_USER_NAME).body(UPDATED_BODY);
        return mention;
    }

    @BeforeEach
    public void initTest() {
        mention = createEntity(em);
    }

    @Test
    @Transactional
    void createMention() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Mention
        var returnedMention = om.readValue(
            restMentionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mention)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Mention.class
        );

        // Validate the Mention in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMentionUpdatableFieldsEquals(returnedMention, getPersistedMention(returnedMention));
    }

    @Test
    @Transactional
    void createMentionWithExistingId() throws Exception {
        // Create the Mention with an existing ID
        mention.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMentionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mention)))
            .andExpect(status().isBadRequest());

        // Validate the Mention in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mention.setUserName(null);

        // Create the Mention, which fails.

        restMentionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mention)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBodyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mention.setBody(null);

        // Create the Mention, which fails.

        restMentionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mention)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMentions() throws Exception {
        // Initialize the database
        mentionRepository.saveAndFlush(mention);

        // Get all the mentionList
        restMentionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mention.getId().intValue())))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)));
    }

    @Test
    @Transactional
    void getMention() throws Exception {
        // Initialize the database
        mentionRepository.saveAndFlush(mention);

        // Get the mention
        restMentionMockMvc
            .perform(get(ENTITY_API_URL_ID, mention.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mention.getId().intValue()))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY));
    }

    @Test
    @Transactional
    void getNonExistingMention() throws Exception {
        // Get the mention
        restMentionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMention() throws Exception {
        // Initialize the database
        mentionRepository.saveAndFlush(mention);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mention
        Mention updatedMention = mentionRepository.findById(mention.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMention are not directly saved in db
        em.detach(updatedMention);
        updatedMention.userName(UPDATED_USER_NAME).body(UPDATED_BODY);

        restMentionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMention.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMention))
            )
            .andExpect(status().isOk());

        // Validate the Mention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMentionToMatchAllProperties(updatedMention);
    }

    @Test
    @Transactional
    void putNonExistingMention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mention.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMentionMockMvc
            .perform(put(ENTITY_API_URL_ID, mention.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mention)))
            .andExpect(status().isBadRequest());

        // Validate the Mention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mention.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMentionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mention))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mention.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMentionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mention)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMentionWithPatch() throws Exception {
        // Initialize the database
        mentionRepository.saveAndFlush(mention);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mention using partial update
        Mention partialUpdatedMention = new Mention();
        partialUpdatedMention.setId(mention.getId());

        restMentionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMention.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMention))
            )
            .andExpect(status().isOk());

        // Validate the Mention in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMentionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMention, mention), getPersistedMention(mention));
    }

    @Test
    @Transactional
    void fullUpdateMentionWithPatch() throws Exception {
        // Initialize the database
        mentionRepository.saveAndFlush(mention);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mention using partial update
        Mention partialUpdatedMention = new Mention();
        partialUpdatedMention.setId(mention.getId());

        partialUpdatedMention.userName(UPDATED_USER_NAME).body(UPDATED_BODY);

        restMentionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMention.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMention))
            )
            .andExpect(status().isOk());

        // Validate the Mention in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMentionUpdatableFieldsEquals(partialUpdatedMention, getPersistedMention(partialUpdatedMention));
    }

    @Test
    @Transactional
    void patchNonExistingMention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mention.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMentionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mention.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mention))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mention.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMentionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mention))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mention.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMentionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mention)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMention() throws Exception {
        // Initialize the database
        mentionRepository.saveAndFlush(mention);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the mention
        restMentionMockMvc
            .perform(delete(ENTITY_API_URL_ID, mention.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return mentionRepository.count();
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

    protected Mention getPersistedMention(Mention mention) {
        return mentionRepository.findById(mention.getId()).orElseThrow();
    }

    protected void assertPersistedMentionToMatchAllProperties(Mention expectedMention) {
        assertMentionAllPropertiesEquals(expectedMention, getPersistedMention(expectedMention));
    }

    protected void assertPersistedMentionToMatchUpdatableProperties(Mention expectedMention) {
        assertMentionAllUpdatablePropertiesEquals(expectedMention, getPersistedMention(expectedMention));
    }
}
