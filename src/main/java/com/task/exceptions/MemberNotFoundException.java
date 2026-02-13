package com.task.exceptions;

public class MemberNotFoundException extends RuntimeException{

	public MemberNotFoundException(String message) {
		super(message);
	}
}
