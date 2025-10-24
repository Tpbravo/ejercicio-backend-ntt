package com.banco.gestionclientes.application;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banco.gestionclientes.domain.model.Cliente;
import com.banco.gestionclientes.domain.service.ClienteService;
import com.banco.gestionclientes.infrastructure.persistence.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
	private final ClienteRepository repository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Cliente guardar(Cliente cliente) {
		cliente.setContrasena(passwordEncoder.encode(cliente.getContrasena()));
		return repository.save(cliente);
	}

	@Override
	public List<Cliente> listar() {
		return repository.findAll();
	}

	@Override
	public Optional<Cliente> buscarPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	public Cliente actualizar(Long id, Cliente cliente) {
		return repository.findById(id).map(existing -> {
			// Campos heredados de Persona
			existing.setNombre(cliente.getNombre());
			existing.setGenero(cliente.getGenero());
			existing.setEdad(cliente.getEdad());
			existing.setIdentificacion(cliente.getIdentificacion());
			existing.setDireccion(cliente.getDireccion());
			existing.setTelefono(cliente.getTelefono());

			// Campos propios de Cliente
			existing.setClienteId(cliente.getClienteId());
			existing.setEstado(cliente.getEstado());
			if (cliente.getContrasena() != null && !cliente.getContrasena().isBlank()) {
				existing.setContrasena(passwordEncoder.encode(cliente.getContrasena()));
			}

			return repository.save(existing);
		}).orElseThrow(() -> new RuntimeException("Cliente no encontrado con id " + id));
	}

	@Override
	public void eliminar(Long id) {
		repository.deleteById(id);
	}
}
