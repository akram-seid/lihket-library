package com.example.application.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowDto {
    private String fullName;
    private String bookTitle;
    private Long id;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
