package com.task.servicetest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.task.dao.BookRepo;
import com.task.exceptions.BookNotFoundException;
import com.task.model.Book;
import com.task.model.BookType;
import com.task.serviceimpl.BookServiceIMPL;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

	@Mock
	public BookRepo bookRepo;

	@InjectMocks
	public BookServiceIMPL bookService;

	public Book book;

	@BeforeEach
	void setUp() {
		book = new Book("J.K. Rowling", "Harry Potter", BookType.STUDY_BOOK, 299.99, "A-101", "AVAILABLE",
				"First Edition", LocalDate.of(2020, 1, 1));
		book.setBookID(1L);
	}

	@Test
	public void displayBookDetails_WhenBookExists_ShouldReturnBook() {

		Mockito.when(bookRepo.findById(1L)).thenReturn(Optional.of(book));

		Book details = bookService.displayBookDetails(1L);

		assertThat(details).isNotNull();
		assertThat(details.getBookID()).isEqualTo(1L);
		assertThat(details.getBookName()).isEqualTo(book.getBookName());
		verify(bookRepo, times(1)).findById(1l);
	}

	@Test
	public void displayBookDetails_WhenBookNotFound_ShouldThrowException() {

		Mockito.when(bookRepo.findById(5L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookService.displayBookDetails(5L)).isInstanceOf(BookNotFoundException.class)
				.hasMessageContaining("Book Not Found");
		verify(bookRepo, times(1)).findById(5L);
	}

	@Test
	public void updateBookStatus_WhenValidStatus_ShouldUpdateAndReturnBook() {

		Mockito.when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
		Mockito.when(bookRepo.save(ArgumentMatchers.any(Book.class))).thenReturn(book);
		Book result = bookService.updateBookStatus(1L, "ISSUED");

		assertThat(result.getStatus()).isEqualTo("ISSUED");

		verify(bookRepo, times(1)).save(any());
	}

	@Test
	public void updateBookStatus_WhenBookNotFound_ShouldThrowException() {

		Mockito.when(bookRepo.findById(5L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookService.displayBookDetails(5L)).isInstanceOf(BookNotFoundException.class)
				.hasMessageContaining("Book Not Found");
		verify(bookRepo, times(1)).findById(5L);
	}

	@Test
	public void getAllBooks_WhenDB_has_SomeBooks() {
		Book book2 = new Book("George Orwell", "1984", BookType.STUDY_BOOK, 199.99, "B-202", "AVAILABLE",
				"Second Edition", LocalDate.of(2019, 5, 15));
		book2.setBookID(2L);
		List<Book> list = Arrays.asList(book, book2);
		Mockito.when(bookRepo.findAll()).thenReturn(list);
		List<Book> books = bookService.getAllBooks();
		assertThat(books.size()).isEqualTo(2);
		verify(bookRepo, times(1)).findAll();
	}

	@Test
	public void getAllBooks_WhenDB_has_NoBooks() {

		Mockito.when(bookRepo.findAll()).thenReturn(Arrays.asList());
		List<Book> books = bookService.getAllBooks();
		assertThat(books.size()).isEqualTo(0);
		assertThat(books).isEmpty();
		verify(bookRepo, times(1)).findAll();
	}
	
	@Test
	public void findByName_When_Name_Found() {
		
		Mockito.when(bookRepo.findByBookName("Harry Potter")).thenReturn(book);
		
		Book name = bookService.findByName("Harry Potter");
		
		assertThat(name.getBookID()).isEqualTo(1L);
		assertThat(name.getBookName()).isEqualTo("Harry Potter");
		verify(bookRepo,times(1)).findByBookName("Harry Potter");
	}
	
	@Test
	public void findByType_WhenBooksExist_ShouldReturnList() {

	    List<Book> books = List.of(book);

	    Mockito.when(bookRepo.findByType(BookType.STUDY_BOOK))
	            .thenReturn(books);

	    List<Book> result = bookService.findByType(BookType.STUDY_BOOK);

	    assertThat(result).isNotNull();
	    assertThat(result).hasSize(1);
	    assertThat(result.get(0).getBookName())
	            .isEqualTo(book.getBookName());

	    verify(bookRepo, times(1))
	            .findByType(BookType.STUDY_BOOK);
	}


}
