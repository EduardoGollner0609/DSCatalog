package com.eduardo.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduardo.dscatalog.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
