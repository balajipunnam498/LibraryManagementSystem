package com.task.controllertest;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.task.controller.BillController;
import com.task.exceptions.MemberNotFoundException;
import com.task.model.Bill;
import com.task.serviceimpl.BillServiceImpl;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(BillController.class)
public class BillControllerTest {

	@MockitoBean
	private BillServiceImpl billService;



	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Bill testBill;
	private List<Long> bookIds;

	@BeforeEach
	void setDefaults() {
		testBill = new Bill();
		testBill.setBillID(101L);
		testBill.setAmount(150.00);

		bookIds = Arrays.asList(1L, 2L, 3L);
	}

	@Test
	@WithMockUser
	public void createBill_whenMemberFound_status202() throws Exception {

		Mockito.when(billService.createBill(bookIds, 1L)).thenReturn(testBill);

		mockMvc.perform(post("/bill/createBill/1").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookIds))).andExpect(status().isAccepted())
				.andExpect(jsonPath("$.billID", is(101))).andExpect(jsonPath("$.amount", is(150.00)));

		verify(billService, times(1)).createBill(bookIds, 1L);
	}

	@Test
	@WithMockUser
	public void createBill_whenMemberNotFound_status404() throws Exception {

		Mockito.when(billService.createBill(any(), anyLong()))
				.thenThrow(new MemberNotFoundException("Member Not Found"));

		mockMvc.perform(post("/bill/createBill/99").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookIds))).andExpect(status().isNotFound());

		verify(billService, times(1)).createBill(any(), anyLong());
	}

	@Test
	@WithMockUser
	public void updateBill_whenBillFound_status200() throws Exception {

		testBill.setAmount(200.00);
		Mockito.when(billService.updateBill(101L, testBill)).thenReturn(testBill);

		mockMvc.perform(put("/bill/updatebill").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testBill))).andExpect(status().isOk())
				.andExpect(jsonPath("$.billID", is(101))).andExpect(jsonPath("$.amount", is(200.00)));

		verify(billService, times(1)).updateBill(101L, testBill);
	}

	@Test
	@WithMockUser
	public void updateBill_whenBillNotFound_status404() throws Exception {

		Mockito.when(billService.updateBill(anyLong(), any(Bill.class)))
				.thenThrow(new MemberNotFoundException("Bill Not Found"));

		mockMvc.perform(put("/bill/updatebill").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testBill))).andExpect(status().isNotFound());

		verify(billService, times(1)).updateBill(anyLong(), any(Bill.class));
	}
}