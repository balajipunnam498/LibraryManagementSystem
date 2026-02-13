package com.task.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<String> bookNotFound(BookNotFoundException bookNotFound){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bookNotFound.getMessage());
	}
	
	@ExceptionHandler(MemberNotFoundException.class)
	public ResponseEntity<String> memberNotFound(MemberNotFoundException memberNotFound){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(memberNotFound.getMessage());
	}
	
	@ExceptionHandler(MaxNumOfIssuedBooksExceed.class)
	public ResponseEntity<String> maxNumberOfIssuedBookExceeded(MaxNumOfIssuedBooksExceed maxNumOfIssuedBooksExceed){
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(maxNumOfIssuedBooksExceed.getMessage());
	} 
	@ExceptionHandler(MinIssuedBooksExceed.class)
	public ResponseEntity<String> minNumberOfIssuedBookExceeded(MinIssuedBooksExceed minIssuedBooksExceed){
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(minIssuedBooksExceed.getMessage());
	} 
}

