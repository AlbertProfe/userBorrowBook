package com.example.userBorrowBook.repository;

import com.example.userBorrowBook.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    // Custom query methods
    List<Book> findByAuthor(String author);
    List<Book> findByTitleContaining(String titleKeyword);
    Book findByIsbn(String isbn);
}
