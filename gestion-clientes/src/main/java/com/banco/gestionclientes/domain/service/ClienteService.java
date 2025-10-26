package com.banco.gestionclientes.domain.service;

import java.util.List;
import java.util.Optional;

import com.banco.gestionclientes.domain.model.Cliente;

public interface ClienteService {
	Cliente guardar(Cliente cliente);

	List<Cliente> listar();

	Optional<Cliente> buscarPorId(Long id);

	Cliente actualizar(Long id, Cliente cliente);

	void eliminar(Long id);

	Optional<Cliente> buscarPorClienteId(String clienteId);

	Cliente buscarPorIdOrThrow(Long id);
}
