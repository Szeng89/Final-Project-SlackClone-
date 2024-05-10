package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.WorkspaceAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Workspace;
import com.mycompany.myapp.repository.WorkspaceRepository;
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
 * Integration tests for the {@link WorkspaceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WorkspaceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/workspaces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkspaceMockMvc;

    private Workspace workspace;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Workspace createEntity(EntityManager em) {
        Workspace workspace = new Workspace().name(DEFAULT_NAME);
        return workspace;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Workspace createUpdatedEntity(EntityManager em) {
        Workspace workspace = new Workspace().name(UPDATED_NAME);
        return workspace;
    }

    @BeforeEach
    public void initTest() {
        workspace = createEntity(em);
    }

    @Test
    @Transactional
    void createWorkspace() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Workspace
        var returnedWorkspace = om.readValue(
            restWorkspaceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workspace)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Workspace.class
        );

        // Validate the Workspace in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertWorkspaceUpdatableFieldsEquals(returnedWorkspace, getPersistedWorkspace(returnedWorkspace));
    }

    @Test
    @Transactional
    void createWorkspaceWithExistingId() throws Exception {
        // Create the Workspace with an existing ID
        workspace.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkspaceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workspace)))
            .andExpect(status().isBadRequest());

        // Validate the Workspace in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workspace.setName(null);

        // Create the Workspace, which fails.

        restWorkspaceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workspace)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWorkspaces() throws Exception {
        // Initialize the database
        workspaceRepository.saveAndFlush(workspace);

        // Get all the workspaceList
        restWorkspaceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workspace.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getWorkspace() throws Exception {
        // Initialize the database
        workspaceRepository.saveAndFlush(workspace);

        // Get the workspace
        restWorkspaceMockMvc
            .perform(get(ENTITY_API_URL_ID, workspace.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workspace.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingWorkspace() throws Exception {
        // Get the workspace
        restWorkspaceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWorkspace() throws Exception {
        // Initialize the database
        workspaceRepository.saveAndFlush(workspace);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workspace
        Workspace updatedWorkspace = workspaceRepository.findById(workspace.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWorkspace are not directly saved in db
        em.detach(updatedWorkspace);
        updatedWorkspace.name(UPDATED_NAME);

        restWorkspaceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWorkspace.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedWorkspace))
            )
            .andExpect(status().isOk());

        // Validate the Workspace in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWorkspaceToMatchAllProperties(updatedWorkspace);
    }

    @Test
    @Transactional
    void putNonExistingWorkspace() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workspace.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkspaceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workspace.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workspace))
            )
            .andExpect(status().isBadRequest());

        // Validate the Workspace in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkspace() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workspace.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkspaceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(workspace))
            )
            .andExpect(status().isBadRequest());

        // Validate the Workspace in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkspace() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workspace.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkspaceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workspace)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Workspace in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWorkspaceWithPatch() throws Exception {
        // Initialize the database
        workspaceRepository.saveAndFlush(workspace);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workspace using partial update
        Workspace partialUpdatedWorkspace = new Workspace();
        partialUpdatedWorkspace.setId(workspace.getId());

        partialUpdatedWorkspace.name(UPDATED_NAME);

        restWorkspaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkspace.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWorkspace))
            )
            .andExpect(status().isOk());

        // Validate the Workspace in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkspaceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWorkspace, workspace),
            getPersistedWorkspace(workspace)
        );
    }

    @Test
    @Transactional
    void fullUpdateWorkspaceWithPatch() throws Exception {
        // Initialize the database
        workspaceRepository.saveAndFlush(workspace);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workspace using partial update
        Workspace partialUpdatedWorkspace = new Workspace();
        partialUpdatedWorkspace.setId(workspace.getId());

        partialUpdatedWorkspace.name(UPDATED_NAME);

        restWorkspaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkspace.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWorkspace))
            )
            .andExpect(status().isOk());

        // Validate the Workspace in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkspaceUpdatableFieldsEquals(partialUpdatedWorkspace, getPersistedWorkspace(partialUpdatedWorkspace));
    }

    @Test
    @Transactional
    void patchNonExistingWorkspace() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workspace.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkspaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workspace.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(workspace))
            )
            .andExpect(status().isBadRequest());

        // Validate the Workspace in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkspace() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workspace.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkspaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(workspace))
            )
            .andExpect(status().isBadRequest());

        // Validate the Workspace in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkspace() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workspace.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkspaceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(workspace)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Workspace in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWorkspace() throws Exception {
        // Initialize the database
        workspaceRepository.saveAndFlush(workspace);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the workspace
        restWorkspaceMockMvc
            .perform(delete(ENTITY_API_URL_ID, workspace.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return workspaceRepository.count();
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

    protected Workspace getPersistedWorkspace(Workspace workspace) {
        return workspaceRepository.findById(workspace.getId()).orElseThrow();
    }

    protected void assertPersistedWorkspaceToMatchAllProperties(Workspace expectedWorkspace) {
        assertWorkspaceAllPropertiesEquals(expectedWorkspace, getPersistedWorkspace(expectedWorkspace));
    }

    protected void assertPersistedWorkspaceToMatchUpdatableProperties(Workspace expectedWorkspace) {
        assertWorkspaceAllUpdatablePropertiesEquals(expectedWorkspace, getPersistedWorkspace(expectedWorkspace));
    }
}
