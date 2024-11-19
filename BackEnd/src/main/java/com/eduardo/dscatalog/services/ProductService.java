package com.eduardo.dscatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.dscatalog.dto.CategoryDTO;
import com.eduardo.dscatalog.dto.ProductDTO;
import com.eduardo.dscatalog.entities.Category;
import com.eduardo.dscatalog.entities.Product;
import com.eduardo.dscatalog.repositories.CategoryRepository;
import com.eduardo.dscatalog.repositories.ProductRepository;
import com.eduardo.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		return repository.findAll(pageable).map(product -> new ProductDTO(product));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Product product = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));
		return new ProductDTO(product, product.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO productDTO) {
		Product product = new Product();
		copyDtoToEntity(product, productDTO);
		return new ProductDTO(repository.save(product));
	}

	private void copyDtoToEntity(Product product, ProductDTO productDTO) {
		product.setName(productDTO.getName());
		product.setPrice(productDTO.getPrice());
		product.setImgUrl(productDTO.getImgUrl());
		product.setDescription(productDTO.getDescription());
		product.setDate(productDTO.getDate());


		for (CategoryDTO categoryDTO : productDTO.getCategories()) {
			Category category = categoryRepository.getReferenceById(categoryDTO.getId());
			product.getCategories().add(category);
		}
	}
}
