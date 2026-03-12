package com.library.app.web.rest;

import com.library.app.repository.PublisherRepository;
import com.library.app.service.PublisherService;
import com.library.app.service.dto.PublisherDTO;
import com.library.app.web.rest.errors.BadRequestAlertException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.library.app.domain.Publisher}.
 */
@RestController
@RequestMapping("/api/publishers")
public class PublisherResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublisherResource.class);

    private static final String ENTITY_NAME = "publisher";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PublisherService publisherService;

    private final PublisherRepository publisherRepository;

    public PublisherResource(PublisherService publisherService, PublisherRepository publisherRepository) {
        this.publisherService = publisherService;
        this.publisherRepository = publisherRepository;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PublisherDTO> createPublisher(@Valid @RequestBody PublisherDTO publisherDTO) throws URISyntaxException {
        LOG.debug("REST request to save Publisher : {}", publisherDTO);
        if (publisherDTO.getId() != null) {
            throw new BadRequestAlertException("A new publisher cannot already have an ID", ENTITY_NAME, "idexists");
        }
        publisherDTO = publisherService.save(publisherDTO);
        return ResponseEntity.created(new URI("/api/publishers/" + publisherDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, publisherDTO.getId().toString()))
            .body(publisherDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PublisherDTO> updatePublisher(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PublisherDTO publisherDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Publisher : {}, {}", id, publisherDTO);
        if (publisherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publisherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publisherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        publisherDTO = publisherService.update(publisherDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publisherDTO.getId().toString()))
            .body(publisherDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PublisherDTO> partialUpdatePublisher(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PublisherDTO publisherDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Publisher partially : {}, {}", id, publisherDTO);
        if (publisherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publisherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publisherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PublisherDTO> result = publisherService.partialUpdate(publisherDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publisherDTO.getId().toString())
        );
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<PublisherDTO>> getAllPublishers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Publishers");
        Page<PublisherDTO> page = publisherService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PublisherDTO> getPublisher(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Publisher : {}", id);
        Optional<PublisherDTO> publisherDTO = publisherService.findOne(id);
        return ResponseUtil.wrapOrNotFound(publisherDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePublisher(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Publisher : {}", id);
        publisherService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
