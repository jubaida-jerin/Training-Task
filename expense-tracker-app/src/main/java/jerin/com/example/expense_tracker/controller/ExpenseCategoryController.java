package jerin.com.example.expense_tracker.controller;


import jakarta.validation.Valid;
import jerin.com.example.expense_tracker.model.ExpenseCategory;
import jerin.com.example.expense_tracker.repository.ExpenseCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class ExpenseCategoryController {
    private final ExpenseCategoryRepository categoryRepository;

    public ExpenseCategoryController(ExpenseCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<ExpenseCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCategory> getCategoryById(@PathVariable Long id) {
        Optional<ExpenseCategory> category = categoryRepository.findById(id);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody ExpenseCategory category) {
        if (categoryRepository.existsByName(category.getName())) {
            return ResponseEntity.badRequest().body("Category name already exists");
        }
        ExpenseCategory savedCategory = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody ExpenseCategory categoryDetails) {
        return categoryRepository.findById(id)
                .map(category -> {
                    if (!category.getName().equals(categoryDetails.getName())) {
                        if (categoryRepository.existsByName(categoryDetails.getName())) {
                            return ResponseEntity.badRequest().body("Category name already exists");
                        }
                    }
                    category.setName(categoryDetails.getName());
                    category.setDescription(categoryDetails.getDescription());
                    ExpenseCategory updatedCategory = categoryRepository.save(category);
                    return ResponseEntity.ok(updatedCategory);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}