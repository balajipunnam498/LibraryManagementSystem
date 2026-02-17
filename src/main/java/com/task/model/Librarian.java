package com.task.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
	private Long librarianId;
	
	private String userName;
	
	private String password;

	@ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinTable(name = "Librarian_Authrities",
				joinColumns = @JoinColumn(name ="librarianId"),
				inverseJoinColumns = @JoinColumn(name="authoritieId"))
	private List<Authorities> authorities;
	
	
	public Librarian(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}


	public Librarian(String userName, String password, List<Authorities> authorities) {
		super();
		this.userName = userName;
		this.password = password;
		this.authorities = authorities;
	}
	
	
}
