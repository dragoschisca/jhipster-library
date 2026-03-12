package com.library.app.service.mapper;

import com.library.app.domain.Book;
import com.library.app.domain.Publisher;
import com.library.app.service.dto.BookDTO;
import com.library.app.service.dto.PublisherDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "publisher", source = "publisher", qualifiedByName = "publisherName")
    BookDTO toDto(Book s);

    @Named("publisherName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PublisherDTO toDtoPublisherName(Publisher publisher);
}
