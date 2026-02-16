package com.task.serviceimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.dao.BillRepo;
import com.task.dao.BookRepo;
import com.task.dao.MemberRepo;
import com.task.dao.TransactionRepo;
import com.task.exceptions.BillNotFoundException;
import com.task.exceptions.BookNotFoundException;
import com.task.exceptions.MemberNotFoundException;
import com.task.exceptions.TransactionNotFoundException;
import com.task.model.Bill;
import com.task.model.Book;
import com.task.model.Member;
import com.task.model.Transaction;
import com.task.service.BillService;

@Service
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
	
	@Override
	public Bill createBill(List<Long> listofBoooks, long memberid) {
		Bill bill = new Bill();
		Member member = memberRepo.findById(memberid).orElseThrow(() -> new MemberNotFoundException("Member Not Found Of Id:"+memberid));
		bill.setDateOfBill(LocalDate.now());
		List<Book> books = bookrepo.findAllById(listofBoooks);
		memberservice.increaseBookIssued(memberid, books.size());
		double totalAmount=0;
		for (Book book : books) {
		    totalAmount += book.getPrice();
		    book.setStatus("Issued");
		}
	    bookrepo.saveAll(books);
		bill.setAmount(totalAmount);
		bill.setMember(member);
		bill.setTransaction(null);
		return billRepo.save(bill);
	}

	@Override
	public Bill updateBill(long billId, Bill updatedBill) {

	    Bill existingBill = billRepo.findById(billId)
	            .orElseThrow(() ->
	                    new BillNotFoundException("Bill Not Found With Id: " + billId));

	    existingBill.setDateOfBill(updatedBill.getDateOfBill());
	    existingBill.setAmount(updatedBill.getAmount());
	    if ((updatedBill.getMember() != null &&
	            updatedBill.getMember().getMemberId() != null)) {
	        Long memberId = updatedBill.getMember().getMemberId();

	        Member member = memberRepo.findById(memberId)
	                .orElseThrow(() ->
	                        new MemberNotFoundException("Member Not Found With Id: " + memberId));

	        existingBill.setMember(member);
	    }

	    if (updatedBill.getTransaction() != null &&
	            updatedBill.getTransaction().getTransactionId() != null) {
	        Long transactionId = updatedBill.getTransaction().getTransactionId();

	        Transaction transaction = transactionRepo.findById(transactionId)
	                .orElseThrow(() ->
	                        new TransactionNotFoundException("Transaction Not Found"));

	        existingBill.setTransaction(transaction);
	    }

	    return billRepo.save(existingBill);
	}


	
}
