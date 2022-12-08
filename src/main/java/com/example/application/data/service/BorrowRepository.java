package com.example.application.data.service;

import com.example.application.data.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    @Query("select br from Borrow br where br.isReturned=false")
    List<Borrow> findBorrowsByReturnedIsFalse();

}