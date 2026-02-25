package com.task.serviceimpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.dao.BillRepo;
import com.task.dao.BookRepo;
import com.task.dao.MemberRepo;
import com.task.dao.TransactionRepo;
import com.task.exceptions.BillNotFoundException;
import com.task.exceptions.BookAlreadyIssuedException;
import com.task.exceptions.MemberNotFoundException;
import com.task.model.Bill;
import com.task.model.Book;
import com.task.model.Member;
import com.task.model.Transaction;
import com.task.service.BillService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BillServiceImpl implements BillService{

	@Autowired
	private BillRepo billRepo;
	
	@Autowired
	private BookRepo bookrepo;
	
	@Autowired
	private MemberRepo memberRepo;
	
	@Autowired
	private MemberServiceImpl memberservice;
	
	@Autowired
	private TransactionRepo transactionRepo;
	
	@Transactional
	public Bill createBill(List<Long> bookIds, long memberId) {

	    Member member = memberRepo.findById(memberId)
	            .orElseThrow(() ->
	                    new MemberNotFoundException("Member not found"));

	    List<Book> books = bookrepo.findAllById(bookIds);

	    double baseAmount = 0;
	    List<Transaction> transactions = new ArrayList<>();
	    Bill bill = new Bill();
	    bill.setDateOfBill(LocalDate.now());
	    bill.setMember(member);
	    bill.setAmount(0);
	    bill = billRepo.save(bill);
	    for (Book book : books) {

	        if ("Issued".equals(book.getStatus())) {
	            throw new BookAlreadyIssuedException("Book already issued");
	        }

	        Transaction tx = new Transaction();
	        tx.setMember(member);
	        tx.setBook(book);
	        tx.setBill(bill);
	        tx.setDateOfIssue(LocalDate.now());
	        tx.setDueDate(LocalDate.now().plusDays(10));

	        baseAmount += book.getPrice();

	        book.setStatus("Issued");

	        transactionRepo.save(tx);
	        transactions.add(tx);
	    }

	    memberservice.increaseBookIssued(memberId, books.size());
	    bill.setAmount(baseAmount);
	    bill.setTransactions(transactions);
	    bookrepo.saveAll(books);
	    billRepo.save(bill);

	    return bill;
	}



	@Override
	@Transactional
	public Bill updateBill(long billId, Bill updatedBill) {

	    Bill existingBill = billRepo.findById(billId)
	        .orElseThrow(() ->
	            new BillNotFoundException("Bill Not Found With Id: " + billId));
	    if (updatedBill.getDateOfBill() != null) {
	        existingBill.setDateOfBill(updatedBill.getDateOfBill());
	    }
	    existingBill.setAmount(updatedBill.getAmount());
	    if (updatedBill.getMember() != null &&
	        updatedBill.getMember().getMemberId() != null) {

	        Long memberId = updatedBill.getMember().getMemberId();

	        Member member = memberRepo.findById(memberId)
	            .orElseThrow(() ->
	                new MemberNotFoundException(
	                    "Member Not Found With Id: " + memberId));

	        existingBill.setMember(member);
	    }
	    return billRepo.save(existingBill);
	}


	
}
