package com.task.servicetest;

import com.task.dao.BillRepo;
import com.task.dao.BookRepo;
import com.task.dao.MemberRepo;
import com.task.dao.TransactionRepo;
import com.task.model.Bill;
import com.task.model.Book;
import com.task.model.Member;
import com.task.model.Transaction;
import com.task.serviceimpl.BillServiceImpl;
import com.task.serviceimpl.BookServiceIMPL;
import com.task.serviceimpl.LibrarianServiceImpl;
import com.task.serviceimpl.MemberServiceImpl;
import com.task.serviceimpl.TransactionServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibrarianServiceImplTest {

    @Mock 
    private BookServiceIMPL bookService;
    @Mock 
    private MemberServiceImpl memberService;
    @Mock 
    private TransactionServiceImpl transactionService;
    @Mock 
    private BillServiceImpl billService;
    @Mock 
    private BookRepo bookRepo;
    @Mock 
    private BillRepo billRepo;
    @Mock 
    private MemberRepo memberRepo;
    @Mock 
    private TransactionRepo transactionRepo;

    @InjectMocks
    private LibrarianServiceImpl librarianService;

    private Member member;
    private Book book;
    private Transaction transaction;
    private Bill bill;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setMemberId(1L);
        member.setNoOfBooksIssued(2);

        book = new Book();
        book.setBookID(101L);
        book.setStatus("Issued");

        bill = new Bill();
        bill.setBillID(201L);
        bill.setAmount(50.0);

        transaction = new Transaction();
        transaction.setTransactionId(301L);
        transaction.setBook(book);
        transaction.setMember(member);
        transaction.setBill(bill);
        transaction.setReturned(false);
        transaction.setDueDate(LocalDate.now().minusDays(5));
    }

    @Test
    void searchBook_ShouldReturnBook_WhenBookExists() {
        when(bookService.displayBookDetails(101L)).thenReturn(book);

        Book result = librarianService.searchBook(101L);

        assertNotNull(result);
        assertEquals(101L, result.getBookID());
        verify(bookService).displayBookDetails(101L);
    }

    @Test
    void verifyMember_ShouldReturnMember_WhenMemberExists() {
        when(memberService.retriveMember(1L)).thenReturn(member);

        Member result = librarianService.verifyMember(1L);

        assertNotNull(result);
        assertEquals(1L, result.getMemberId());
        verify(memberService).retriveMember(1L);
    }

    @Test
    void calculateFine_ShouldReturnZero_WhenWithinAllowedDays() {
        LocalDate recentDate = LocalDate.now().minusDays(5);

        double fine = librarianService.calculateFine(recentDate);

        assertEquals(0.0, fine);
    }

    @Test
    void calculateFine_ShouldReturnCorrectFine_WhenOverdue() {
        LocalDate issuedDate = LocalDate.now().minusDays(15);

        double fine = librarianService.calculateFine(issuedDate);

        assertEquals(10.0, fine);
    }

    @Test
    void calculateFine_ShouldReturnZero_WhenExactlyOnDueDate() {
        LocalDate issuedDate = LocalDate.now().minusDays(10);

        double fine = librarianService.calculateFine(issuedDate);

        assertEquals(0.0, fine);
    }

    @Test
    void returnBook_ShouldUpdateEntities_AndReturnBillWithFine() {
        when(transactionService.retriveTransaction(301L)).thenReturn(transaction);
        when(bookRepo.save(book)).thenReturn(book);
        when(transactionRepo.save(transaction)).thenReturn(transaction);
        when(billRepo.save(bill)).thenReturn(bill);

        Bill result = librarianService.returnBook(301L);

        assertTrue(transaction.isReturned());
        assertEquals(LocalDate.now(), transaction.getReturnDate());
        assertEquals("Available", book.getStatus());
        assertEquals(50.0, result.getAmount());
        verify(memberService).decreaseBookIssued(1L, 1);
        verify(bookRepo).save(book);
        verify(transactionRepo).save(transaction);
        verify(billRepo).save(bill);
    }

    @Test
    void returnBook_ShouldThrowException_WhenBookAlreadyReturned() {
        transaction.setReturned(true);
        when(transactionService.retriveTransaction(301L)).thenReturn(transaction);

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> librarianService.returnBook(301L)
        );
        assertEquals("Book already returned", ex.getMessage());
        verifyNoInteractions(bookRepo, billRepo, transactionRepo);
    }

    @Test
    void returnBook_ShouldThrowException_WhenBillIsNull() {
        transaction.setBill(null);
        when(transactionService.retriveTransaction(301L)).thenReturn(transaction);

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> librarianService.returnBook(301L)
        );
        assertEquals("Bill not found for this transaction", ex.getMessage());
    }

    @Test
    void registerMember_ShouldSetDefaultsAndSave() {
        when(memberRepo.save(member)).thenReturn(member);

        Member result = librarianService.registerMember(member);

        assertEquals(0, result.getNoOfBooksIssued());
        verify(memberRepo).save(member);
    }

    @Test
    void issueBook_ShouldDelegateToTransactionService() {
        when(transactionService.createTransaction(1L, 101L)).thenReturn(transaction);

        Transaction result = librarianService.issueBook(1L, 101L);

        assertNotNull(result);
        assertEquals(301L, result.getTransactionId());
        verify(transactionService).createTransaction(1L, 101L);
    }


    @Test
    void createBill_ShouldDelegateToBillService() {
        List<Long> bookIds = List.of(101L, 102L);
        when(billService.createBill(bookIds, 1L)).thenReturn(bill);

        Bill result = librarianService.createBill(bookIds, 1L);

        assertNotNull(result);
        assertEquals(201L, result.getBillID());
        verify(billService).createBill(bookIds, 1L);
    }
}