package com.task.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Bills")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bill {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long billID;
	
	private LocalDate dateOfBill;
	
	private double amount;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "memberid")
	private Member member;
	
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();


	public Bill(LocalDate dateOfBill, double amount, Member member, List<Transaction> transaction) {
		super();
		this.dateOfBill = dateOfBill;
		this.amount = amount;
		this.member = member;
		this.transactions = transaction;
	}
}
