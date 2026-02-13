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
}
