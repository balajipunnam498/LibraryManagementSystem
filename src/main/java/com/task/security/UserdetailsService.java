 package com.task.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.task.dao.LibrarianRepo;
import com.task.model.Authorities;
import com.task.model.Librarian;

@Service
public class UserdetailsService implements UserDetailsService {

	
	@Autowired
	private LibrarianRepo librarianRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Librarian userName = librarianRepo.findByUserName(username);
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		List<Authorities> authorities = userName.getAuthorities();
		for(Authorities authority: authorities) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+authority.getAuthoritieType()));
		}
		UserDetails userDetails = User.builder().authorities(grantedAuthorities)
						.username(userName.getUserName())
						.password(userName.getPassword())
						.build();
		return userDetails;
	}

}
