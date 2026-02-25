package com.task.controllertest;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.task.controller.MemberController;
import com.task.exceptions.MemberNotFoundException;
import com.task.model.Bill;
import com.task.model.Member;
import com.task.security.UserdetailsService;
import com.task.serviceimpl.MemberServiceImpl;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

	@MockitoBean
	private MemberServiceImpl memberServiceImpl;

	@MockitoBean
	private UserdetailsService userDetailService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Member testMember;
	private Bill testBill;

	@BeforeEach
	void setDefaults() {
		testMember = new Member();
		testMember.setMemberId(1L);
		testMember.setName("John Doe");
		testMember.setNoOfBooksIssued(2);

		testBill = new Bill();
		testBill.setBillID(101L);
		testBill.setAmount(150.00);
	}

	@Test
	@WithMockUser
	public void retriveMember_whenMemberFound_status200() throws Exception {

		Mockito.when(memberServiceImpl.retriveMember(1L)).thenReturn(testMember);

		mockMvc.perform(get("/member/findbymemberid/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.MemberId", is(1)))
				.andExpect(jsonPath("$.name", is("John Doe")));

		verify(memberServiceImpl, times(1)).retriveMember(1L);
	}

	@Test
	@WithMockUser
	public void retriveMember_whenMemberNotFound_status404() throws Exception {

		Mockito.when(memberServiceImpl.retriveMember(anyLong()))
				.thenThrow(new MemberNotFoundException("Member Not Found"));

		mockMvc.perform(get("/member/findbymemberid/99").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(memberServiceImpl, times(1)).retriveMember(99L);
	}

	@Test
	@WithMockUser
	public void increaseBookIssued_whenMemberFound_status200() throws Exception {

		testMember.setNoOfBooksIssued(4);
		Mockito.when(memberServiceImpl.increaseBookIssued(1L, 2)).thenReturn(testMember);

		mockMvc.perform(put("/member/increasebooksissued/1").with(csrf()).param("numOfBooks", "2")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.noOfBooksIssued", is(4)));

		verify(memberServiceImpl, times(1)).increaseBookIssued(1L, 2);
	}

	@Test
	@WithMockUser
	public void increaseBookIssued_whenMemberNotFound_status404() throws Exception {

		Mockito.when(memberServiceImpl.increaseBookIssued(anyLong(), anyInt()))
				.thenThrow(new MemberNotFoundException("Member Not Found"));

		mockMvc.perform(put("/member/increasebooksissued/99").with(csrf()).param("numOfBooks", "2")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

		verify(memberServiceImpl, times(1)).increaseBookIssued(anyLong(), anyInt());
	}

	@Test
	@WithMockUser
	public void decreaseBookIssued_whenMemberFound_status200() throws Exception {

		testMember.setNoOfBooksIssued(1);
		Mockito.when(memberServiceImpl.decreaseBookIssued(1L, 1)).thenReturn(testMember);

		mockMvc.perform(put("/member/decreasebooksissued/1").with(csrf()).param("numOfBooks", "1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.noOfBooksIssued", is(1)));

		verify(memberServiceImpl, times(1)).decreaseBookIssued(1L, 1);
	}

	@Test
	@WithMockUser
	public void decreaseBookIssued_whenMemberNotFound_status404() throws Exception {

		Mockito.when(memberServiceImpl.decreaseBookIssued(anyLong(), anyInt()))
				.thenThrow(new MemberNotFoundException("Member Not Found"));

		mockMvc.perform(put("/member/decreasebooksissued/99").with(csrf()).param("numOfBooks", "1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

		verify(memberServiceImpl, times(1)).decreaseBookIssued(anyLong(), anyInt());
	}

	@Test
	@WithMockUser
	public void payBill_whenBillFound_status200() throws Exception {

		Mockito.when(memberServiceImpl.payBill(150.00, 101L)).thenReturn("Bill Paid Successfully");

		mockMvc.perform(post("/member/paybill").with(csrf()).param("amount", "150.00")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testBill)))
				.andExpect(status().isOk());

		verify(memberServiceImpl, times(1)).payBill(150.00, 101L);
	}

	@Test
	@WithMockUser
	public void payBill_whenBillNotFound_status404() throws Exception {

		Mockito.when(memberServiceImpl.payBill(anyDouble(), anyLong()))
				.thenThrow(new MemberNotFoundException("Bill Not Found"));

		mockMvc.perform(post("/member/paybill").with(csrf()).param("amount", "150.00")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testBill)))
				.andExpect(status().isNotFound());

		verify(memberServiceImpl, times(1)).payBill(anyDouble(), anyLong());
	}
}