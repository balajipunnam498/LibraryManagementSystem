package com.task.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.model.Book;
import com.task.model.BookType;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {

	 Book findByBookName(String bookName);

	 List<Book> findByType(BookType type);
}
