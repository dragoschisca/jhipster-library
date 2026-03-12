package com.library.app.web.rest;

import static com.library.app.domain.BorrowedBookAsserts.*;
import static com.library.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.app.IntegrationTest;
import com.library.app.domain.BorrowedBook;
import com.library.app.repository.BorrowedBookRepository;
import com.library.app.service.BorrowedBookService;
import com.library.app.service.dto.BorrowedBookDTO;
import com.library.app.service.mapper.BorrowedBookMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link BorrowedBookResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BorrowedBookResourceIT {

    private static final String DEFAULT_BOOK_ISBN = "AAAAAAAAAA";
    private static final String UPDATED_BOOK_ISBN = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BORROW_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BORROW_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/borrowed-books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @Mock
    private BorrowedBookRepository borrowedBookRepositoryMock;

    @Autowired
    private BorrowedBookMapper borrowedBookMapper;

    @Mock
    private BorrowedBookService borrowedBookServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBorrowedBookMockMvc;

    private BorrowedBook borrowedBook;

    private BorrowedBook insertedBorrowedBook;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BorrowedBook createEntity() {
        return new BorrowedBook().bookIsbn(DEFAULT_BOOK_ISBN).borrowDate(DEFAULT_BORROW_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BorrowedBook createUpdatedEntity() {
        return new BorrowedBook().bookIsbn(UPDATED_BOOK_ISBN).borrowDate(UPDATED_BORROW_DATE);
    }

    @BeforeEach
    void initTest() {
        borrowedBook = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBorrowedBook != null) {
            borrowedBookRepository.delete(insertedBorrowedBook);
            insertedBorrowedBook = null;
        }
    }

    @Test
    @Transactional
    void createBorrowedBook() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BorrowedBook
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);
        var returnedBorrowedBookDTO = om.readValue(
            restBorrowedBookMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(borrowedBookDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BorrowedBookDTO.class
        );

        // Validate the BorrowedBook in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBorrowedBook = borrowedBookMapper.toEntity(returnedBorrowedBookDTO);
        assertBorrowedBookUpdatableFieldsEquals(returnedBorrowedBook, getPersistedBorrowedBook(returnedBorrowedBook));

        insertedBorrowedBook = returnedBorrowedBook;
    }

    @Test
    @Transactional
    void createBorrowedBookWithExistingId() throws Exception {
        // Create the BorrowedBook with an existing ID
        borrowedBook.setId(1L);
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBorrowedBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(borrowedBookDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBookIsbnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        borrowedBook.setBookIsbn(null);

        // Create the BorrowedBook, which fails.
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);

        restBorrowedBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(borrowedBookDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBorrowedBooks() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList
        restBorrowedBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrowedBook.getId().intValue())))
            .andExpect(jsonPath("$.[*].bookIsbn").value(hasItem(DEFAULT_BOOK_ISBN)))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBorrowedBooksWithEagerRelationshipsIsEnabled() throws Exception {
        when(borrowedBookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBorrowedBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(borrowedBookServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBorrowedBooksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(borrowedBookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBorrowedBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(borrowedBookRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBorrowedBook() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get the borrowedBook
        restBorrowedBookMockMvc
            .perform(get(ENTITY_API_URL_ID, borrowedBook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(borrowedBook.getId().intValue()))
            .andExpect(jsonPath("$.bookIsbn").value(DEFAULT_BOOK_ISBN))
            .andExpect(jsonPath("$.borrowDate").value(DEFAULT_BORROW_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBorrowedBook() throws Exception {
        // Get the borrowedBook
        restBorrowedBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBorrowedBook() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the borrowedBook
        BorrowedBook updatedBorrowedBook = borrowedBookRepository.findById(borrowedBook.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBorrowedBook are not directly saved in db
        em.detach(updatedBorrowedBook);
        updatedBorrowedBook.bookIsbn(UPDATED_BOOK_ISBN).borrowDate(UPDATED_BORROW_DATE);
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(updatedBorrowedBook);

        restBorrowedBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, borrowedBookDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(borrowedBookDTO))
            )
            .andExpect(status().isOk());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBorrowedBookToMatchAllProperties(updatedBorrowedBook);
    }

    @Test
    @Transactional
    void putNonExistingBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // Create the BorrowedBook
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, borrowedBookDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(borrowedBookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // Create the BorrowedBook
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(borrowedBookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // Create the BorrowedBook
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(borrowedBookDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBorrowedBookWithPatch() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the borrowedBook using partial update
        BorrowedBook partialUpdatedBorrowedBook = new BorrowedBook();
        partialUpdatedBorrowedBook.setId(borrowedBook.getId());

        partialUpdatedBorrowedBook.bookIsbn(UPDATED_BOOK_ISBN).borrowDate(UPDATED_BORROW_DATE);

        restBorrowedBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBorrowedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBorrowedBook))
            )
            .andExpect(status().isOk());

        // Validate the BorrowedBook in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBorrowedBookUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBorrowedBook, borrowedBook),
            getPersistedBorrowedBook(borrowedBook)
        );
    }

    @Test
    @Transactional
    void fullUpdateBorrowedBookWithPatch() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the borrowedBook using partial update
        BorrowedBook partialUpdatedBorrowedBook = new BorrowedBook();
        partialUpdatedBorrowedBook.setId(borrowedBook.getId());

        partialUpdatedBorrowedBook.bookIsbn(UPDATED_BOOK_ISBN).borrowDate(UPDATED_BORROW_DATE);

        restBorrowedBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBorrowedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBorrowedBook))
            )
            .andExpect(status().isOk());

        // Validate the BorrowedBook in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBorrowedBookUpdatableFieldsEquals(partialUpdatedBorrowedBook, getPersistedBorrowedBook(partialUpdatedBorrowedBook));
    }

    @Test
    @Transactional
    void patchNonExistingBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // Create the BorrowedBook
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, borrowedBookDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(borrowedBookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // Create the BorrowedBook
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(borrowedBookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // Create the BorrowedBook
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(borrowedBookDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBorrowedBook() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the borrowedBook
        restBorrowedBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, borrowedBook.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return borrowedBookRepository.count();
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

    protected BorrowedBook getPersistedBorrowedBook(BorrowedBook borrowedBook) {
        return borrowedBookRepository.findById(borrowedBook.getId()).orElseThrow();
    }

    protected void assertPersistedBorrowedBookToMatchAllProperties(BorrowedBook expectedBorrowedBook) {
        assertBorrowedBookAllPropertiesEquals(expectedBorrowedBook, getPersistedBorrowedBook(expectedBorrowedBook));
    }

    protected void assertPersistedBorrowedBookToMatchUpdatableProperties(BorrowedBook expectedBorrowedBook) {
        assertBorrowedBookAllUpdatablePropertiesEquals(expectedBorrowedBook, getPersistedBorrowedBook(expectedBorrowedBook));
    }
}
