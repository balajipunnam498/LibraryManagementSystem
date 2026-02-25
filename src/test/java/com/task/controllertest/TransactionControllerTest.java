package com.task.controllertest;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.task.controller.TransactionController;
import com.task.exceptions.BookAlreadyIssuedException;
import com.task.exceptions.BookNotFoundException;
import com.task.exceptions.MaxNumOfIssuedBooksExceed;
import com.task.exceptions.MemberNotFoundException;
import com.task.exceptions.TransactionNotFoundException;
import com.task.model.Transaction;
import com.task.security.UserdetailsService;
import com.task.serviceimpl.TransactionServiceImpl;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

	@MockitoBean
	private TransactionServiceImpl transactionServiceImpl;

	@MockitoBean
	private UserdetailsService userDetailService;

	@Autowired
	private MockMvc mockMvc;

	private Transaction testTransaction;

	@BeforeEach
	void setDefaults() {
		testTransaction = new Transaction();
		testTransaction.setTransactionId(1L);
		testTransaction.setMember(null);
		testTransaction.setBook(null);
	}

	@Test
	@WithMockUser
	public void deleteTransaction_whenFound_status200() throws Exception {

		Mockito.when(transactionServiceImpl.deleteTransaction(1L)).thenReturn("Transaction Succesfully Deleted");

		mockMvc.perform(delete("/transaction/deletetransaction/1").with(csrf())).andExpect(status().isOk())
				.andExpect(content().string("Transaction Succesfully Deleted"));

		verify(transactionServiceImpl, times(1)).deleteTransaction(1L);
	}

	@Test
	@WithMockUser
	public void deleteTransaction_whenNotFound_status404() throws Exception {

		Mockito.when(transactionServiceImpl.deleteTransaction(anyLong()))
				.thenThrow(new TransactionNotFoundException("Transaction Not Found"));

		mockMvc.perform(delete("/transaction/deletetransaction/99").with(csrf())).andExpect(status().isNotFound());

		verify(transactionServiceImpl, times(1)).deleteTransaction(anyLong());
	}

	@Test
	@WithMockUser
	public void retriveTransaction_whenFound_status200() throws Exception {

		Mockito.when(transactionServiceImpl.retriveTransaction(1L)).thenReturn(testTransaction);

		mockMvc.perform(get("/transaction/retrivetransaction/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.transactionId", is(1)));

		verify(transactionServiceImpl, times(1)).retriveTransaction(1L);
	}

	@Test
	@WithMockUser
	public void retriveTransaction_whenNotFound_status404() throws Exception {

		Mockito.when(transactionServiceImpl.retriveTransaction(anyLong()))
				.thenThrow(new TransactionNotFoundException("Transaction Not Found"));

		mockMvc.perform(get("/transaction/retrivetransaction/99").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(transactionServiceImpl, times(1)).retriveTransaction(anyLong());
	}

	@Test
	@WithMockUser
	public void createTransaction_whenBothFound_status200() throws Exception {

		Mockito.when(transactionServiceImpl.createTransaction(10L, 5L)).thenReturn(testTransaction);

		mockMvc.perform(
				post("/transaction/createtransaction/10/5").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.transactionId", is(1)));

		verify(transactionServiceImpl, times(1)).createTransaction(10L, 5L);
	}

	@Test
	@WithMockUser
	public void createTransaction_whenMemberNotFound_status404() throws Exception {

		Mockito.when(transactionServiceImpl.createTransaction(anyLong(), anyLong()))
				.thenThrow(new MemberNotFoundException("Member Not Found Of Id:99"));

		mockMvc.perform(
				post("/transaction/createtransaction/99/5").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(transactionServiceImpl, times(1)).createTransaction(anyLong(), anyLong());
	}

	@Test
	@WithMockUser
	public void createTransaction_whenBookNotFound_status404() throws Exception {

		Mockito.when(transactionServiceImpl.createTransaction(anyLong(), anyLong()))
				.thenThrow(new BookNotFoundException("Book Not Found With Id:99"));

		mockMvc.perform(
				post("/transaction/createtransaction/10/99").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(transactionServiceImpl, times(1)).createTransaction(anyLong(), anyLong());
	}

	@Test
	@WithMockUser
	public void createTransaction_whenBookAlreadyIssued_status409() throws Exception {

		Mockito.when(transactionServiceImpl.createTransaction(anyLong(), anyLong()))
				.thenThrow(new BookAlreadyIssuedException("Book is already issued"));

		mockMvc.perform(
				post("/transaction/createtransaction/10/5").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict()); // 409

		verify(transactionServiceImpl, times(1)).createTransaction(anyLong(), anyLong());
	}

	@Test
	@WithMockUser
	public void createTransaction_whenMemberMaxLimitReached_status409() throws Exception {

		Mockito.when(transactionServiceImpl.createTransaction(anyLong(), anyLong()))
				.thenThrow(new MaxNumOfIssuedBooksExceed("Member reached max book limit"));

		mockMvc.perform(
				post("/transaction/createtransaction/10/5").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict()); // 409

		verify(transactionServiceImpl, times(1)).createTransaction(anyLong(), anyLong());
	}
}