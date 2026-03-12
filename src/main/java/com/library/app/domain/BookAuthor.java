package com.library.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

@Entity
@Table(name = "book_author")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookAuthor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 13)
    @Column(name = "book_isbn", length = 13, nullable = false)
    private String bookIsbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "publisher" }, allowSetters = true)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    private Author author;

    public Long getId() {
        return this.id;
    }

    public BookAuthor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookIsbn() {
        return this.bookIsbn;
    }

    public BookAuthor bookIsbn(String bookIsbn) {
        this.setBookIsbn(bookIsbn);
        return this;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BookAuthor book(Book book) {
        this.setBook(book);
        return this;
    }

    public Author getAuthor() {
        return this.author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public BookAuthor author(Author author) {
        this.setAuthor(author);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookAuthor)) return false;
        return getId() != null && getId().equals(((BookAuthor) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "BookAuthor{" + "id=" + getId() + ", bookIsbn='" + getBookIsbn() + "'" + "}";
    }
}
