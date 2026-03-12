package com.library.app.service.mapper;

import com.library.app.domain.Author;
import com.library.app.domain.Book;
import com.library.app.domain.BookAuthor;
import com.library.app.service.dto.AuthorDTO;
import com.library.app.service.dto.BookAuthorDTO;
import com.library.app.service.dto.BookDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookAuthor} and its DTO {@link BookAuthorDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookAuthorMapper extends EntityMapper<BookAuthorDTO, BookAuthor> {
    @Mapping(target = "book", source = "book", qualifiedByName = "bookIsbn")
    @Mapping(target = "author", source = "author", qualifiedByName = "authorFirstName")
    BookAuthorDTO toDto(BookAuthor s);

    @Named("bookIsbn")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "isbn", source = "isbn")
    BookDTO toDtoBookIsbn(Book book);

    @Named("authorFirstName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    AuthorDTO toDtoAuthorFirstName(Author author);
}
