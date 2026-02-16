package com.task.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.model.Bill;

public interface BillRepo extends JpaRepository<Bill,Long> {
	
	

}
