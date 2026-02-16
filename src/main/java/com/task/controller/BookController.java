package com.task.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.model.Book;
import com.task.serviceimpl.BookServiceIMPL;

@RestController
@RequestMapping("/book")
public class BookController {

	@Autowired
	private BookServiceIMPL bookservice;
	
	@GetMapping("/findbookbyid/{id}")
	public ResponseEntity<Book> detailsOfBookById(@PathVariable long id){
		Book book = bookservice.displayBookDetails(id);
		return ResponseEntity.status(HttpStatus.OK).body(book);
	}
	
	@PutMapping("/updatebookstatusbyid/{id}")
	public ResponseEntity<Book> updateBookStatus(@PathVariable long id, @RequestParam String status){
		Book book = bookservice.updateBookStatus(id, status);
		return ResponseEntity.status(HttpStatus.OK).body(book);
	}
	
	@GetMapping("/getallbooks")
	public ResponseEntity<List<Book>> findAllbooks(){
		List<Book> allBooks = bookservice.getAllBooks();
		return ResponseEntity.status(HttpStatus.OK).body(allBooks);
	}
}
