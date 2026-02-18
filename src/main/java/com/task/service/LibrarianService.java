package com.task.service;

import java.time.LocalDate;
import java.util.List;

import com.task.model.Bill;
import com.task.model.Book;
import com.task.model.Member;
import com.task.model.Transaction;

public interface LibrarianService {

	
	Book searchBook(long bookid);
	
	Member verifyMember(long memberid);
	
	Transaction issueBook(long memberid, long bookid);
	
	double calculateFine(LocalDate issuedDate);
	
	Bill createBill(List<Long> bookid,long memberid);
	
	Bill returnBook(long transactionId);
	
	Member registerMember(Member member);
}
	