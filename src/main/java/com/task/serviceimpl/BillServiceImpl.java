package com.task.serviceimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.dao.BillRepo;
import com.task.dao.BookRepo;
import com.task.dao.MemberRepo;
import com.task.exceptions.BookNotFoundException;
import com.task.exceptions.MemberNotFoundException;
import com.task.model.Bill;
import com.task.model.Book;
import com.task.model.Member;
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

	
}
