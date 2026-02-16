package com.task.service;

import java.util.List;

import com.task.model.Bill;

public interface BillService {
	
	Bill createBill(List<Long> listofBoooks, long memberid);
	Bill updateBill(long billId,Bill bill);
	

}
