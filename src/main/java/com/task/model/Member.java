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
	
	  public void setDefaults() {
	        if (dateOfmembership == null) {
	        	dateOfmembership = LocalDate.now();
	        }
	        if (maxBookLimit == 0) {
	        	maxBookLimit = (memberType == Membertype.STUDENT_TYPE) ? 5 : 10;
	        }
	
	  }
}
