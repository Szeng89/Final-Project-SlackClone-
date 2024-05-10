package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Workspace;
import com.mycompany.myapp.repository.WorkspaceRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Workspace}.
 */
@RestController
@RequestMapping("/api/workspaces")
@Transactional
public class WorkspaceResource {

    private final Logger log = LoggerFactory.getLogger(WorkspaceResource.class);

    private static final String ENTITY_NAME = "workspace";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceResource(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    /**
     * {@code POST  /workspaces} : Create a new workspace.
     *
     * @param workspace the workspace to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workspace, or with status {@code 400 (Bad Request)} if the workspace has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Workspace> createWorkspace(@Valid @RequestBody Workspace workspace) throws URISyntaxException {
        log.debug("REST request to save Workspace : {}", workspace);
        if (workspace.getId() != null) {
            throw new BadRequestAlertException("A new workspace cannot already have an ID", ENTITY_NAME, "idexists");
        }
        workspace = workspaceRepository.save(workspace);
        return ResponseEntity.created(new URI("/api/workspaces/" + workspace.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, workspace.getId().toString()))
            .body(workspace);
    }

    /**
     * {@code PUT  /workspaces/:id} : Updates an existing workspace.
     *
     * @param id the id of the workspace to save.
     * @param workspace the workspace to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workspace,
     * or with status {@code 400 (Bad Request)} if the workspace is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workspace couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Workspace> updateWorkspace(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Workspace workspace
    ) throws URISyntaxException {
        log.debug("REST request to update Workspace : {}, {}", id, workspace);
        if (workspace.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workspace.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workspaceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        workspace = workspaceRepository.save(workspace);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workspace.getId().toString()))
            .body(workspace);
    }

    /**
     * {@code PATCH  /workspaces/:id} : Partial updates given fields of an existing workspace, field will ignore if it is null
     *
     * @param id the id of the workspace to save.
     * @param workspace the workspace to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workspace,
     * or with status {@code 400 (Bad Request)} if the workspace is not valid,
     * or with status {@code 404 (Not Found)} if the workspace is not found,
     * or with status {@code 500 (Internal Server Error)} if the workspace couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Workspace> partialUpdateWorkspace(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Workspace workspace
    ) throws URISyntaxException {
        log.debug("REST request to partial update Workspace partially : {}, {}", id, workspace);
        if (workspace.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workspace.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workspaceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Workspace> result = workspaceRepository
            .findById(workspace.getId())
            .map(existingWorkspace -> {
                if (workspace.getName() != null) {
                    existingWorkspace.setName(workspace.getName());
                }

                return existingWorkspace;
            })
            .map(workspaceRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workspace.getId().toString())
        );
    }

    /**
     * {@code GET  /workspaces} : get all the workspaces.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workspaces in body.
     */
    @GetMapping("")
    public List<Workspace> getAllWorkspaces() {
        log.debug("REST request to get all Workspaces");
        return workspaceRepository.findAll();
    }

    /**
     * {@code GET  /workspaces/:id} : get the "id" workspace.
     *
     * @param id the id of the workspace to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workspace, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Workspace> getWorkspace(@PathVariable("id") Long id) {
        log.debug("REST request to get Workspace : {}", id);
        Optional<Workspace> workspace = workspaceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(workspace);
    }

    /**
     * {@code DELETE  /workspaces/:id} : delete the "id" workspace.
     *
     * @param id the id of the workspace to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable("id") Long id) {
        log.debug("REST request to delete Workspace : {}", id);
        workspaceRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
