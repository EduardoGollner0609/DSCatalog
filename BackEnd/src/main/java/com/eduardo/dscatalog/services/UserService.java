package com.eduardo.dscatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.dscatalog.dto.UserDTO;
import com.eduardo.dscatalog.entities.User;
import com.eduardo.dscatalog.repositories.UserRepository;
import com.eduardo.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		return repository.findAll(pageable).map(user -> new UserDTO(user));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));
		return new UserDTO(user);
	}

	@Transactional
	public UserDTO insert(UserDTO UserDTO) {
		User user = new User();
		copyDtoToEntity(user, UserDTO);
		return new UserDTO(repository.save(user));
	}

	@Transactional
	public UserDTO update(Long id, UserDTO userDTO) {
		try {
			User user = repository.getReferenceById(id);
			copyDtoToEntity(user, userDTO);
			return new UserDTO(user);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id Not Found " + id);
		}
	}

	private void copyDtoToEntity(User user, UserDTO userDTO) {
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user.setPassword(userDTO.getPassword());
	}
}
