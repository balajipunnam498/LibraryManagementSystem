package com.task.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.model.Librarian;

@Repository
public interface LibrarianRepo extends JpaRepository<Librarian, Long> {

	Librarian findByUserName(String userName);
}
