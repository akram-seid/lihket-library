package com.example.application.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Book extends AbstractEntity {
    private String code;
    private String genera;
    private String author;
    private String title;
    private String year;
    private int quantity;


}
