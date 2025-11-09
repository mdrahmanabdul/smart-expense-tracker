package com.smartexpense.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.smartexpense.entities.Expense;
import com.smartexpense.entities.UserEntity;
import com.smartexpense.repositories.ExpenseRepository;
import com.smartexpense.repositories.UserRepository;

@Service
public class ExpenseService {

	private ExpenseRepository expenseRepo;
	private UserRepository userRepo;
	
	public ExpenseService(ExpenseRepository expenseRepo, UserRepository userRepo) {
		this.expenseRepo=expenseRepo;
		this.userRepo=userRepo;
	}
	
	public Expense addExpense(Expense expense) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        expense.setUser(user);
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }
        return expenseRepo.save(expense);
    }
	
	public List<Expense> getUserExpenses() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return expenseRepo.findByUser(user);
    }

	public void deleteExpense(Long id) {
        Expense expense = expenseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if (!expense.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You cannot delete someone else’s expense!");
        }

        expenseRepo.delete(expense);
    }

}
