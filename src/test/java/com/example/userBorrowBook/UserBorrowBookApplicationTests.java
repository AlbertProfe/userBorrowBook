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

@SpringBootTest
class UserBorrowBookApplicationTests {

	@Autowired
	BookRepository bookRepository;
	@Autowired
	BorrowRepository borrowRepository;
	@Autowired
	UserAppRepository userAppRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void createBorrowTest() {

		Borrow borrow = new Borrow();
		borrow.setBorrowDate(LocalDate.now());
		borrow.setReturnDate(LocalDate.now());
		borrow.setPoints(0);

		Optional<UserApp> userFound = userAppRepository.findById("U001");
		Optional<Book> bookFound = bookRepository.findById("B001");

		borrow.setUser(userFound.get());
		borrow.setBook(bookFound.get());

		borrowRepository.save(borrow);




	}
}
