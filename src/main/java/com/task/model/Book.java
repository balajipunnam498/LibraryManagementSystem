package com.task.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Books")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long bookID;
	
	private String authorName;
	
	private String bookName;
	
	@Enumerated(EnumType.STRING)
	private BookType type;
	
	private double price;
	
	private String rackNo;
	
	private String status;
	
	private String edition;
	
	private LocalDate dateOfPurchase;
	

	
	
	
}
