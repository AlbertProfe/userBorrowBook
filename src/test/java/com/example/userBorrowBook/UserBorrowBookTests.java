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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserBorrowBookTests {

	@Autowired
	BookRepository bookRepository;
	@Autowired
	BorrowRepository borrowRepository;
	@Autowired
	UserAppRepository userAppRepository;

	private static final Logger logger = LoggerFactory.getLogger(UserBorrowBookTests.class);

	@Test
	void contextLoads() {
		// Verify that the application context loads successfully
		assertNotNull(bookRepository);
		assertNotNull(userAppRepository);
		assertNotNull(borrowRepository);

		// Test book repository
		assertEquals(20, bookRepository.count());
		Book book = bookRepository.findById("B001").orElse(null);
		assertNotNull(book);
		assertEquals("To Kill a Mockingbird", book.getTitle());

		// Test user repository
		assertEquals(20, userAppRepository.count());
		UserApp user = userAppRepository.findById("U001").orElse(null);
		assertNotNull(user);
		assertEquals("John Doe", user.getUserAppName());

		// Test borrow repository
		assertEquals(36, borrowRepository.count());
		Borrow borrow = borrowRepository.findById("BR001").orElse(null);
		assertNotNull(borrow);
		assertEquals(LocalDate.of(2025, 2, 15), borrow.getBorrowDate());
	}

	@Test
	void testCreateBorrow() {
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
	void testCreateAndSaveBorrow() {
		logger.info("Starting testCreateAndSaveBorrow");

		// Given
		Borrow borrow = new Borrow();
		borrow.setBorrowDate(LocalDate.now());
		borrow.setReturnDate(LocalDate.now().plusDays(14)); // Assuming a 2-week borrow period
		borrow.setPoints(0);

		String userId = "U001";
		String bookId = "B001";

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

	@Test
	void testFindByBookAndIsReturned() {
		logger.info("Starting testFindByBookAndIsReturned");

		// Given
		String bookTitle = "Moby-Dick";
		Book mobyDick = bookRepository.findByTitleContaining(bookTitle).get(0); // Assuming only one "Moby-Dick" book
		assertNotNull(mobyDick, "Moby-Dick book should exist");

		// Find the returned borrows
		boolean isReturned = true;
		List<Borrow> returnedBorrows = borrowRepository.findByBookAndIsReturned(mobyDick, isReturned);
		assertNotNull(returnedBorrows, "Returned borrows list should not be null");
		assertEquals(1, returnedBorrows.size(), "Should find 1 returned borrow for Moby-Dick");
		logger.info("Found {} returned borrows for Moby-Dick", returnedBorrows.size());

		// Validate the returned borrow
		Borrow returnedBorrow = returnedBorrows.get(0);
		assertEquals("BR007", returnedBorrow.getId(), "Returned borrow ID should be BR007");
		assertTrue(returnedBorrow.isReturned(), "Borrow BR007 should be returned");
		logger.info("Verified returned borrow details: {}", returnedBorrow);

		// Find the non-returned borrows
		isReturned = false;
		List<Borrow> nonReturnedBorrows = borrowRepository.findByBookAndIsReturned(mobyDick, isReturned);
		assertNotNull(nonReturnedBorrows, "Non-returned borrows list should not be null");
		assertEquals(1, nonReturnedBorrows.size(), "Should find 1 non-returned borrow for Moby-Dick");
		logger.info("Found {} non-returned borrows for Moby-Dick", nonReturnedBorrows.size());

		// Validate the non-returned borrow
		Borrow nonReturnedBorrow = nonReturnedBorrows.get(0);
		assertEquals("BR029", nonReturnedBorrow.getId(), "Non-returned borrow ID should be BR029");
		assertFalse(nonReturnedBorrow.isReturned(), "Borrow BR029 should not be returned");
		logger.info("Verified non-returned borrow details: {}", nonReturnedBorrow);

		logger.info("testFindByBookAndIsReturned completed successfully");
	}

	@Test
	void testFindByUserAndIsReturned() {
		logger.info("Starting testFindByUserAndIsReturned");

		// Given
		String userId = "U001";
		UserApp user = userAppRepository.findById(userId).orElse(null);
		assertNotNull(user, "User with ID U001 should exist");

		// Find returned borrows for user U001
		boolean isReturned = true;
		List<Borrow> returnedBorrows = borrowRepository.findByUserAndIsReturned(user, isReturned);
		assertNotNull(returnedBorrows, "Returned borrows list should not be null");
		assertEquals(2, returnedBorrows.size(), "Should find 2 returned borrows for user U001");
		logger.info("Found {} returned borrows for user U001", returnedBorrows.size());

		// Verify the returned borrows
		List<String> expectedReturnedBorrowIds = Arrays.asList("BR001", "BR022");
		List<String> actualReturnedBorrowIds = returnedBorrows.stream()
				.map(Borrow::getId)
				.collect(Collectors.toList());
		assertTrue(actualReturnedBorrowIds.containsAll(expectedReturnedBorrowIds), "Returned borrows should include BR001 and BR022");

		// Find non-returned borrows for user U001
		isReturned = false;
		List<Borrow> nonReturnedBorrows = borrowRepository.findByUserAndIsReturned(user, isReturned);
		assertNotNull(nonReturnedBorrows, "Non-returned borrows list should not be null");
		assertEquals(2, nonReturnedBorrows.size(), "Should find 2 non-returned borrows for user U001");
		logger.info("Found {} non-returned borrows for user U001", nonReturnedBorrows.size());

		// Verify the non-returned borrows
		List<String> expectedNonReturnedBorrowIds = Arrays.asList("BR021", "BR023");
		List<String> actualNonReturnedBorrowIds = nonReturnedBorrows.stream()
				.map(Borrow::getId)
				.collect(Collectors.toList());
		assertTrue(actualNonReturnedBorrowIds.containsAll(expectedNonReturnedBorrowIds), "Non-returned borrows should include BR021 and BR023");

		logger.info("testFindByUserAndIsReturned completed successfully");
	}



}
