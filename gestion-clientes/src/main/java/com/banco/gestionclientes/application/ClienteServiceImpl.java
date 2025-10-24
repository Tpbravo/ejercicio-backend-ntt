package com.banco.gestionclientes.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOG = LoggerFactory.getLogger(ClienteServiceImpl.class);

	@Override
	public Cliente guardar(Cliente cliente) {
		LOG.info("Creando nuevo cliente: nombre={} identificación={}", cliente.getNombre(),
				cliente.getIdentificacion());

		if (cliente.getClienteId() == null || cliente.getClienteId().isBlank()) {
			cliente.setClienteId(generarClienteId());
		}
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
		LOG.warn("Actualizando cliente con id={}", id);
		return repository.findById(id).map(existing -> {
			// Campos heredados de Persona
			existing.setNombre(cliente.getNombre());
			existing.setGenero(cliente.getGenero());
			existing.setEdad(cliente.getEdad());
			existing.setIdentificacion(cliente.getIdentificacion());
			existing.setDireccion(cliente.getDireccion());
			existing.setTelefono(cliente.getTelefono());

			// Campos propios de Cliente
			existing.setEstado(cliente.isEstado());
			if (cliente.getContrasena() != null && !cliente.getContrasena().isBlank()) {
				existing.setContrasena(passwordEncoder.encode(cliente.getContrasena()));
			}

			return repository.save(existing);
		}).orElseThrow(() -> new RuntimeException("Cliente no encontrado con id " + id));
	}

	@Override
	public void eliminar(Long id) {
		LOG.warn("Eliminando cliente con id={}", id);
		repository.deleteById(id);
	}

	private String generarClienteId() {
		return "CLI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}
}
