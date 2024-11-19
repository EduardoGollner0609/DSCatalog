package com.eduardo.dscatalog.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.eduardo.dscatalog.dto.UserDTO;
import com.eduardo.dscatalog.dto.UserInsertDTO;
import com.eduardo.dscatalog.dto.UserUpdateDTO;
import com.eduardo.dscatalog.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping
	public ResponseEntity<Page<UserDTO>> findAllPaged(Pageable pageable) {
		return ResponseEntity.ok(service.findAllPaged(pageable));
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@PostMapping
	public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO userDTO) {
		UserDTO newDTO = service.insert(userDTO);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newDTO.getId())
				.toUri();
		return ResponseEntity.created(uri).body(newDTO);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userDTO) {
		return ResponseEntity.ok(service.update(id, userDTO));
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
