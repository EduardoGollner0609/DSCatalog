package com.eduardo.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.dscatalog.dto.RoleDTO;
import com.eduardo.dscatalog.dto.UserDTO;
import com.eduardo.dscatalog.dto.UserInsertDTO;
import com.eduardo.dscatalog.dto.UserUpdateDTO;
import com.eduardo.dscatalog.entities.Role;
import com.eduardo.dscatalog.entities.User;
import com.eduardo.dscatalog.projections.UserDetailsProjection;
import com.eduardo.dscatalog.repositories.RoleRepository;
import com.eduardo.dscatalog.repositories.UserRepository;
import com.eduardo.dscatalog.services.exceptions.DatabaseException;
import com.eduardo.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AuthService authService;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		return repository.findAll(pageable).map(user -> new UserDTO(user));
	}

	@Transactional(readOnly = true)
	public UserDTO findMe() {
		User user = authService.authenticated();
		return new UserDTO(user);
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));
		return new UserDTO(user);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO userDTO) {
		User user = new User();
		copyDtoToEntity(user, userDTO);

		user.getRoles().clear();
		Role role = roleRepository.findByAuthority("ROLE_OPERATOR");
		user.getRoles().add(role);
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		return new UserDTO(repository.save(user));
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO userDTO) {
		try {
			User user = repository.getReferenceById(id);
			copyDtoToEntity(user, userDTO);
			return new UserDTO(user);
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

	private void copyDtoToEntity(User user, UserDTO userDTO) {
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());

		user.getRoles().clear();

		for (RoleDTO roleDTO : userDTO.getRoles()) {
			Role role = roleRepository.getReferenceById(roleDTO.getId());
			user.getRoles().add(role);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);

		if (result.size() == 0) {
			throw new UsernameNotFoundException("Email not found");
		}

		User user = new User();
		user.setEmail(result.get(0).getUsername());
		user.setPassword(result.get(0).getPassword());

		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}

		return user;
	}
}
