package com.task.controllertest;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.task.controller.LibrarianController;
import com.task.dto.CreateBillRequest;
import com.task.exceptions.BookAlreadyIssuedException;
import com.task.exceptions.BookNotFoundException;
import com.task.exceptions.MaxNumOfIssuedBooksExceed;
import com.task.exceptions.MemberNotFoundException;
import com.task.exceptions.TransactionNotFoundException;
import com.task.model.Bill;
import com.task.model.Book;
import com.task.model.BookType;
import com.task.model.Member;
import com.task.model.Transaction;
import com.task.serviceimpl.LibrarianServiceImpl;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(LibrarianController.class)
public class LibrarianControllerTest {

	@MockitoBean
	private LibrarianServiceImpl librarianService;



	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Book testBook;
	private Member testMember;
	private Transaction testTransaction;
	private Bill testBill;
	private List<Long> bookIds;

	@BeforeEach
	void setDefaults() {
		testBook = new Book("Martin Fowler", "Refactoring", BookType.STUDY_BOOK, 599.99, "D-404", "AVAILABLE",
				"Second Edition", LocalDate.of(2022, 6, 20));
		testBook.setBookID(1L);

		testMember = new Member();
		testMember.setMemberId(10L);
		testMember.setName("John Doe");
		testMember.setNoOfBooksIssued(1);
		testMember.setMaxBookLimit(5);

		testTransaction = new Transaction();
		testTransaction.setTransactionId(1L);
		testTransaction.setBook(testBook);
		testTransaction.setMember(testMember);
		testTransaction.setDateOfIssue(LocalDate.now());
		testTransaction.setDueDate(LocalDate.now().plusDays(10));

		testBill = new Bill();
		testBill.setBillID(101L);
		testBill.setAmount(150.00);

		bookIds = Arrays.asList(1L, 2L);
	}

	@Test
	@WithMockUser
	public void searchBook_whenFound_status200() throws Exception {

		Mockito.when(librarianService.searchBook(1L)).thenReturn(testBook);

		mockMvc.perform(get("/librarian/searchbook/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.bookID", is(1)))
				.andExpect(jsonPath("$.bookName", is("Refactoring")));

		verify(librarianService, times(1)).searchBook(1L);
	}

	@Test
	@WithMockUser
	public void searchBook_whenNotFound_status404() throws Exception {

		Mockito.when(librarianService.searchBook(anyLong())).thenThrow(new BookNotFoundException("Book Not Found"));

		mockMvc.perform(get("/librarian/searchbook/99").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(librarianService, times(1)).searchBook(anyLong());
	}

	@Test
	@WithMockUser
	public void verifyMember_whenFound_status200() throws Exception {

		Mockito.when(librarianService.verifyMember(10L)).thenReturn(testMember);

		mockMvc.perform(get("/librarian/verifymember/10").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.MemberId", is(10)));

		verify(librarianService, times(1)).verifyMember(10L);
	}

	@Test
	@WithMockUser
	public void verifyMember_whenNotFound_status404() throws Exception {

		Mockito.when(librarianService.verifyMember(anyLong()))
				.thenThrow(new MemberNotFoundException("Member Not Found"));

		mockMvc.perform(get("/librarian/verifymember/99").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(librarianService, times(1)).verifyMember(anyLong());
	}

	@Test
	@WithMockUser
	public void issueBook_whenBothFound_status200() throws Exception {

		Mockito.when(librarianService.issueBook(10L, 1L)).thenReturn(testTransaction);

		mockMvc.perform(post("/librarian/issuebook/10/1").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.transactionId", is(1)));

		verify(librarianService, times(1)).issueBook(10L, 1L);
	}

	@Test
	@WithMockUser
	public void issueBook_whenMemberNotFound_status404() throws Exception {

		Mockito.when(librarianService.issueBook(anyLong(), anyLong()))
				.thenThrow(new MemberNotFoundException("Member Not Found"));

		mockMvc.perform(post("/librarian/issuebook/99/1").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(librarianService, times(1)).issueBook(anyLong(), anyLong());
	}

	@Test
	@WithMockUser
	public void issueBook_whenBookNotFound_status404() throws Exception {

		Mockito.when(librarianService.issueBook(anyLong(), anyLong()))
				.thenThrow(new BookNotFoundException("Book Not Found"));

		mockMvc.perform(post("/librarian/issuebook/10/99").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(librarianService, times(1)).issueBook(anyLong(), anyLong());
	}

	@Test
	@WithMockUser
	public void issueBook_whenBookAlreadyIssued_status409() throws Exception {

		Mockito.when(librarianService.issueBook(anyLong(), anyLong()))
				.thenThrow(new BookAlreadyIssuedException("Book is already issued"));

		mockMvc.perform(post("/librarian/issuebook/10/1").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());

		verify(librarianService, times(1)).issueBook(anyLong(), anyLong());
	}

	@Test
	@WithMockUser
	public void issueBook_whenMemberMaxLimitReached_status409() throws Exception {

		Mockito.when(librarianService.issueBook(anyLong(), anyLong()))
				.thenThrow(new MaxNumOfIssuedBooksExceed("Member reached max book limit"));

		mockMvc.perform(post("/librarian/issuebook/10/1").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());

		verify(librarianService, times(1)).issueBook(anyLong(), anyLong());
	}

	@Test
	@WithMockUser
	public void calculateFine_whenOverdue_returnsFinAmount() throws Exception {

		// Book issued 20 days ago — 10 days overdue — fine = 20.0
		LocalDate overdueDate = LocalDate.now().minusDays(20);
		Mockito.when(librarianService.calculateFine(overdueDate)).thenReturn(20.0);

		mockMvc.perform(get("/librarian/calculatefine").param("date", overdueDate.toString()) // "2026-01-30"
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		verify(librarianService, times(1)).calculateFine(overdueDate);
	}

	@Test
	@WithMockUser
	public void calculateFine_whenNotOverdue_returnsZero() throws Exception {

		LocalDate recentDate = LocalDate.now();
		Mockito.when(librarianService.calculateFine(recentDate)).thenReturn(0.0);

		mockMvc.perform(get("/librarian/calculatefine").param("date", recentDate.toString())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		verify(librarianService, times(1)).calculateFine(recentDate);
	}

	@Test
	@WithMockUser
	public void createBill_whenMemberFound_status200() throws Exception {

		CreateBillRequest request = new CreateBillRequest();
		request.setBookIds(bookIds);

		Mockito.when(librarianService.createBill(bookIds, 10L)).thenReturn(testBill);

		mockMvc.perform(post("/librarian/createbill/10").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.billID", is(101)));

		verify(librarianService, times(1)).createBill(bookIds, 10L);
	}

	@Test
	@WithMockUser
	public void createBill_whenMemberNotFound_status404() throws Exception {

		CreateBillRequest request = new CreateBillRequest();
		request.setBookIds(bookIds);

		Mockito.when(librarianService.createBill(any(), anyLong()))
				.thenThrow(new MemberNotFoundException("Member Not Found"));

		mockMvc.perform(post("/librarian/createbill/99").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isNotFound());

		verify(librarianService, times(1)).createBill(any(), anyLong());
	}

	@Test
	@WithMockUser
	public void returnBook_whenTransactionFound_status200() throws Exception {

		Mockito.when(librarianService.returnBook(1L)).thenReturn(testBill);

		mockMvc.perform(delete("/librarian/returnbook/1").with(csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.billID", is(101)));

		verify(librarianService, times(1)).returnBook(1L);
	}

	@Test
	@WithMockUser
	public void returnBook_whenTransactionNotFound_status404() throws Exception {

		Mockito.when(librarianService.returnBook(anyLong()))
				.thenThrow(new TransactionNotFoundException("Transaction Not Found"));

		mockMvc.perform(delete("/librarian/returnbook/99").with(csrf())).andExpect(status().isNotFound());

		verify(librarianService, times(1)).returnBook(anyLong());
	}

	@Test
	@WithMockUser
	public void createMember_whenValid_status200() throws Exception {

		Mockito.when(librarianService.registerMember(any(Member.class))).thenReturn(testMember);

		mockMvc.perform(post("/librarian/createMember").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testMember))).andExpect(status().isOk())
				.andExpect(jsonPath("$.MemberId", is(10)));

		verify(librarianService, times(1)).registerMember(any(Member.class));
	}
}