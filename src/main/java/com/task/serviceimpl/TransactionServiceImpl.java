package com.task.serviceimpl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.dao.BookRepo;
import com.task.dao.MemberRepo;
import com.task.dao.TransactionRepo;
import com.task.exceptions.BookAlreadyIssuedException;
import com.task.exceptions.BookNotFoundException;
import com.task.exceptions.MaxNumOfIssuedBooksExceed;
import com.task.exceptions.MemberNotFoundException;
import com.task.exceptions.TransactionNotFoundException;
import com.task.model.Book;
import com.task.model.Member;
import com.task.model.Transaction;
import com.task.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService{

	@Autowired
	private TransactionRepo transactionRepo;
	
	@Autowired
	private MemberRepo memberRepo;
	
	@Autowired
	private BookRepo bookRepo;
	
	
	@Override
	public Transaction retriveTransaction(long transactionId) {
		return transactionRepo.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction Not Found"));
	}

	@Override
	public String deleteTransaction(long transactionid) {
		transactionRepo.findById(transactionid)
        .orElseThrow(() ->
                new TransactionNotFoundException("Transaction Not Found"));
		transactionRepo.deleteById(transactionid);
		return "Transaction Succesfully Deleted";
	}

	@Override
	public Transaction createTransaction(long memberid, long bookid) {

	    Member member = memberRepo.findById(memberid)
	            .orElseThrow(() ->
	                    new MemberNotFoundException("Member Not Found Of Id:" + memberid));

	    Book book = bookRepo.findById(bookid)
	            .orElseThrow(() ->
	                    new BookNotFoundException("Book Not Found With Id:" + bookid));

	    if ("Issued".equals(book.getStatus())) {
	        throw new BookAlreadyIssuedException("Book is already issued");
	    }

	    if (member.getNoOfBooksIssued() >= member.getMaxBookLimit()) {
	        throw new MaxNumOfIssuedBooksExceed("Member reached max book limit");
	    }

	    Transaction transaction = new Transaction();
	    transaction.setMember(member);
	    transaction.setBook(book);
	    transaction.setDateOfIssue(LocalDate.now());
	    transaction.setDueDate(LocalDate.now().plusDays(10));
	    book.setStatus("Issued");
	    member.setNoOfBooksIssued(member.getNoOfBooksIssued() + 1);
	    
	    bookRepo.save(book);
	    memberRepo.save(member);
	    transactionRepo.save(transaction);

	    return transaction;
	}


}
