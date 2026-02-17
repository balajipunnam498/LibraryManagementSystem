package com.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task.dao.LibrarianRepo;
import com.task.model.Librarian;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private LibrarianRepo repo;
	
	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private com.task.security.JwtService jwtService;
	
	@PostMapping("/signup")
	public Librarian signup(@RequestBody Librarian librarian) {
		String password = librarian.getPassword();
		librarian.setPassword(encoder.encode(password));
		Librarian save = repo.save(librarian);
		return save;
	}
	
	@GetMapping("/login")
	public ResponseEntity<String> login(@RequestBody Librarian librarian) {
		try {
		manager.authenticate(new UsernamePasswordAuthenticationToken(librarian.getUserName(), librarian.getPassword()));
		}catch(Exception e) {
			return   ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
		}
		String token = jwtService.generateToken(librarian.getUserName());
		return ResponseEntity.status(HttpStatus.OK).body(token);		
	}
}
