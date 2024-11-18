package com.eduardo.dscatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eduardo.dscatalog.dto.CategoryDTO;
import com.eduardo.dscatalog.entities.Category;
import com.eduardo.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		return repository.findAll(pageable).map(category -> new CategoryDTO(category));
	}

	public CategoryDTO findById(Long id) {
		Category category = repository.findById(id).orElseThrow(() -> new RuntimeException("Resource Not Found"));
		return new CategoryDTO(category);
	}
}
