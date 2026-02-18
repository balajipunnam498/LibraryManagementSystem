package com.task.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.task.model.Book;
import com.task.model.BookType;

@Service
public interface BookService {

	Book displayBookDetails(long id);
	
	Book updateBookStatus(long id,String status);
	
	List<Book> getAllBooks();
	
	Book findByName(String bookName);

    List<Book> findByType(BookType type);
}
