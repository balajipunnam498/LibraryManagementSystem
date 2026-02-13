package com.task.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
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

	private long memberID;
	
	private LocalDate dateOfBill;
	
	private double amount;
	
}
