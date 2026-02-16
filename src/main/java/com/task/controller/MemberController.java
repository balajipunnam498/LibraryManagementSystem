package com.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.model.Member;
import com.task.serviceimpl.MemberServiceImpl;

@RestController
@RequestMapping("/member")
public class MemberController {
	
	@Autowired
	private MemberServiceImpl memberServiceImpl;
	
	@GetMapping("/findbymemberid/{memberid}")
	public ResponseEntity<Member> retriveMember(@PathVariable long memberid) {
		Member retriveMember = memberServiceImpl.retriveMember(memberid);
		return ResponseEntity.status(HttpStatus.OK).body(retriveMember);
	}
	
	@PutMapping("/increasebooksissued/{id}")
	public ResponseEntity<Member> increaseBookissued(@PathVariable long id, @RequestParam int numOfBooks){
		Member increaseBookIssued = memberServiceImpl.increaseBookIssued(id, numOfBooks);
		return ResponseEntity.status(HttpStatus.OK).body(increaseBookIssued);
	}
	
	@PutMapping("/decreasebooksissued/{id}")
	public ResponseEntity<Member> decreaseBookissued(@PathVariable long id, @RequestParam int numOfBooks){
		Member increaseBookIssued = memberServiceImpl.decreaseBookIssued(id, numOfBooks);
		return ResponseEntity.status(HttpStatus.OK).body(increaseBookIssued);
	}
	
	@PostMapping("/paybill/{billId}")
	public ResponseEntity<String> payBill(@RequestParam double amount,@PathVariable long billId){
		String payBill = memberServiceImpl.payBill(amount, billId);
		return ResponseEntity.status(HttpStatus.OK).body(payBill);
	}
}
