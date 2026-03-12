package com.library.app.service.mapper;

import com.library.app.domain.Book;
import com.library.app.domain.BorrowedBook;
import com.library.app.domain.Client;
import com.library.app.service.dto.BookDTO;
import com.library.app.service.dto.BorrowedBookDTO;
import com.library.app.service.dto.ClientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BorrowedBook} and its DTO {@link BorrowedBookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BorrowedBookMapper extends EntityMapper<BorrowedBookDTO, BorrowedBook> {
    @Mapping(target = "book", source = "book", qualifiedByName = "bookIsbn")
    @Mapping(target = "client", source = "client", qualifiedByName = "clientFirstName")
    BorrowedBookDTO toDto(BorrowedBook s);

    @Named("bookIsbn")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "isbn", source = "isbn")
    BookDTO toDtoBookIsbn(Book book);

    @Named("clientFirstName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    ClientDTO toDtoClientFirstName(Client client);
}
