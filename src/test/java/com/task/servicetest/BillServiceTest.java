package com.task.servicetest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.task.dao.*;
import com.task.exceptions.*;
import com.task.model.*;
import com.task.serviceimpl.BillServiceImpl;
import com.task.serviceimpl.MemberServiceImpl;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock private BillRepo billRepo;
    @Mock private BookRepo bookRepo;
    @Mock private MemberRepo memberRepo;
    @Mock private MemberServiceImpl memberService;
    @Mock private TransactionRepo transactionRepo;

    @InjectMocks
    private BillServiceImpl billService;

    private Member member;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setMemberId(1L);

        book1 = new Book();
        book1.setBookID(10L);
        book1.setPrice(100);
        book1.setStatus("Available");

        book2 = new Book();
        book2.setBookID(20L);
        book2.setPrice(200);
        book2.setStatus("Available");
    }

    @Test
    void createBill_WhenValidData_ShouldCreateBillSuccessfully() {

        List<Long> bookIds = Arrays.asList(10L, 20L);

        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepo.findAllById(bookIds)).thenReturn(Arrays.asList(book1, book2));
        when(billRepo.save(any(Bill.class))).thenAnswer(i -> i.getArgument(0));

        Bill result = billService.createBill(bookIds, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(300);
        assertThat(result.getTransactions()).hasSize(2);

        verify(memberService).increaseBookIssued(1L, 2);
        verify(transactionRepo, times(2)).save(any(Transaction.class));
        verify(bookRepo).saveAll(anyList());
        verify(billRepo, atLeastOnce()).save(any(Bill.class));
    }

    @Test
    void createBill_WhenMemberNotFound_ShouldThrowException() {

        when(memberRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            billService.createBill(List.of(10L), 99L))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void createBill_WhenBookAlreadyIssued_ShouldThrowException() {

        book1.setStatus("Issued");

        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepo.findAllById(List.of(10L)))
                .thenReturn(List.of(book1));
        when(billRepo.save(any(Bill.class))).thenAnswer(i -> i.getArgument(0));

        assertThatThrownBy(() ->
            billService.createBill(List.of(10L), 1L))
            .isInstanceOf(BookAlreadyIssuedException.class);
    }

    @Test
    void updateBill_WhenValidData_ShouldUpdateSuccessfully() {

        Bill existing = new Bill();
        existing.setBillID(1L);
        existing.setAmount(100);

        Bill updated = new Bill();
        updated.setAmount(500);
        updated.setDateOfBill(LocalDate.now());
        updated.setMember(member);

        when(billRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(billRepo.save(any(Bill.class))).thenAnswer(i -> i.getArgument(0));

        Bill result = billService.updateBill(1L, updated);

        assertThat(result.getAmount()).isEqualTo(500);
        assertThat(result.getMember()).isEqualTo(member);

        verify(billRepo).save(existing);
    }

    @Test
    void updateBill_WhenBillNotFound_ShouldThrowException() {

        when(billRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            billService.updateBill(99L, new Bill()))
            .isInstanceOf(BillNotFoundException.class);
    }

    @Test
    void updateBill_WhenMemberNotFound_ShouldThrowException() {

        Bill existing = new Bill();
        existing.setBillID(1L);

        Bill updated = new Bill();
        Member wrongMember = new Member();
        wrongMember.setMemberId(99L);
        updated.setMember(wrongMember);

        when(billRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(memberRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            billService.updateBill(1L, updated))
            .isInstanceOf(MemberNotFoundException.class);
    }
}
