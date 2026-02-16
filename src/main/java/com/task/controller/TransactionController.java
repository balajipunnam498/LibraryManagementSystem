package com.task.controller;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.model.Transaction;
import com.task.serviceimpl.TransactionServiceImpl;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private TransactionServiceImpl transactionServiceImpl;
	
	@DeleteMapping("/deletetransaction/{id}")
	public ResponseEntity<String> deleteTransaction(@PathVariable long id){
		String deleteTransaction = transactionServiceImpl.deleteTransaction(id);
		return ResponseEntity.status(HttpStatus.OK).body(deleteTransaction);
	}
	
	@GetMapping("/retrivetransaction/{id}")
	public ResponseEntity<Transaction>  retrivetransaction(@PathVariable long id) {
		Transaction retriveTransaction = transactionServiceImpl.retriveTransaction(id);
		return ResponseEntity.status(HttpStatus.OK).body(retriveTransaction);
	}
	
	@PostMapping("/createtransaction")
	public ResponseEntity<Transaction> createTransaction(@RequestParam long memberid, @RequestParam long bookid){
		Transaction transaction = transactionServiceImpl.createTransaction(memberid, bookid);
		return ResponseEntity.status(HttpStatus.OK).body(transaction);
	}
}
