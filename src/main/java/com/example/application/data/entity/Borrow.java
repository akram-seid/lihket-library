package com.example.application.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Borrow extends AbstractEntity {

    @ManyToOne
    private Customer customer;
    @ManyToOne
    private Book book;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean isReturned;

}
