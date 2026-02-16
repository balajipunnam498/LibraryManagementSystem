package com.task.model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;
	
	private LocalDate dateOfIssue;
	
	private LocalDate dueDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "memberid")
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bookid")
	private Book book;
	
	public Transaction(LocalDate dateOfIssue, LocalDate dueDate, Member member, Book book, Bill bill) {
		super();
		this.dateOfIssue = dateOfIssue;
		this.dueDate = dueDate;
		this.member = member;
		this.book = book;
		this.bill = bill;
	}


 	public Transaction(LocalDate dateOfIssue, LocalDate dueDate, Member member, Book book) {
		super();
		this.dateOfIssue = dateOfIssue;
		this.dueDate = dueDate;
		this.member = member;
		this.book = book;
	}


	@OneToOne(mappedBy = "transaction",cascade = CascadeType.ALL)
	private Bill bill;
	
}
