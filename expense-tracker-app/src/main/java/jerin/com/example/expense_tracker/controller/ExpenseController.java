package jerin.com.example.expense_tracker.controller;

import jakarta.validation.Valid;
import jerin.com.example.expense_tracker.model.Expense;
import jerin.com.example.expense_tracker.model.ExpenseCategory;
import jerin.com.example.expense_tracker.model.User;
import jerin.com.example.expense_tracker.repository.ExpenseCategoryRepository;
import jerin.com.example.expense_tracker.repository.ExpenseRepository;
import jerin.com.example.expense_tracker.repository.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseCategoryRepository categoryRepository;

    public ExpenseController(ExpenseRepository expenseRepository,
                             UserRepository userRepository,
                             ExpenseCategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        return expense.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getExpensesByUser(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Expense> expenses = expenseRepository.findByUser(user.get());
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<Expense>> getExpensesByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user.get(), start, end);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(
            @PathVariable Long userId,
            @PathVariable Long categoryId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<ExpenseCategory> category = categoryRepository.findById(categoryId);
        if (user.isEmpty() || category.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Expense> expenses = expenseRepository.findByUserAndCategory(user.get(), category.get());
        return ResponseEntity.ok(expenses);
    }

    @PostMapping
    public ResponseEntity<?> createExpense(@Valid @RequestBody Expense expense) {
        if (expense.getUser() == null || expense.getUser().getId() == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }
        if (expense.getCategory() == null || expense.getCategory().getId() == null) {
            return ResponseEntity.badRequest().body("Category ID is required");
        }

        Optional<User> user = userRepository.findById(expense.getUser().getId());
        Optional<ExpenseCategory> category = categoryRepository.findById(expense.getCategory().getId());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body("Category not found");
        }

        expense.setUser(user.get());
        expense.setCategory(category.get());

        Expense savedExpense = expenseRepository.save(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expenseDetails) {
        return expenseRepository.findById(id)
                .map(expense -> {
                    if (expenseDetails.getUser() != null && expenseDetails.getUser().getId() != null) {
                        Optional<User> user = userRepository.findById(expenseDetails.getUser().getId());
                        if (user.isEmpty()) {
                            return ResponseEntity.badRequest().body("User not found");
                        }
                        expense.setUser(user.get());
                    }

                    if (expenseDetails.getCategory() != null && expenseDetails.getCategory().getId() != null) {
                        Optional<ExpenseCategory> category = categoryRepository.findById(expenseDetails.getCategory().getId());
                        if (category.isEmpty()) {
                            return ResponseEntity.badRequest().body("Category not found");
                        }
                        expense.setCategory(category.get());
                    }

                    if (expenseDetails.getDescription() != null) {
                        expense.setDescription(expenseDetails.getDescription());
                    }
                    if (expenseDetails.getAmount() != null) {
                        expense.setAmount(expenseDetails.getAmount());
                    }
                    if (expenseDetails.getDate() != null) {
                        expense.setDate(expenseDetails.getDate());
                    }

                    Expense updatedExpense = expenseRepository.save(expense);
                    return ResponseEntity.ok(updatedExpense);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<BigDecimal> getTotalExpensesByUser(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BigDecimal total = expenseRepository.findByUser(user.get())
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(total);
    }
}
