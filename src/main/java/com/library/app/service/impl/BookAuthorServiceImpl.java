package com.library.app.service.impl;

import com.library.app.domain.BookAuthor;
import com.library.app.repository.BookAuthorRepository;
import com.library.app.service.BookAuthorService;
import com.library.app.service.dto.BookAuthorDTO;
import com.library.app.service.mapper.BookAuthorMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.library.app.domain.BookAuthor}.
 */
@Service
@Transactional
public class BookAuthorServiceImpl implements BookAuthorService {

    private static final Logger LOG = LoggerFactory.getLogger(BookAuthorServiceImpl.class);

    private final BookAuthorRepository bookAuthorRepository;

    private final BookAuthorMapper bookAuthorMapper;

    public BookAuthorServiceImpl(BookAuthorRepository bookAuthorRepository, BookAuthorMapper bookAuthorMapper) {
        this.bookAuthorRepository = bookAuthorRepository;
        this.bookAuthorMapper = bookAuthorMapper;
    }

    @Override
    public BookAuthorDTO save(BookAuthorDTO bookAuthorDTO) {
        LOG.debug("Request to save BookAuthor : {}", bookAuthorDTO);
        BookAuthor bookAuthor = bookAuthorMapper.toEntity(bookAuthorDTO);
        bookAuthor = bookAuthorRepository.save(bookAuthor);
        return bookAuthorMapper.toDto(bookAuthor);
    }

    @Override
    public BookAuthorDTO update(BookAuthorDTO bookAuthorDTO) {
        LOG.debug("Request to update BookAuthor : {}", bookAuthorDTO);
        BookAuthor bookAuthor = bookAuthorMapper.toEntity(bookAuthorDTO);
        bookAuthor = bookAuthorRepository.save(bookAuthor);
        return bookAuthorMapper.toDto(bookAuthor);
    }

    @Override
    public Optional<BookAuthorDTO> partialUpdate(BookAuthorDTO bookAuthorDTO) {
        LOG.debug("Request to partially update BookAuthor : {}", bookAuthorDTO);

        return bookAuthorRepository
            .findById(bookAuthorDTO.getId())
            .map(existingBookAuthor -> {
                bookAuthorMapper.partialUpdate(existingBookAuthor, bookAuthorDTO);

                return existingBookAuthor;
            })
            .map(bookAuthorRepository::save)
            .map(bookAuthorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookAuthorDTO> findAll() {
        LOG.debug("Request to get all BookAuthors");
        return bookAuthorRepository.findAll().stream().map(bookAuthorMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<BookAuthorDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookAuthorRepository.findAllWithEagerRelationships(pageable).map(bookAuthorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookAuthorDTO> findOne(Long id) {
        LOG.debug("Request to get BookAuthor : {}", id);
        return bookAuthorRepository.findOneWithEagerRelationships(id).map(bookAuthorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete BookAuthor : {}", id);
        bookAuthorRepository.deleteById(id);
    }
}
