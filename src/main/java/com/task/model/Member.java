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
@Table(name = "Members")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long MemberId;
	
	@Enumerated(EnumType.STRING)
	
	private Membertype memberType;
	
	private LocalDate dateOfmembership;
	
	private int noOfBooksIssued;
	
	private int maxBookLimit;
	
	private String name;
	
	private String address;
	
	private String phoneNumber;
	
	@OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Transaction> transactions;

	@OneToMany(mappedBy ="member",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonIgnore

	private List<Bill> bills;
	
	  public void setDefaults() {
	        if (dateOfmembership == null) {
	        	dateOfmembership = LocalDate.now();
	        }
	        if (maxBookLimit == 0) {
	        	maxBookLimit = (memberType == Membertype.STUDENT_TYPE) ? 5 : 10;
	        }
	
	  }

	  public Member(Membertype memberType, LocalDate dateOfmembership, int noOfBooksIssued, int maxBookLimit, String name,
			String address, String phoneNumber, List<Transaction> transactions, List<Bill> bills) {
		super();
		this.memberType = memberType;
		this.dateOfmembership = dateOfmembership;
		this.noOfBooksIssued = noOfBooksIssued;
		this.maxBookLimit = maxBookLimit;
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.transactions = transactions;
		this.bills = bills;
	  }

}
