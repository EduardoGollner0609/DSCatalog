package com.eduardo.dscatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eduardo.dscatalog.dto.UserDTO;
import com.eduardo.dscatalog.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	public Page<UserDTO> findAllPaged(Pageable pageable) {
		return repository.findAll(pageable).map(user -> new UserDTO(user));
	}
}
