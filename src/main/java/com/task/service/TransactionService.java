package com.task.service;

import org.springframework.stereotype.Service;

import com.task.model.Transaction;

@Service
public interface TransactionService{

	Transaction createTransaction(long memberid, long bookid);
	
	Transaction retriveTransaction(long transactionId);
	
	String deleteTransaction(long transactionid);
}
