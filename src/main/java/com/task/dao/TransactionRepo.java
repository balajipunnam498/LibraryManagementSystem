package com.task.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.model.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction,Long>{
	

}
