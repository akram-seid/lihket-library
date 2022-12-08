package com.example.application.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Book extends AbstractEntity {

    private String bookTitle;
    private String author;
    private String isbn;
    private LocalDate publicationDate;
    private String catalogId;
    private String genera;
    private int quantity;


}
