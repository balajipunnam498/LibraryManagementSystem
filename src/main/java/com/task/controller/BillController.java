package com.task.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task.model.Bill;
import com.task.serviceimpl.BillServiceImpl;

@RestController
@RequestMapping("/bill")
public class BillController {

	@Autowired
	private BillServiceImpl billService;
	
	@PostMapping("/createBill/{memberId}")
	public ResponseEntity<Bill> createBill(@RequestBody List<Long> billId, @PathVariable long memberId){
		Bill bill = billService.createBill(billId, memberId);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(bill);
	}
}
