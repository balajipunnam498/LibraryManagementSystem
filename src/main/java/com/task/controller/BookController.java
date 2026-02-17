package com.task.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.model.Book;
import com.task.model.BookType;
import com.task.serviceimpl.BookServiceIMPL;

@RestController
@RequestMapping("/book")
public class BookController {

	@Autowired
	private BookServiceIMPL bookservice;
	
	@GetMapping("/findbookbyid/{bookid}")
	public ResponseEntity<Book> detailsOfBookById(@PathVariable long bookid){
		Book book = bookservice.displayBookDetails(bookid);
		return ResponseEntity.status(HttpStatus.OK).body(book);
	}
	
	@PutMapping("/updatebookstatusbyid")
	public ResponseEntity<Book> updateBookStatus(@RequestBody Book bookDetails){
		Book book = bookservice.updateBookStatus(bookDetails.getBookID(), bookDetails.getStatus());
		return ResponseEntity.status(HttpStatus.OK).body(book);
	}
	
	@GetMapping("/getallbooks")
	public ResponseEntity<List<Book>> findAllbooks(){
		List<Book> allBooks = bookservice.getAllBooks();
		return ResponseEntity.status(HttpStatus.OK).body(allBooks);
	}
	
	@PostMapping("/searchbyname")
	public ResponseEntity<Book> findByName(@RequestParam String bookname) {
	     Book list = bookservice.findByName(bookname);
	     return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@PostMapping("/searchbytype")
	public ResponseEntity<List<Book>>  findByType( @RequestParam BookType book) {
	    List<Book> list = bookservice.findByType(book);
	    return ResponseEntity.status(HttpStatus.OK).body(list);
	}

}
