package com.task.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.dao.BookRepo;
import com.task.exceptions.BookNotFoundException;
import com.task.model.Book;
import com.task.model.BookType;
import com.task.service.BookService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookServiceIMPL implements BookService {

	@Autowired
	private BookRepo bookrepo;
	
	
	@Override
	public Book displayBookDetails(long id) {
		Book book = bookrepo.findById(id).orElseThrow(()-> new BookNotFoundException("Book Not Found With Id:"+id));
		return book;
	}


	@Override
	public Book updateBookStatus(long id,String status) {
		Book book = bookrepo.findById(id).orElseThrow(()-> new BookNotFoundException("Book Not Found With Id:"+id));
		book.setStatus(status);
		return bookrepo.save(book);
		
	}

	@Override
	public List<Book> getAllBooks() {
		return bookrepo.findAll();
	}
	
	@Override
    public Book findByName(String bookName) {
        return bookrepo.findByBookName(bookName);
    }

    @Override
    public List<Book> findByType(BookType type) {
        return bookrepo.findByType(type);
    }

	
}
