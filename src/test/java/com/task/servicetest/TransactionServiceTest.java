package com.task.servicetest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.task.dao.BookRepo;
import com.task.dao.MemberRepo;
import com.task.dao.TransactionRepo;
import com.task.exceptions.BookAlreadyIssuedException;
import com.task.exceptions.BookNotFoundException;
import com.task.exceptions.MaxNumOfIssuedBooksExceed;
import com.task.exceptions.MemberNotFoundException;
import com.task.exceptions.TransactionNotFoundException;
import com.task.model.Book;
import com.task.model.Member;
import com.task.model.Transaction;
import com.task.serviceimpl.TransactionServiceImpl;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepo transactionRepo;
    @Mock private MemberRepo memberRepo;
    @Mock private BookRepo bookRepo;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Member member;
    private Book book;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setMemberId(1L);
        member.setNoOfBooksIssued(2);
        member.setMaxBookLimit(5);

        book = new Book();
        book.setBookID(10L);
        book.setStatus("Available");

        transaction = new Transaction();
        transaction.setTransactionId(100L);
        transaction.setMember(member);
        transaction.setBook(book);
        transaction.setDateOfIssue(LocalDate.now());
        transaction.setDueDate(LocalDate.now().plusDays(10));
    }

    @Test
    void retriveTransaction_WhenExists_ShouldReturnTransaction() {
        when(transactionRepo.findById(100L)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.retriveTransaction(100L);

        assertThat(result).isNotNull();
        assertThat(result.getTransactionId()).isEqualTo(100L);
        verify(transactionRepo,times(1)).findById(100L);
    }

    @Test
    void retriveTransaction_WhenNotFound_ShouldThrowException() {
        when(transactionRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.retriveTransaction(99L))
            .isInstanceOf(TransactionNotFoundException.class)
            .hasMessageContaining("Transaction Not Found");
    }

    @Test
    void deleteTransaction_WhenExists_ShouldDeleteAndReturnMessage() {
        when(transactionRepo.findById(100L)).thenReturn(Optional.of(transaction));

        String result = transactionService.deleteTransaction(100L);

        assertThat(result).contains("Deleted");
        verify(transactionRepo).deleteById(100L);
    }

    @Test
    void deleteTransaction_WhenNotFound_ShouldThrowException() {
        when(transactionRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deleteTransaction(99L))
            .isInstanceOf(TransactionNotFoundException.class)
            .hasMessageContaining("Transaction Not Found");

        verify(transactionRepo, never()).deleteById(anyLong());
    }

    @Test
    void createTransaction_WhenValid_ShouldCreateAndReturnTransaction() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepo.findById(10L)).thenReturn(Optional.of(book));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.createTransaction(1L, 10L);

        assertThat(result).isNotNull();
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getBook()).isEqualTo(book);
        assertThat(result.getDateOfIssue()).isEqualTo(LocalDate.now());
        assertThat(result.getDueDate()).isEqualTo(LocalDate.now().plusDays(10));

        assertThat(book.getStatus()).isEqualTo("Issued");
        assertThat(member.getNoOfBooksIssued()).isEqualTo(3); // was 2, now 3

        verify(bookRepo).save(book);
        verify(memberRepo).save(member);
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    void createTransaction_WhenMemberNotFound_ShouldThrowException() {
        when(memberRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(99L, 10L))
            .isInstanceOf(MemberNotFoundException.class)
            .hasMessageContaining("99");

        verifyNoInteractions(bookRepo, transactionRepo);
    }

    @Test
    void createTransaction_WhenBookNotFound_ShouldThrowException() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(1L, 99L))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessageContaining("99");

        verifyNoInteractions(transactionRepo);
    }

    @Test
    void createTransaction_WhenBookAlreadyIssued_ShouldThrowException() {
        book.setStatus("Issued");

        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepo.findById(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> transactionService.createTransaction(1L, 10L))
            .isInstanceOf(BookAlreadyIssuedException.class)
            .hasMessageContaining("already issued");

        verify(bookRepo, never()).save(any());
        verify(transactionRepo, never()).save(any());
    }

    @Test
    void createTransaction_WhenMemberAtMaxLimit_ShouldThrowException() {
        member.setNoOfBooksIssued(5); // already at max limit of 5

        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepo.findById(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> transactionService.createTransaction(1L, 10L))
            .isInstanceOf(MaxNumOfIssuedBooksExceed.class)
            .hasMessageContaining("max book limit");

        verify(memberRepo, never()).save(any());
        verify(transactionRepo, never()).save(any());
    }

    @Test
    void createTransaction_WhenMemberOneBeforeLimit_ShouldSucceed() {
        member.setNoOfBooksIssued(4);

        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepo.findById(10L)).thenReturn(Optional.of(book));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.createTransaction(1L, 10L);

        assertThat(result).isNotNull();
        assertThat(member.getNoOfBooksIssued()).isEqualTo(5);
        verify(transactionRepo).save(any(Transaction.class));
    }
}