package com.task.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Librarians")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Librarian {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long librarianId;
	
	private String userName;
	
	private String password;

	public Librarian(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	
	
}
