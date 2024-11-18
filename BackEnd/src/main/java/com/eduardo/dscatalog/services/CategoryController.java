package com.eduardo.dscatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class CategoryController {

	@Autowired
	private CategoryService service;

}
