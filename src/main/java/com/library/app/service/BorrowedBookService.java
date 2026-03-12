package com.library.app.service;

import com.library.app.service.dto.BorrowedBookDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowedBookService {
    BorrowedBookDTO save(BorrowedBookDTO borrowedBookDTO);

    BorrowedBookDTO update(BorrowedBookDTO borrowedBookDTO);

    Optional<BorrowedBookDTO> partialUpdate(BorrowedBookDTO borrowedBookDTO);

    Page<BorrowedBookDTO> findAll(Pageable pageable);

    Page<BorrowedBookDTO> findAllWithEagerRelationships(Pageable pageable);

    Optional<BorrowedBookDTO> findOne(Long id);

    void delete(Long id);

    BorrowedBookDTO borrowBook(BorrowedBookDTO borrowedBookDTO);

    BorrowedBookDTO returnBook(Long borrowedBookId);
}
