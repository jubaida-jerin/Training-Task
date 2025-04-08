package jerin.com.example.expense_tracker.repository;

import jerin.com.example.expense_tracker.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    Optional<ExpenseCategory> findByName(String name);
    boolean existsByName(String name);

}
