package com.task.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.model.Librarian;

public interface LibrarianRepo extends JpaRepository<Librarian, Long> {

	
}
