package com.task.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.dao.MemberRepo;
import com.task.exceptions.MaxNumOfIssuedBooksExceed;
import com.task.exceptions.MemberNotFoundException;
import com.task.exceptions.MinIssuedBooksExceed;
import com.task.model.Member;
import com.task.service.MemberService;

@Service
public class MemberServiceImpl implements MemberService{

	@Autowired
	private MemberRepo memberrepo;
	
	@Override
	public Member retriveMember(long memberid) {
		Member member = memberrepo.findById(memberid).orElseThrow(() -> new MemberNotFoundException("Member Not Found Of Id:"+memberid));
		return member;
	}

	@Override
	public Member increaseBookIssued(long memberid, int noOfBooks) {
		Member member = memberrepo.findById(memberid).orElseThrow(() -> new MemberNotFoundException("Member Not Found Of Id:"+memberid));
		if(member.getNoOfBooksIssued()+noOfBooks>member.getMaxBookLimit()) {
			throw new MaxNumOfIssuedBooksExceed("Limit Excceded");
		}
		member.setNoOfBooksIssued(member.getNoOfBooksIssued()+noOfBooks);
		return memberrepo.save(member); 
	}

	@Override
	public Member decreaseBookIssued(long memberid, int noOfBooks) {
		Member member = memberrepo.findById(memberid).orElseThrow(() -> new MemberNotFoundException("Member Not Found Of Id:"+memberid));
		if(member.getNoOfBooksIssued()-noOfBooks<0) {
			throw new MinIssuedBooksExceed("Cannot Be Zero");
		}
		member.setNoOfBooksIssued(member.getNoOfBooksIssued()-noOfBooks);
		return memberrepo.save(member); 
	}

}
