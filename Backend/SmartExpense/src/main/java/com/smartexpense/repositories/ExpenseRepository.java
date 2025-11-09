package com.smartexpense.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartexpense.entities.Expense;
import com.smartexpense.entities.UserEntity;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>{

	List<Expense> findByUser(UserEntity user);

}
