package com.library.app.service;

import com.library.app.service.dto.BookAuthorDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.library.app.domain.BookAuthor}.
 */
public interface BookAuthorService {
    /**
     * Save a bookAuthor.
     *
     * @param bookAuthorDTO the entity to save.
     * @return the persisted entity.
     */
    BookAuthorDTO save(BookAuthorDTO bookAuthorDTO);

    /**
     * Updates a bookAuthor.
     *
     * @param bookAuthorDTO the entity to update.
     * @return the persisted entity.
     */
    BookAuthorDTO update(BookAuthorDTO bookAuthorDTO);

    /**
     * Partially updates a bookAuthor.
     *
     * @param bookAuthorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BookAuthorDTO> partialUpdate(BookAuthorDTO bookAuthorDTO);

    /**
     * Get all the bookAuthors.
     *
     * @return the list of entities.
     */
    List<BookAuthorDTO> findAll();

    /**
     * Get all the bookAuthors with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BookAuthorDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" bookAuthor.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BookAuthorDTO> findOne(Long id);

    /**
     * Delete the "id" bookAuthor.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
