package com.task.controller;


import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.model.Bill;
import com.task.model.Book;
import com.task.model.Member;
import com.task.model.Transaction;
import com.task.serviceimpl.LibrarianServiceImpl;

@RestController
@RequestMapping("/librarian")
public class LibrarianController {

	@Autowired
	private LibrarianServiceImpl librarianService;
	
	@GetMapping("/searchbook/{bookid}")
	public ResponseEntity<Book> searchBook(@PathVariable long bookid) {
		Book searchBook = librarianService.searchBook(bookid);
		return ResponseEntity.status(HttpStatus.OK).body(searchBook);
		
	}
	
	@GetMapping("/verifymember/{memberid}")
	public ResponseEntity<Member> verifyMember(@PathVariable long memberid){
		Member verifyMember = librarianService.verifyMember(memberid);
		return ResponseEntity.status(HttpStatus.OK).body(verifyMember);
	}
	
	@PostMapping("/issuebook")
	public ResponseEntity<Transaction> issueBook(@RequestParam long memberid, @RequestParam long bookid){
		Transaction issueBook = librarianService.issueBook(memberid, bookid);
		return ResponseEntity.status(HttpStatus.OK).body(issueBook);
	}
	
	@GetMapping("/calculatefine")
	public ResponseEntity<Double> calculateFine(@RequestParam LocalDate date){
		double fine = librarianService.calculateFine(date);
		return ResponseEntity.status(HttpStatus.OK).body(fine);
	}
	
	@PostMapping("/createbill/{id}")
	public ResponseEntity<Bill> createBill(@RequestBody List<Long> bookids, @PathVariable long id){
		Bill bill = librarianService.createBill(bookids, id);
		return ResponseEntity.status(HttpStatus.OK).body(bill);
	}
	
	@DeleteMapping("/returnbook/{transid}")
	public ResponseEntity<Bill> returnBook(@PathVariable long transid){
		Bill returnBook = librarianService.returnBook(transid);
		return ResponseEntity.status(HttpStatus.OK).body(returnBook);
	}
}
