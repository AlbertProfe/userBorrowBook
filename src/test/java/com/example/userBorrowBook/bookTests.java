package com.example.userBorrowBook;

import com.example.userBorrowBook.model.Book;
import com.example.userBorrowBook.model.Borrow;
import com.example.userBorrowBook.model.UserApp;
import com.example.userBorrowBook.repository.BookRepository;
import com.example.userBorrowBook.repository.BorrowRepository;
import com.example.userBorrowBook.repository.UserAppRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class bookTests {

	@Autowired
	BookRepository bookRepository;

	private static final Logger logger = LoggerFactory.getLogger(bookTests.class);

	@Test
	void contextLoads() {
	}

	@Test
	void testFindByAuthor() {
		logger.info("Starting testFindByAuthor");

		// Given
		String author = "J.R.R. Tolkien";

		// When
		List<Book> foundBooks = bookRepository.findByAuthor(author);

		// Then
		assertNotNull(foundBooks, "Found books list should not be null");
		assertEquals(2, foundBooks.size(), "Should find 2 books by J.R.R. Tolkien");
        logger.info("Found books: {}", foundBooks.size());

		// Verify the first book
		Book firstBook = foundBooks.get(0);
		assertEquals("The Lord of the Rings", firstBook.getTitle());
		assertEquals("9780618640157", firstBook.getIsbn());
		assertEquals(1178, firstBook.getPagesQty());
		assertEquals(LocalDate.of(1954, 7, 29), firstBook.getPublicationDate());
		assertTrue(firstBook.isAvailable());
		logger.info("First book: {}", firstBook);

		// Verify the second book
		Book secondBook = foundBooks.get(1);
		assertEquals("The Hobbit", secondBook.getTitle());
		assertEquals("9780547928227", secondBook.getIsbn());
		assertEquals(366, secondBook.getPagesQty());
		assertEquals(LocalDate.of(1937, 9, 21), secondBook.getPublicationDate());
		assertTrue(secondBook.isAvailable());
		logger.info("Second book: {}", secondBook);

		logger.info("testFindByAuthor completed successfully");
	}

	@Test
	void testFindByAuthorNoResults() {
		logger.info("Starting testFindByAuthorNoResults");

		// Given
		String author = "Pink Floyd";

		// When
		List<Book> foundBooks = bookRepository.findByAuthor(author);

		// Then
		assertNotNull(foundBooks, "Found books list should not be null");
		assertTrue(foundBooks.isEmpty(), "Should find no books by Pink Floyd");
		assertEquals(0, foundBooks.size(), "Should find 0 books by Pink Floyd");

		logger.info("Found books: {}", foundBooks.size());
		logger.info("testFindByAuthorNoResults completed successfully");
	}

	@Test
	void testFindByTitleContaining() {
		logger.info("Starting testFindByTitleContaining");

		// Given
		String titleKeyword = "Girl";

		// When
		List<Book> foundBooks = bookRepository.findByTitleContaining(titleKeyword);

		// Then
		assertNotNull(foundBooks, "Found books list should not be null");
		assertEquals(2, foundBooks.size(), "Should find 2 books containing 'Girl' in the title");
		logger.info("Found books: {}", foundBooks.size());

		// Verify the first book (The Diary of a Young Girl)
		Book firstBook = foundBooks.stream()
				.filter(book -> book.getId().equals("B011"))
				.findFirst()
				.orElse(null);
		assertNotNull(firstBook, "Should find 'The Diary of a Young Girl'");
		assertEquals("The Diary of a Young Girl", firstBook.getTitle());
		assertEquals("Anne Frank", firstBook.getAuthor());
		assertEquals("9780553296983", firstBook.getIsbn());
		assertEquals(283, firstBook.getPagesQty());
		assertEquals(LocalDate.of(1947, 6, 25), firstBook.getPublicationDate());
		assertTrue(firstBook.isAvailable());
		logger.info("First book: {}", firstBook);

		// Verify the second book (The Girl with the Dragon Tattoo)
		Book secondBook = foundBooks.stream()
				.filter(book -> book.getId().equals("B017"))
				.findFirst()
				.orElse(null);
		assertNotNull(secondBook, "Should find 'The Girl with the Dragon Tattoo'");
		assertEquals("The Girl with the Dragon Tattoo", secondBook.getTitle());
		assertEquals("Stieg Larsson", secondBook.getAuthor());
		assertEquals("9780307454546", secondBook.getIsbn());
		assertEquals(672, secondBook.getPagesQty());
		assertEquals(LocalDate.of(2005, 8, 1), secondBook.getPublicationDate());
		assertTrue(secondBook.isAvailable());
		logger.info("Second book: {}", secondBook);

		logger.info("testFindByTitleContaining completed successfully");
	}

	@Test
	void testFindByTitleContainingAndAvailable() {
		logger.info("Starting testFindByTitleContainingAndAvailable");

		// Given
		String keyword = "Girl";
		boolean available = true;

		// When
		List<Book> foundBooks = bookRepository.findByTitleContainingAndAvailable(keyword, available);
		// Then
		assertNotNull(foundBooks, "Found books list should not be null");
		assertEquals(2, foundBooks.size(), "Should find 2 available books containing 'Girl'");
		logger.info("Found available books with 'Girl': {}", foundBooks.size());

		// Verify first book (The Diary of a Young Girl - B011)
		Book firstBook = foundBooks.stream()
				.filter(b -> b.getId().equals("B011"))
				.findFirst()
				.orElse(null);

		assertNotNull(firstBook, "B011 should be present");
		assertEquals("The Diary of a Young Girl", firstBook.getTitle());
		assertEquals("Anne Frank", firstBook.getAuthor());
		assertEquals(283, firstBook.getPagesQty());
		assertEquals(LocalDate.of(1947, 6, 25), firstBook.getPublicationDate());
		assertTrue(firstBook.isAvailable(), "B011 should be available");
		logger.info("First book details: {}", firstBook);

		// Verify second book (The Girl with the Dragon Tattoo - B017)
		Book secondBook = foundBooks.stream()
				.filter(b -> b.getId().equals("B017"))
				.findFirst()
				.orElse(null);

		assertNotNull(secondBook, "B017 should be present");
		assertEquals("The Girl with the Dragon Tattoo", secondBook.getTitle());
		assertEquals("Stieg Larsson", secondBook.getAuthor());
		assertEquals(672, secondBook.getPagesQty());
		assertEquals(LocalDate.of(2005, 8, 1), secondBook.getPublicationDate());
		assertTrue(secondBook.isAvailable(), "B017 should be available");
		logger.info("Second book details: {}", secondBook);

		logger.info("testFindByTitleContainingAndAvailable completed successfully");
	}

	@Test
	void testFindByPublicationDateBeforeAndAvailable() {
		logger.info("Starting testFindByPublicationDateBeforeAndAvailable");

		// Given
		LocalDate cutoffDate = LocalDate.of(2000, 1, 1);
		boolean available = true;

		// When
		List<Book> foundBooks = bookRepository.findByPublicationDateBeforeAndAvailable(cutoffDate, available);

		// Then
		assertNotNull(foundBooks, "Found books list should not be null");
		logger.info("Found {} books published before 2000 and available", foundBooks.size());
		assertEquals(14, foundBooks.size(), "Should find 14 books published before 2000 and available");

		// Verify specific books are present
		List<String> expectedBookIds = Arrays.asList("B001", "B002", "B003", "B004", "B005", "B006", "B007", "B008", "B009", "B010", "B011", "B012", "B013", "B014");

		List<String> actualBookIds = foundBooks.stream()
				.map(Book::getId)
				.collect(Collectors.toList());

		assertTrue(actualBookIds.containsAll(expectedBookIds), "All expected books should be in the results");
		logger.info("Verified all expected books are in the results");

		// Optional: You can add more detailed checks for individual books if needed
		Book prideAndPrejudice = foundBooks.stream()
				.filter(book -> book.getId().equals("B003"))
				.findFirst().orElse(null);
		assertNotNull(prideAndPrejudice, "Pride and Prejudice should be found");
		assertEquals("Pride and Prejudice", prideAndPrejudice.getTitle());
		assertEquals(LocalDate.of(1813, 1, 28), prideAndPrejudice.getPublicationDate());
		assertTrue(prideAndPrejudice.isAvailable());

		logger.info("testFindByPublicationDateBeforeAndAvailable completed successfully");
	}





}
