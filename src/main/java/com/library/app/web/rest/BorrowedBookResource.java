package com.library.app.web.rest;

import com.library.app.repository.BorrowedBookRepository;
import com.library.app.service.BorrowedBookService;
import com.library.app.service.dto.BorrowedBookDTO;
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
 * REST controller for managing {@link com.library.app.domain.BorrowedBook}.
 */
@RestController
@RequestMapping("/api/borrowed-books")
public class BorrowedBookResource {

    private static final Logger LOG = LoggerFactory.getLogger(BorrowedBookResource.class);

    private static final String ENTITY_NAME = "borrowedBook";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BorrowedBookService borrowedBookService;

    private final BorrowedBookRepository borrowedBookRepository;

    public BorrowedBookResource(BorrowedBookService borrowedBookService, BorrowedBookRepository borrowedBookRepository) {
        this.borrowedBookService = borrowedBookService;
        this.borrowedBookRepository = borrowedBookRepository;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BorrowedBookDTO> createBorrowedBook(@Valid @RequestBody BorrowedBookDTO borrowedBookDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save BorrowedBook : {}", borrowedBookDTO);
        if (borrowedBookDTO.getId() != null) {
            throw new BadRequestAlertException("A new borrowedBook cannot already have an ID", ENTITY_NAME, "idexists");
        }
        borrowedBookDTO = borrowedBookService.save(borrowedBookDTO);
        return ResponseEntity.created(new URI("/api/borrowed-books/" + borrowedBookDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, borrowedBookDTO.getId().toString()))
            .body(borrowedBookDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BorrowedBookDTO> updateBorrowedBook(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BorrowedBookDTO borrowedBookDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BorrowedBook : {}, {}", id, borrowedBookDTO);
        if (borrowedBookDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, borrowedBookDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!borrowedBookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        borrowedBookDTO = borrowedBookService.update(borrowedBookDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, borrowedBookDTO.getId().toString()))
            .body(borrowedBookDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BorrowedBookDTO> partialUpdateBorrowedBook(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BorrowedBookDTO borrowedBookDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BorrowedBook partially : {}, {}", id, borrowedBookDTO);
        if (borrowedBookDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, borrowedBookDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!borrowedBookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BorrowedBookDTO> result = borrowedBookService.partialUpdate(borrowedBookDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, borrowedBookDTO.getId().toString())
        );
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_LIBRARIAN')")
    public ResponseEntity<List<BorrowedBookDTO>> getAllBorrowedBooks(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of BorrowedBooks");
        Page<BorrowedBookDTO> page;
        if (eagerload) {
            page = borrowedBookService.findAllWithEagerRelationships(pageable);
        } else {
            page = borrowedBookService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_LIBRARIAN')")
    public ResponseEntity<BorrowedBookDTO> getBorrowedBook(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BorrowedBook : {}", id);
        Optional<BorrowedBookDTO> borrowedBookDTO = borrowedBookService.findOne(id);
        return ResponseUtil.wrapOrNotFound(borrowedBookDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBorrowedBook(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BorrowedBook : {}", id);
        borrowedBookService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/borrow")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    public ResponseEntity<BorrowedBookDTO> borrowBook(@RequestBody BorrowedBookDTO borrowedBookDTO) {
        LOG.debug("REST request to borrow a Book : {}", borrowedBookDTO);
        BorrowedBookDTO result = borrowedBookService.borrowBook(borrowedBookDTO);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/return/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    public ResponseEntity<BorrowedBookDTO> returnBook(@PathVariable Long id) {
        LOG.debug("REST request to return a Book, BorrowedBook id : {}", id);
        BorrowedBookDTO result = borrowedBookService.returnBook(id);
        return ResponseEntity.ok().body(result);
    }
}
