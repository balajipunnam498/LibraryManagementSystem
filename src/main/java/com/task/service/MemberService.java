package com.task.service;

import org.springframework.stereotype.Service;

import com.task.model.Member;

@Service
public interface MemberService {

	Member retriveMember(long memberid);
	
	Member increaseBookIssued(long memberid,int noOfBooks);
	
	Member decreaseBookIssued(long memberid,int noOfBooks);
	
	String payBill(double amount, long billId);
}
