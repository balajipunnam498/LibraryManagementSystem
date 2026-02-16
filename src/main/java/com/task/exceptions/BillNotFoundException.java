package com.task.exceptions;

public class BillNotFoundException extends RuntimeException {

	
	public BillNotFoundException(String message){
		super(message);
	}
}
