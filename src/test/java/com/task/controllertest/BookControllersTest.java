package com.task.controllertest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.task.controller.BookController;
import com.task.dao.LibrarianRepo;
import com.task.exceptions.BookNotFoundException;
import com.task.exceptions.MemberNotFoundException;
import com.task.model.Book;
import com.task.model.BookType;
import com.task.security.JwtService;
import com.task.security.UserdetailsService;
import com.task.serviceimpl.BookServiceIMPL;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(BookController.class)
public class BookControllersTest {

	@MockitoBean
	private BookServiceIMPL bookService;

	@Autowired
	private MockMvc mockmvc;

	private Book testBook;

	@MockitoBean
	private JwtService jwtService;

	@MockitoBean
	private LibrarianRepo librarian;

	@MockitoBean
	private UserdetailsService userDetailService;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void SetDefaults() {
		testBook = new Book("Martin Fowler", "Refactoring", BookType.STUDY_BOOK, 599.99, "D-404", "AVAILABLE",
				"Second Edition", LocalDate.of(2022, 6, 20));
		testBook.setBookID(1L);
	}

	@Test
	@WithMockUser
	public void detailsOfBookById_when_bookFound_status200() throws Exception {

		Mockito.when(bookService.displayBookDetails(1L)).thenReturn(testBook);

		mockmvc.perform(get("/book/findbookbyid/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.bookID", is(1)));
		verify(bookService, times(1)).displayBookDetails(1L);
	}

	@Test
	@WithMockUser
	public void detailsOfBookById_when_bookNotFond_status404() throws Exception {

		Mockito.when(bookService.displayBookDetails(5L)).thenThrow(new BookNotFoundException("Not Found"));

		mockmvc.perform(get("/book/findbookbyid/5").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(bookService, times(1)).displayBookDetails(5L);
	}

	@Test
	@WithMockUser
	public void updateBookStatus_when_BookFound_status200() throws Exception {
		testBook.setStatus("ISSUED");
		Mockito.when(bookService.updateBookStatus(1L, "ISSUED")).thenReturn(testBook);

		mockmvc.perform(put("/book/updatebookstatusbyid").contentType(MediaType.APPLICATION_JSON).with(csrf())
				.content(objectMapper.writeValueAsString(testBook))).andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("ISSUED")));

		verify(bookService, times(1)).updateBookStatus(1L, "ISSUED");

	}

	@Test
	@WithMockUser
	public void updateBookStatus_when_BookNotFound_status404() throws Exception {
		testBook.setStatus("ISSUED");
		Mockito.when(bookService.updateBookStatus(anyLong(), anyString()))
				.thenThrow(new BookNotFoundException("Not Found"));

		mockmvc.perform(put("/book/updatebookstatusbyid").contentType(MediaType.APPLICATION_JSON).with(csrf())
				.content(objectMapper.writeValueAsString(testBook))).andExpect(status().isNotFound());

		verify(bookService, times(1)).updateBookStatus(anyLong(), anyString());

	}

	@Test
	@WithMockUser
	public void findAllbooks_WhenHaveBooks() throws Exception {
		Book book2 = new Book("Joshua Bloch", "Effective Java", BookType.STUDY_BOOK, 799.99, "E-505", "AVAILABLE",
				"Third Edition", LocalDate.of(2021, 8, 10));
		book2.setBookID(2L);
		List<Book> list = Arrays.asList(testBook, book2);
		Mockito.when(bookService.getAllBooks()).thenReturn(list);

		mockmvc.perform(get("/book/getallbooks").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].bookName", is("Refactoring")));
		verify(bookService, times(1)).getAllBooks();
	}

	@Test
	@WithMockUser
	void getAllBooks_WhenNoBooks_ShouldReturn200AndEmptyList() throws Exception {
		Mockito.when(bookService.getAllBooks()).thenReturn(Arrays.asList());

		mockmvc.perform(get("/book/getallbooks")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));

		verify(bookService, times(1)).getAllBooks();
	}

	@Test
	@WithMockUser
	public void findBookByName_WhenFound_status200() throws Exception {
		Mockito.when(bookService.findByName("Refactoring")).thenReturn(testBook);

		mockmvc.perform(post("/book/searchbyname").with(csrf()).param("bookname", "Refactoring"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.bookName", is("Refactoring")));

		verify(bookService, times(1)).findByName("Refactoring");
	}

	@Test
	@WithMockUser
	public void findBookByName_WhenNotFound_status404() throws Exception {

		Mockito.when(bookService.findByName("Refactoring")).thenThrow(new MemberNotFoundException("Member Not Found"));

		mockmvc.perform(post("/book/searchbyname").with(csrf()).param("bookname", "Refactoring"))
				.andExpect(status().isNotFound());

		verify(bookService, times(1)).findByName("Refactoring");
	}

	@Test
	@WithMockUser
	public void findbyBookType_WhenTypeMatches_status200() throws Exception {

		List<Book> list = Arrays.asList(testBook);
		Mockito.when(bookService.findByType(BookType.STUDY_BOOK)).thenReturn(list);

		mockmvc.perform(post("/book/searchbytype").with(csrf()).param("book", "STUDY_BOOK")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	@WithMockUser
	public void findByType_whenTypeNotMatches_EmptyList() throws Exception {

		Mockito.when(bookService.findByType(BookType.MAGZINES)).thenReturn(Arrays.asList());
		mockmvc.perform(post("/book/searchbytype").with(csrf()).param("book", "STUDY_BOOK")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}
}
