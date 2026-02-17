package com.task.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.model.Librarian;
import java.util.List;


public interface LibrarianRepo extends JpaRepository<Librarian, Long> {

	Librarian findByUserName(String userName);
}
