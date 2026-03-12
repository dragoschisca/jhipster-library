package com.library.app.service.mapper;

import static com.library.app.domain.BookAuthorAsserts.*;
import static com.library.app.domain.BookAuthorTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookAuthorMapperTest {

    private BookAuthorMapper bookAuthorMapper;

    @BeforeEach
    void setUp() {
        bookAuthorMapper = new BookAuthorMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBookAuthorSample1();
        var actual = bookAuthorMapper.toEntity(bookAuthorMapper.toDto(expected));
        assertBookAuthorAllPropertiesEquals(expected, actual);
    }
}
