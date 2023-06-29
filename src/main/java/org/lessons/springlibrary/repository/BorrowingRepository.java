package org.lessons.springlibrary.repository;

import org.lessons.springlibrary.model.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRepository extends JpaRepository<Borrowing, Integer> {

}
