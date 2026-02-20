package com.task.servicetest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.task.dao.BillRepo;
import com.task.dao.MemberRepo;
import com.task.exceptions.BillNotFoundException;
import com.task.exceptions.InSufficientFundsException;
import com.task.exceptions.MaxNumOfIssuedBooksExceed;
import com.task.exceptions.MemberNotFoundException;
import com.task.exceptions.MinIssuedBooksExceed;
import com.task.model.Bill;
import com.task.model.Member;
import com.task.serviceimpl.MemberServiceImpl;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock private MemberRepo memberRepo;
    @Mock private BillRepo billRepo;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private Bill bill;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setMemberId(1L);
        member.setNoOfBooksIssued(2);
        member.setMaxBookLimit(5);

        bill = new Bill();
        bill.setBillID(10L);
        bill.setAmount(200.0);
        bill.setPaid(false);
    }

    @Test
    void retriveMember_WhenMemberExists_ShouldReturnMember() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));

        Member result = memberService.retriveMember(1L);

        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(1L);
        verify(memberRepo).findById(1L);
    }

    @Test
    void retriveMember_WhenMemberNotFound_ShouldThrowException() {
        when(memberRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.retriveMember(99L))
            .isInstanceOf(MemberNotFoundException.class)
            .hasMessageContaining("99");
    }


    @Test
    void increaseBookIssued_WhenWithinLimit_ShouldIncreaseAndSave() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepo.save(member)).thenReturn(member);

        // currently 2 issued, max 5 → adding 2 = 4 (within limit)
        Member result = memberService.increaseBookIssued(1L, 2);

        assertThat(result.getNoOfBooksIssued()).isEqualTo(4);
        verify(memberRepo).save(member);
    }

    @Test
    void increaseBookIssued_WhenExceedsMaxLimit_ShouldThrowException() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));

        // currently 2 issued, max 5 → adding 4 = 6 (exceeds limit)
        assertThatThrownBy(() -> memberService.increaseBookIssued(1L, 4))
            .isInstanceOf(MaxNumOfIssuedBooksExceed.class)
            .hasMessageContaining("Limit");

        verify(memberRepo, never()).save(any());
    }

    @Test
    void increaseBookIssued_WhenExactlyAtLimit_ShouldSucceed() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepo.save(member)).thenReturn(member);

        // currently 2 issued, max 5 → adding 3 = exactly 5 (allowed)
        Member result = memberService.increaseBookIssued(1L, 3);

        assertThat(result.getNoOfBooksIssued()).isEqualTo(5);
        verify(memberRepo).save(member);
    }

    @Test
    void increaseBookIssued_WhenMemberNotFound_ShouldThrowException() {
        when(memberRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.increaseBookIssued(99L, 1))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void decreaseBookIssued_WhenValid_ShouldDecreaseAndSave() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepo.save(member)).thenReturn(member);

        // currently 2 issued → subtract 1 = 1 (valid)
        Member result = memberService.decreaseBookIssued(1L, 1);

        assertThat(result.getNoOfBooksIssued()).isEqualTo(1);
        verify(memberRepo).save(member);
    }

    @Test
    void decreaseBookIssued_WhenResultGoesNegative_ShouldThrowException() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));

        // currently 2 issued → subtract 3 = -1 (invalid)
        assertThatThrownBy(() -> memberService.decreaseBookIssued(1L, 3))
            .isInstanceOf(MinIssuedBooksExceed.class)
            .hasMessageContaining("Zero");

        verify(memberRepo, never()).save(any());
    }

    @Test
    void decreaseBookIssued_WhenResultIsExactlyZero_ShouldSucceed() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepo.save(member)).thenReturn(member);

        // currently 2 issued → subtract 2 = exactly 0 (allowed)
        Member result = memberService.decreaseBookIssued(1L, 2);

        assertThat(result.getNoOfBooksIssued()).isEqualTo(0);
        verify(memberRepo).save(member);
    }

    @Test
    void decreaseBookIssued_WhenMemberNotFound_ShouldThrowException() {
        when(memberRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.decreaseBookIssued(99L, 1))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void payBill_WhenExactAmount_ShouldPayAndReturnZeroChange() {
        when(billRepo.findById(10L)).thenReturn(Optional.of(bill));

        String result = memberService.payBill(200.0, 10L);

        assertThat(result).contains("0.0");
        assertThat(bill.isPaid()).isTrue();
        verify(billRepo).save(bill);
    }

    @Test
    void payBill_WhenMoreThanBillAmount_ShouldReturnCorrectChange() {
        when(billRepo.findById(10L)).thenReturn(Optional.of(bill));

        // bill is 200, paying 250 → change = 50
        String result = memberService.payBill(250.0, 10L);

        assertThat(result).contains("50.0");
        assertThat(bill.isPaid()).isTrue();
        verify(billRepo).save(bill);
    }

    @Test
    void payBill_WhenInsufficientAmount_ShouldThrowException() {
        when(billRepo.findById(10L)).thenReturn(Optional.of(bill));

        // bill is 200, paying only 100
        assertThatThrownBy(() -> memberService.payBill(100.0, 10L))
            .isInstanceOf(InSufficientFundsException.class)
            .hasMessageContaining("Insufficient");

        assertThat(bill.isPaid()).isFalse();
        verify(billRepo, never()).save(any());
    }

    @Test
    void payBill_WhenBillNotFound_ShouldThrowException() {
        when(billRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.payBill(500.0, 99L))
            .isInstanceOf(BillNotFoundException.class)
            .hasMessageContaining("99");
    }
}