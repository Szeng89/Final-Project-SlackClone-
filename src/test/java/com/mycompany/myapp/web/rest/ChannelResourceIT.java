package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ChannelAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Channel;
import com.mycompany.myapp.repository.ChannelRepository;
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
 * Integration tests for the {@link ChannelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChannelResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/channels";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChannelMockMvc;

    private Channel channel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Channel createEntity(EntityManager em) {
        Channel channel = new Channel().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return channel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Channel createUpdatedEntity(EntityManager em) {
        Channel channel = new Channel().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return channel;
    }

    @BeforeEach
    public void initTest() {
        channel = createEntity(em);
    }

    @Test
    @Transactional
    void createChannel() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Channel
        var returnedChannel = om.readValue(
            restChannelMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(channel)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Channel.class
        );

        // Validate the Channel in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertChannelUpdatableFieldsEquals(returnedChannel, getPersistedChannel(returnedChannel));
    }

    @Test
    @Transactional
    void createChannelWithExistingId() throws Exception {
        // Create the Channel with an existing ID
        channel.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChannelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(channel)))
            .andExpect(status().isBadRequest());

        // Validate the Channel in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        channel.setName(null);

        // Create the Channel, which fails.

        restChannelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(channel)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChannels() throws Exception {
        // Initialize the database
        channelRepository.saveAndFlush(channel);

        // Get all the channelList
        restChannelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(channel.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getChannel() throws Exception {
        // Initialize the database
        channelRepository.saveAndFlush(channel);

        // Get the channel
        restChannelMockMvc
            .perform(get(ENTITY_API_URL_ID, channel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(channel.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingChannel() throws Exception {
        // Get the channel
        restChannelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChannel() throws Exception {
        // Initialize the database
        channelRepository.saveAndFlush(channel);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the channel
        Channel updatedChannel = channelRepository.findById(channel.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChannel are not directly saved in db
        em.detach(updatedChannel);
        updatedChannel.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restChannelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedChannel.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedChannel))
            )
            .andExpect(status().isOk());

        // Validate the Channel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChannelToMatchAllProperties(updatedChannel);
    }

    @Test
    @Transactional
    void putNonExistingChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        channel.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChannelMockMvc
            .perform(put(ENTITY_API_URL_ID, channel.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(channel)))
            .andExpect(status().isBadRequest());

        // Validate the Channel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        channel.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChannelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(channel))
            )
            .andExpect(status().isBadRequest());

        // Validate the Channel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        channel.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChannelMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(channel)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Channel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChannelWithPatch() throws Exception {
        // Initialize the database
        channelRepository.saveAndFlush(channel);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the channel using partial update
        Channel partialUpdatedChannel = new Channel();
        partialUpdatedChannel.setId(channel.getId());

        restChannelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChannel.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChannel))
            )
            .andExpect(status().isOk());

        // Validate the Channel in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChannelUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedChannel, channel), getPersistedChannel(channel));
    }

    @Test
    @Transactional
    void fullUpdateChannelWithPatch() throws Exception {
        // Initialize the database
        channelRepository.saveAndFlush(channel);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the channel using partial update
        Channel partialUpdatedChannel = new Channel();
        partialUpdatedChannel.setId(channel.getId());

        partialUpdatedChannel.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restChannelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChannel.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChannel))
            )
            .andExpect(status().isOk());

        // Validate the Channel in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChannelUpdatableFieldsEquals(partialUpdatedChannel, getPersistedChannel(partialUpdatedChannel));
    }

    @Test
    @Transactional
    void patchNonExistingChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        channel.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChannelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, channel.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(channel))
            )
            .andExpect(status().isBadRequest());

        // Validate the Channel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        channel.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChannelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(channel))
            )
            .andExpect(status().isBadRequest());

        // Validate the Channel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        channel.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChannelMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(channel)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Channel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChannel() throws Exception {
        // Initialize the database
        channelRepository.saveAndFlush(channel);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the channel
        restChannelMockMvc
            .perform(delete(ENTITY_API_URL_ID, channel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return channelRepository.count();
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

    protected Channel getPersistedChannel(Channel channel) {
        return channelRepository.findById(channel.getId()).orElseThrow();
    }

    protected void assertPersistedChannelToMatchAllProperties(Channel expectedChannel) {
        assertChannelAllPropertiesEquals(expectedChannel, getPersistedChannel(expectedChannel));
    }

    protected void assertPersistedChannelToMatchUpdatableProperties(Channel expectedChannel) {
        assertChannelAllUpdatablePropertiesEquals(expectedChannel, getPersistedChannel(expectedChannel));
    }
}
