package com.task.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.model.Member;

@Repository
public interface MemberRepo extends JpaRepository<Member, Long> {

}
