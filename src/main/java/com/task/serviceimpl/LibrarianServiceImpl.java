package com.task.serviceimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.dao.BillRepo;
import com.task.dao.BookRepo;
import com.task.dao.MemberRepo;
import com.task.dao.TransactionRepo;
import com.task.model.Bill;
import com.task.model.Book;
import com.task.model.Member;
import com.task.model.Transaction;
import com.task.service.LibrarianService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LibrarianServiceImpl implements LibrarianService{

	@Autowired
	private BookServiceIMPL bookService;
	
	@Autowired
	private MemberServiceImpl memberService;
	
	@Autowired
	private TransactionServiceImpl transactionService;
	
	@Autowired
	private BillServiceImpl billservice;
	
	@Autowired
	private BookRepo bookRepo;
	
	@Autowired
	private BillRepo billRepo;
	
	@Autowired
	private MemberRepo memberRepo;
	
	@Autowired
	private TransactionRepo transactionRepo;
	
	
	@Override
	public Book searchBook(long bookid) {
		return bookService.displayBookDetails(bookid);
		
	}

	@Override
	public Member verifyMember(long memberid) {
		return memberService.retriveMember(memberid);
	}

	@Override
	public Transaction issueBook(long memberid, long bookid) {
		return transactionService.createTransaction(memberid, bookid);
	
	}

	@Override
	public double calculateFine(LocalDate issuedDate) {
	    LocalDate now = LocalDate.now();

	    int allowedDays = 10;
	    double finePerDay = 2.0;

	    LocalDate dueDate = issuedDate.plusDays(allowedDays);

	    if (now.isAfter(dueDate)) {
	        long overdueDays = ChronoUnit.DAYS.between(dueDate, now);
	        return overdueDays * finePerDay;
	    }

	    return 0;
	}

	@Override
	public Bill createBill(List<Long> bookid, long memberid) {
		Bill bill = billservice.createBill(bookid, memberid);
		return bill;
	}
	
	@Override
	public Bill returnBook(long transactionId) {

	    Transaction transaction = transactionService.retriveTransaction(transactionId);

	    if (transaction.isReturned()) {
	        throw new IllegalStateException("Book already returned");
	    }

	    Bill bill = transaction.getBill();
	    if (bill == null) {
	        throw new IllegalStateException("Bill not found for this transaction");
	    }
	    double fine = calculateFine(transaction.getDueDate());

	    transaction.setReturned(true);
	    transaction.setReturnDate(LocalDate.now());

	    Book book = transaction.getBook();
	    book.setStatus("Available");

	    Member member = transaction.getMember();
	    memberService.decreaseBookIssued(member.getMemberId(), 1);

	    bill.setAmount(bill.getAmount() + fine);

	    bookRepo.save(book);
	    transactionRepo.save(transaction);
	    billRepo.save(bill);

	    return bill;
	}

	@Override
	public Member registerMember(Member member) {
		member.setDefaults();
		member.setNoOfBooksIssued(0);
		return memberRepo.save(member);
		
	}



	
}
