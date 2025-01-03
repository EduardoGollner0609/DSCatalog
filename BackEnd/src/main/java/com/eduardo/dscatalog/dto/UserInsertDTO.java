package com.eduardo.dscatalog.dto;

import com.eduardo.dscatalog.services.validations.UserInsertValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@UserInsertValid
public class UserInsertDTO extends UserDTO {

	private static final long serialVersionUID = 1L;

	@NotBlank(message = "Campo Obrigatório")
	@Size(min = 8, message = "Deve ter no mínimo 8 caracteres")
	private String password;

	public UserInsertDTO() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
