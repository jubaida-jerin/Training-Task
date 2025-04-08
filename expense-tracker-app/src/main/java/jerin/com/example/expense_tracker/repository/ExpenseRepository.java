package jerin.com.example.expense_tracker.repository;

import jerin.com.example.expense_tracker.model.Expense;
import jerin.com.example.expense_tracker.model.ExpenseCategory;
import jerin.com.example.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
    List<Expense> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<Expense> findByUserAndCategory(User user, ExpenseCategory category);
    List<Expense> findByUserOrderByDateDesc(User user);

}
