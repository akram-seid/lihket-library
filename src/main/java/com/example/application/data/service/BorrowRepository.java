package com.example.application.data.service;

import com.example.application.data.entity.Borrow;
import com.example.application.data.entity.BorrowDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    @Modifying
    @Query("update Borrow br set br.isReturned=true where br.id=:id ")
    void updateByID(Long id);

    @Query("select new com.example.application.data.entity.BorrowDto(c.fullName, b.title,br.id, br.borrowDate,br.returnDate)" +
            " from Borrow br join Customer c on c.id = br.customer.id join Book b on b.id = br.book.id where br.isReturned=true")
    List<BorrowDto> findBorrowsByReturnedIsTrue();

    @Query("select new com.example.application.data.entity.BorrowDto(c.fullName, b.title,br.id, br.borrowDate,br.returnDate)" +
            " from Borrow br join Customer c on c.id = br.customer.id join Book b on b.id = br.book.id where br.isReturned=false")
    List<BorrowDto> findBorrowsByReturnedIsFalse();


}