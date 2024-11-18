package com.eduardo.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduardo.dscatalog.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
