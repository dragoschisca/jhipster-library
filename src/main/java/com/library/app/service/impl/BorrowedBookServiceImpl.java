package com.library.app.service.impl;

import com.library.app.domain.Book;
import com.library.app.domain.BorrowedBook;
import com.library.app.repository.BookRepository;
import com.library.app.repository.BorrowedBookRepository;
import com.library.app.service.BorrowedBookService;
import com.library.app.service.dto.BorrowedBookDTO;
import com.library.app.service.mapper.BorrowedBookMapper;
import java.time.LocalDate;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BorrowedBookServiceImpl implements BorrowedBookService {

    private final Logger log = LoggerFactory.getLogger(BorrowedBookServiceImpl.class);

    private final BorrowedBookRepository borrowedBookRepository;
    private final BorrowedBookMapper borrowedBookMapper;
    private final BookRepository bookRepository;

    public BorrowedBookServiceImpl(
        BorrowedBookRepository borrowedBookRepository,
        BorrowedBookMapper borrowedBookMapper,
        BookRepository bookRepository
    ) {
        this.borrowedBookRepository = borrowedBookRepository;
        this.borrowedBookMapper = borrowedBookMapper;
        this.bookRepository = bookRepository;
    }

    @Override
    public BorrowedBookDTO save(BorrowedBookDTO borrowedBookDTO) {
        log.debug("Request to save BorrowedBook : {}", borrowedBookDTO);
        BorrowedBook borrowedBook = borrowedBookMapper.toEntity(borrowedBookDTO);
        borrowedBook = borrowedBookRepository.save(borrowedBook);
        return borrowedBookMapper.toDto(borrowedBook);
    }

    @Override
    public BorrowedBookDTO update(BorrowedBookDTO borrowedBookDTO) {
        log.debug("Request to update BorrowedBook : {}", borrowedBookDTO);
        BorrowedBook borrowedBook = borrowedBookMapper.toEntity(borrowedBookDTO);
        borrowedBook = borrowedBookRepository.save(borrowedBook);
        return borrowedBookMapper.toDto(borrowedBook);
    }

    @Override
    public Optional<BorrowedBookDTO> partialUpdate(BorrowedBookDTO borrowedBookDTO) {
        log.debug("Request to partially update BorrowedBook : {}", borrowedBookDTO);
        return borrowedBookRepository
            .findById(borrowedBookDTO.getId())
            .map(existingBorrowedBook -> {
                borrowedBookMapper.partialUpdate(existingBorrowedBook, borrowedBookDTO);
                return existingBorrowedBook;
            })
            .map(borrowedBookRepository::save)
            .map(borrowedBookMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BorrowedBookDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BorrowedBooks");
        return borrowedBookRepository.findAll(pageable).map(borrowedBookMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BorrowedBookDTO> findAllWithEagerRelationships(Pageable pageable) {
        log.debug("Request to get all BorrowedBooks with eager relationships");
        return borrowedBookRepository.findAllWithEagerRelationships(pageable).map(borrowedBookMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BorrowedBookDTO> findOne(Long id) {
        log.debug("Request to get BorrowedBook : {}", id);
        return borrowedBookRepository.findById(id).map(borrowedBookMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete BorrowedBook : {}", id);
        borrowedBookRepository.deleteById(id);
    }

    @Override
    public BorrowedBookDTO borrowBook(BorrowedBookDTO borrowedBookDTO) {
        log.debug("Request to borrow Book : {}", borrowedBookDTO);

        // Gaseste cartea dupa ISBN
        Book book = bookRepository
            .findById(borrowedBookDTO.getBook().getId())
            .orElseThrow(() -> new RuntimeException("Cartea nu a fost gasita"));

        // Verifica daca mai sunt exemplare disponibile
        if (book.getCopies() == null || book.getCopies() <= 0) {
            throw new RuntimeException("Nu mai sunt exemplare disponibile pentru aceasta carte");
        }

        // Scade numarul de exemplare
        book.setCopies(book.getCopies() - 1);
        bookRepository.save(book);

        borrowedBookDTO.setBorrowDate(LocalDate.now());

        BorrowedBook borrowedBook = borrowedBookMapper.toEntity(borrowedBookDTO);
        borrowedBook = borrowedBookRepository.save(borrowedBook);
        return borrowedBookMapper.toDto(borrowedBook);
    }

    @Override
    public BorrowedBookDTO returnBook(Long borrowedBookId) {
        log.debug("Request to return Book with BorrowedBook id : {}", borrowedBookId);

        // Gaseste inregistrarea de imprumut
        BorrowedBook borrowedBook = borrowedBookRepository
            .findById(borrowedBookId)
            .orElseThrow(() -> new RuntimeException("Inregistrarea de imprumut nu a fost gasita"));

        // Gaseste cartea si creste numarul de exemplare
        Book book = borrowedBook.getBook();
        book.setCopies(book.getCopies() + 1);
        bookRepository.save(book);

        // Sterge inregistrarea de imprumut
        borrowedBookRepository.deleteById(borrowedBookId);

        return borrowedBookMapper.toDto(borrowedBook);
    }
}
