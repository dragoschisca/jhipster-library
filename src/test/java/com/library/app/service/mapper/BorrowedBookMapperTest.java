package com.library.app.service.mapper;

import static com.library.app.domain.BorrowedBookAsserts.*;
import static com.library.app.domain.BorrowedBookTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BorrowedBookMapperTest {

    private BorrowedBookMapper borrowedBookMapper;

    @BeforeEach
    void setUp() {
        borrowedBookMapper = new BorrowedBookMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBorrowedBookSample1();
        var actual = borrowedBookMapper.toEntity(borrowedBookMapper.toDto(expected));
        assertBorrowedBookAllPropertiesEquals(expected, actual);
    }
}
