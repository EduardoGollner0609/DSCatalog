package com.eduardo.dscatalog.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.dscatalog.dto.CategoryDTO;
import com.eduardo.dscatalog.dto.ProductDTO;
import com.eduardo.dscatalog.entities.Category;
import com.eduardo.dscatalog.entities.Product;
import com.eduardo.dscatalog.projections.ProductProjection;
import com.eduardo.dscatalog.repositories.CategoryRepository;
import com.eduardo.dscatalog.repositories.ProductRepository;
import com.eduardo.dscatalog.services.exceptions.DatabaseException;
import com.eduardo.dscatalog.services.exceptions.ResourceNotFoundException;
import com.eduardo.dscatalog.utils.Utils;

import jakarta.persistence.EntityNotFoundException;

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
	public Page<ProductDTO> findAllPaged(String categoryId, String name, Pageable paegeable) {
		List<Long> categoryIds = Arrays.asList();

		if (!"0".equals(categoryId)) {
			categoryIds = Arrays.asList(categoryId.split(",")).stream().map(Long::parseLong).toList();
		}

		Page<ProductProjection> page = repository.searchProducts(categoryIds, name, paegeable);
		List<Long> productIds = page.map(x -> x.getId()).toList();
		List<Product> entities = repository.searchProductsWithCategories(productIds);
		entities = Utils.replace(page.getContent(), entities);
		return new PageImpl<>(entities.stream().map(x -> new ProductDTO(x, x.getCategories())).toList(),
				page.getPageable(), page.getTotalElements());
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

	@Transactional
	public ProductDTO update(Long id, ProductDTO productDTO) {
		try {
			Product product = repository.getReferenceById(id);
			copyDtoToEntity(product, productDTO);
			return new ProductDTO(repository.save(product));
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id Not Found " + id);
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Referential integrity failure");
		}
	}

	private void copyDtoToEntity(Product product, ProductDTO productDTO) {
		product.setName(productDTO.getName());
		product.setPrice(productDTO.getPrice());
		product.setImgUrl(productDTO.getImgUrl());
		product.setDescription(productDTO.getDescription());
		product.setDate(productDTO.getDate());

		product.getCategories().clear();

		for (CategoryDTO categoryDTO : productDTO.getCategories()) {
			Category category = categoryRepository.getReferenceById(categoryDTO.getId());
			product.getCategories().add(category);
		}
	}
}
