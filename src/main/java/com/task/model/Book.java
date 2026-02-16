package com.task.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	
	@OneToMany(mappedBy = "book",cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Transaction> transaction;

	public Book(String authorName, String bookName, BookType type, double price, String rackNo, String status,
			String edition, LocalDate dateOfPurchase) {
		super();
		this.authorName = authorName;
		this.bookName = bookName;
		this.type = type;
		this.price = price;
		this.rackNo = rackNo;
		this.status = status;
		this.edition = edition;
		this.dateOfPurchase = dateOfPurchase;
	}
	
	
	
	
}
