package com.eduardo.dscatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.dscatalog.dto.CategoryDTO;
import com.eduardo.dscatalog.entities.Category;
import com.eduardo.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		return repository.findAll(pageable).map(category -> new CategoryDTO(category));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Category category = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));
		return new CategoryDTO(category);
	}

}
