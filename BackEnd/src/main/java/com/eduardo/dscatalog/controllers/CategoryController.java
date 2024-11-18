package com.eduardo.dscatalog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.dscatalog.dto.CategoryDTO;
import com.eduardo.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

	@Autowired
	private CategoryService service;

	@GetMapping
	private ResponseEntity<Page<CategoryDTO>> findAll(Pageable pageable) {
		return ResponseEntity.ok(service.findAllPaged(pageable));
	}
}
