package com.example.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class Book extends AbstractEntity {

    private String bookTitle;
    private String author;
    private String isbn;
    private LocalDate publicationDate;
    private String catalogId;
    private String gener;

    public String getBookTitle() {
        return bookTitle;
    }
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
    public String getCatalogId() {
        return catalogId;
    }
    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }
    public String getGener() {
        return gener;
    }
    public void setGener(String gener) {
        this.gener = gener;
    }

}
