package com.task.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Authorities")
@AllArgsConstructor
public class Authorities {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long authoritieId;
	
	public Authorities(String authoritieType) {
		super();
		this.authoritieType = authoritieType;
	}

	private String authoritieType;
	
	
}
