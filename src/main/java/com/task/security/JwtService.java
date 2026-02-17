package com.task.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

@Service

public class JwtService {
	
	@Value("${jwt.secret}")
	private String jwtSecreateKey;
	
	@Value("${jwt.expiration}")
	private Long expiration;
	
	public SecretKey getsigninkey() {
//		 SecretKey key = Jwts.SIG.HS256.key().build();
  //     String base64Key = Encoders.BASE64.encode(key.getEncoded());
		 byte[] keyBytes = Decoders.BASE64.decode(jwtSecreateKey);
	        return Keys.hmacShaKeyFor(keyBytes);
	}
	public String buildToken(Map<String, Object> extraClaims, String username, Long expirationDate) {
		String compact = Jwts.builder().claims(extraClaims)
					  .subject(username)
					  .issuedAt(new Date(System.currentTimeMillis()))
					  .expiration(new Date(System.currentTimeMillis()+expirationDate))
					  .signWith(getsigninkey())
					  .compact();
					return compact;
	}
	public String generateToken(Map<String, Object> extraClaims, String username) {
		return buildToken(extraClaims,username,expiration);
	}
	public String generateToken(String username) {
		return generateToken(new HashMap<>(),username);
	}
	 private Claims extractAllClaims(String token) {
	        return Jwts.parser()
	                .verifyWith(getsigninkey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload();
	    }
	 public <T>  T extractClaim(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = extractAllClaims(token);
	        return claimsResolver.apply(claims);
	    }
	 public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);
	    }
	 
	 public boolean isTokenValid(String token, String username) {
	        final String usernameFromToken = extractUsername(token);
	        return (usernameFromToken.equals(username)) && !isTokenExpired(token);
	    }

	    private boolean isTokenExpired(String token) {
	        return extractExpiration(token).before(new Date());
	    }

	    private Date extractExpiration(String token) {
	        return extractClaim(token, Claims::getExpiration);
	    }
	
	
}
