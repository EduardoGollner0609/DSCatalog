package com.eduardo.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduardo.dscatalog.dto.CategoryDTO;
import com.eduardo.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	public List<CategoryDTO> findAll() {
		return repository.findAll().stream().map(category -> new CategoryDTO(category)).toList();
	}
}
