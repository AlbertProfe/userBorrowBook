package com.example.userBorrowBook;

import com.example.userBorrowBook.model.Book;
import com.example.userBorrowBook.model.Borrow;
import com.example.userBorrowBook.model.UserApp;
import com.example.userBorrowBook.repository.BookRepository;
import com.example.userBorrowBook.repository.BorrowRepository;
import com.example.userBorrowBook.repository.UserAppRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserBorrowBookApplicationTests {

	@Autowired
	BookRepository bookRepository;
	@Autowired
	BorrowRepository borrowRepository;
	@Autowired
	UserAppRepository userAppRepository;

	private static final Logger logger = LoggerFactory.getLogger(UserBorrowBookApplicationTests.class);

	@Test
	void contextLoads() {
	}

	@Test
	void createBorrowTest() {
		// Given
		Borrow borrow = new Borrow();
		borrow.setBorrowDate(LocalDate.now());
		borrow.setReturnDate(LocalDate.now());
		borrow.setPoints(0);
		// When
		Optional<UserApp> userFound = userAppRepository.findById("U001");
		Optional<Book> bookFound = bookRepository.findById("B001");
		// Then
		borrow.setUser(userFound.get());
		borrow.setBook(bookFound.get());

		borrowRepository.save(borrow);

	}



	@Test
	void CreateAndSaveBorrowTest() {
		logger.info("Starting testCreateAndSaveBorrow");

		// Given
		Borrow borrow = new Borrow();
		borrow.setBorrowDate(LocalDate.now());
		borrow.setReturnDate(LocalDate.now().plusDays(14)); // Assuming a 2-week borrow period
		borrow.setPoints(0);

		String userId = "U001";
		String bookId = "B009";

		Optional<UserApp> userFound = userAppRepository.findById(userId);
		Optional<Book> bookFound = bookRepository.findById(bookId);

		assertTrue(userFound.isPresent(), "User with ID " + userId + " should exist");
		assertTrue(bookFound.isPresent(), "Book with ID " + bookId + " should exist");

		// Set user and book so we ASSIGN book and user to BORROW
		borrow.setUser(userFound.get());
		borrow.setBook(bookFound.get());

		// When
		Borrow savedBorrow = borrowRepository.save(borrow);
		logger.info("Borrow saved with ID: {}", savedBorrow.getId());

		// Then
		assertNotNull(savedBorrow.getId(), "Saved borrow should have an ID");
		assertEquals(userFound.get(), savedBorrow.getUser(), "Saved borrow should have the correct user");
		assertEquals(bookFound.get(), savedBorrow.getBook(), "Saved borrow should have the correct book");
		assertEquals(LocalDate.now(), savedBorrow.getBorrowDate(), "Borrow date should be today");
		assertEquals(LocalDate.now().plusDays(14), savedBorrow.getReturnDate(), "Return date should be 14 days from today");
		assertEquals(0, savedBorrow.getPoints(), "Initial points should be 0");

		logger.info("testCreateAndSaveBorrow completed successfully");
	}
}
