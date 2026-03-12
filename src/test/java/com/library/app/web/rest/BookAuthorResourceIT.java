package com.library.app.web.rest;

import static com.library.app.domain.BookAuthorAsserts.*;
import static com.library.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.app.IntegrationTest;
import com.library.app.domain.BookAuthor;
import com.library.app.repository.BookAuthorRepository;
import com.library.app.service.BookAuthorService;
import com.library.app.service.dto.BookAuthorDTO;
import com.library.app.service.mapper.BookAuthorMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookAuthorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookAuthorResourceIT {

    private static final String DEFAULT_BOOK_ISBN = "AAAAAAAAAA";
    private static final String UPDATED_BOOK_ISBN = "BBBBBBBBBB";

    private static final Integer DEFAULT_AUTHOR_ID = 1;
    private static final Integer UPDATED_AUTHOR_ID = 2;

    private static final String ENTITY_API_URL = "/api/book-authors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookAuthorRepository bookAuthorRepository;

    @Mock
    private BookAuthorRepository bookAuthorRepositoryMock;

    @Autowired
    private BookAuthorMapper bookAuthorMapper;

    @Mock
    private BookAuthorService bookAuthorServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookAuthorMockMvc;

    private BookAuthor bookAuthor;

    private BookAuthor insertedBookAuthor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookAuthor createEntity() {
        return new BookAuthor().bookIsbn(DEFAULT_BOOK_ISBN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookAuthor createUpdatedEntity() {
        return new BookAuthor().bookIsbn(UPDATED_BOOK_ISBN);
    }

    @BeforeEach
    void initTest() {
        bookAuthor = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBookAuthor != null) {
            bookAuthorRepository.delete(insertedBookAuthor);
            insertedBookAuthor = null;
        }
    }

    @Test
    @Transactional
    void createBookAuthor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookAuthor
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(bookAuthor);
        var returnedBookAuthorDTO = om.readValue(
            restBookAuthorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookAuthorDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookAuthorDTO.class
        );

        // Validate the BookAuthor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBookAuthor = bookAuthorMapper.toEntity(returnedBookAuthorDTO);
        assertBookAuthorUpdatableFieldsEquals(returnedBookAuthor, getPersistedBookAuthor(returnedBookAuthor));

        insertedBookAuthor = returnedBookAuthor;
    }

    @Test
    @Transactional
    void createBookAuthorWithExistingId() throws Exception {
        // Create the BookAuthor with an existing ID
        bookAuthor.setId(1L);
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(bookAuthor);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookAuthorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookAuthorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBookIsbnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookAuthor.setBookIsbn(null);

        // Create the BookAuthor, which fails.
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(bookAuthor);

        restBookAuthorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookAuthorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAuthorIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null

        // Create the BookAuthor, which fails.
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(bookAuthor);

        restBookAuthorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookAuthorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBookAuthors() throws Exception {
        // Initialize the database
        insertedBookAuthor = bookAuthorRepository.saveAndFlush(bookAuthor);

        // Get all the bookAuthorList
        restBookAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookAuthor.getId().intValue())))
            .andExpect(jsonPath("$.[*].bookIsbn").value(hasItem(DEFAULT_BOOK_ISBN)))
            .andExpect(jsonPath("$.[*].authorId").value(hasItem(DEFAULT_AUTHOR_ID)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookAuthorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookAuthorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookAuthorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookAuthorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookAuthorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookAuthorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookAuthorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookAuthorRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBookAuthor() throws Exception {
        // Initialize the database
        insertedBookAuthor = bookAuthorRepository.saveAndFlush(bookAuthor);

        // Get the bookAuthor
        restBookAuthorMockMvc
            .perform(get(ENTITY_API_URL_ID, bookAuthor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookAuthor.getId().intValue()))
            .andExpect(jsonPath("$.bookIsbn").value(DEFAULT_BOOK_ISBN))
            .andExpect(jsonPath("$.authorId").value(DEFAULT_AUTHOR_ID));
    }

    @Test
    @Transactional
    void getNonExistingBookAuthor() throws Exception {
        // Get the bookAuthor
        restBookAuthorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookAuthor() throws Exception {
        // Initialize the database
        insertedBookAuthor = bookAuthorRepository.saveAndFlush(bookAuthor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookAuthor
        BookAuthor updatedBookAuthor = bookAuthorRepository.findById(bookAuthor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookAuthor are not directly saved in db
        em.detach(updatedBookAuthor);
        updatedBookAuthor.bookIsbn(UPDATED_BOOK_ISBN);
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(updatedBookAuthor);

        restBookAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookAuthorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookAuthorDTO))
            )
            .andExpect(status().isOk());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookAuthorToMatchAllProperties(updatedBookAuthor);
    }

    @Test
    @Transactional
    void putNonExistingBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthor.setId(longCount.incrementAndGet());

        // Create the BookAuthor
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(bookAuthor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookAuthorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookAuthorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthor.setId(longCount.incrementAndGet());

        // Create the BookAuthor
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(bookAuthor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookAuthorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthor.setId(longCount.incrementAndGet());

        // Create the BookAuthor
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(bookAuthor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookAuthorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookAuthorWithPatch() throws Exception {
        // Initialize the database
        insertedBookAuthor = bookAuthorRepository.saveAndFlush(bookAuthor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookAuthor using partial update
        BookAuthor partialUpdatedBookAuthor = new BookAuthor();
        partialUpdatedBookAuthor.setId(bookAuthor.getId());

        partialUpdatedBookAuthor.bookIsbn(UPDATED_BOOK_ISBN);

        restBookAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookAuthor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookAuthor))
            )
            .andExpect(status().isOk());

        // Validate the BookAuthor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookAuthorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBookAuthor, bookAuthor),
            getPersistedBookAuthor(bookAuthor)
        );
    }

    @Test
    @Transactional
    void fullUpdateBookAuthorWithPatch() throws Exception {
        // Initialize the database
        insertedBookAuthor = bookAuthorRepository.saveAndFlush(bookAuthor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookAuthor using partial update
        BookAuthor partialUpdatedBookAuthor = new BookAuthor();
        partialUpdatedBookAuthor.setId(bookAuthor.getId());

        partialUpdatedBookAuthor.bookIsbn(UPDATED_BOOK_ISBN);

        restBookAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookAuthor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookAuthor))
            )
            .andExpect(status().isOk());

        // Validate the BookAuthor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookAuthorUpdatableFieldsEquals(partialUpdatedBookAuthor, getPersistedBookAuthor(partialUpdatedBookAuthor));
    }

    @Test
    @Transactional
    void patchNonExistingBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthor.setId(longCount.incrementAndGet());

        // Create the BookAuthor
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(bookAuthor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookAuthorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookAuthorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthor.setId(longCount.incrementAndGet());

        // Create the BookAuthor
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(bookAuthor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookAuthorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthor.setId(longCount.incrementAndGet());

        // Create the BookAuthor
        BookAuthorDTO bookAuthorDTO = bookAuthorMapper.toDto(bookAuthor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookAuthorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookAuthor() throws Exception {
        // Initialize the database
        insertedBookAuthor = bookAuthorRepository.saveAndFlush(bookAuthor);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookAuthor
        restBookAuthorMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookAuthor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookAuthorRepository.count();
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

    protected BookAuthor getPersistedBookAuthor(BookAuthor bookAuthor) {
        return bookAuthorRepository.findById(bookAuthor.getId()).orElseThrow();
    }

    protected void assertPersistedBookAuthorToMatchAllProperties(BookAuthor expectedBookAuthor) {
        assertBookAuthorAllPropertiesEquals(expectedBookAuthor, getPersistedBookAuthor(expectedBookAuthor));
    }

    protected void assertPersistedBookAuthorToMatchUpdatableProperties(BookAuthor expectedBookAuthor) {
        assertBookAuthorAllUpdatablePropertiesEquals(expectedBookAuthor, getPersistedBookAuthor(expectedBookAuthor));
    }
}
