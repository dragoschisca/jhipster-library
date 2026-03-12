package com.library.app.web.rest;

import com.library.app.repository.BookAuthorRepository;
import com.library.app.service.BookAuthorService;
import com.library.app.service.dto.BookAuthorDTO;
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

@RestController
@RequestMapping("/api/book-authors")
public class BookAuthorResource {

    private static final Logger LOG = LoggerFactory.getLogger(BookAuthorResource.class);

    private static final String ENTITY_NAME = "bookAuthor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookAuthorService bookAuthorService;
    private final BookAuthorRepository bookAuthorRepository;

    public BookAuthorResource(BookAuthorService bookAuthorService, BookAuthorRepository bookAuthorRepository) {
        this.bookAuthorService = bookAuthorService;
        this.bookAuthorRepository = bookAuthorRepository;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookAuthorDTO> createBookAuthor(@Valid @RequestBody BookAuthorDTO bookAuthorDTO) throws URISyntaxException {
        LOG.debug("REST request to save BookAuthor : {}", bookAuthorDTO);
        if (bookAuthorDTO.getId() != null) {
            throw new BadRequestAlertException("A new bookAuthor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bookAuthorDTO = bookAuthorService.save(bookAuthorDTO);
        return ResponseEntity.created(new URI("/api/book-authors/" + bookAuthorDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bookAuthorDTO.getId().toString()))
            .body(bookAuthorDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookAuthorDTO> updateBookAuthor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookAuthorDTO bookAuthorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BookAuthor : {}, {}", id, bookAuthorDTO);
        if (bookAuthorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookAuthorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookAuthorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookAuthorDTO = bookAuthorService.update(bookAuthorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookAuthorDTO.getId().toString()))
            .body(bookAuthorDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookAuthorDTO> partialUpdateBookAuthor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookAuthorDTO bookAuthorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BookAuthor partially : {}, {}", id, bookAuthorDTO);
        if (bookAuthorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookAuthorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookAuthorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookAuthorDTO> result = bookAuthorService.partialUpdate(bookAuthorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookAuthorDTO.getId().toString())
        );
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<BookAuthorDTO> getAllBookAuthors(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all BookAuthors");
        return bookAuthorService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookAuthorDTO> getBookAuthor(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BookAuthor : {}", id);
        Optional<BookAuthorDTO> bookAuthorDTO = bookAuthorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookAuthorDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBookAuthor(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BookAuthor : {}", id);
        bookAuthorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
