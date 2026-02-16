package com.task.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.model.Book;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {

	
}
