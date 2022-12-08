package com.example.application.data.service;

import com.example.application.data.entity.Borrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BorrowService {

    private final BorrowRepository repository;

    @Autowired
    public BorrowService(BorrowRepository repository) {
        this.repository = repository;
    }

    public Optional<Borrow> get(Long id) {
        return repository.findById(id);
    }

    public Borrow update(Borrow entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Borrow> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public List<Borrow> findAll() {
        return repository.findAll();
    }

    public List<Borrow> unReturned() {
        return repository.findBorrowsByReturnedIsFalse();
    }


}
