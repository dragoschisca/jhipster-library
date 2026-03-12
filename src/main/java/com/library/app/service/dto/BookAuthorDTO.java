package com.library.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.library.app.domain.BookAuthor} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookAuthorDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 13)
    private String bookIsbn;

    @NotNull
    private Integer authorId;

    private BookDTO book;

    private AuthorDTO author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    public AuthorDTO getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDTO author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookAuthorDTO)) {
            return false;
        }

        BookAuthorDTO bookAuthorDTO = (BookAuthorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookAuthorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookAuthorDTO{" +
            "id=" + getId() +
            ", bookIsbn='" + getBookIsbn() + "'" +
            ", authorId=" + getAuthorId() +
            ", book=" + getBook() +
            ", author=" + getAuthor() +
            "}";
    }
}
