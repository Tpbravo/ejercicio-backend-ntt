package com.banco.gestionclientes.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banco.gestionclientes.domain.model.Cliente;
import com.banco.gestionclientes.domain.service.ClienteService;
import com.banco.gestionclientes.infrastructure.messaging.ClienteEventProducer;
import com.banco.gestionclientes.infrastructure.persistence.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
	private final ClienteRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final ClienteEventProducer clienteEventProducer;

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
			boolean estabaActivo = existing.getEstado();

			// Solo actualizamos los campos que vienen informados (no nulos)
			if (cliente.getNombre() != null && !cliente.getNombre().isBlank()) {
				existing.setNombre(cliente.getNombre());
			}

			if (cliente.getGenero() != null) {
				existing.setGenero(cliente.getGenero());
			}

			if (cliente.getEdad() != null) {
				existing.setEdad(cliente.getEdad());
			}

			if (cliente.getIdentificacion() != null && !cliente.getIdentificacion().isBlank()) {
				existing.setIdentificacion(cliente.getIdentificacion());
			}

			if (cliente.getDireccion() != null && !cliente.getDireccion().isBlank()) {
				existing.setDireccion(cliente.getDireccion());
			}

			if (cliente.getTelefono() != null && !cliente.getTelefono().isBlank()) {
				existing.setTelefono(cliente.getTelefono());
			}

			if (cliente.getEstado() != null) {
				existing.setEstado(cliente.getEstado());
			}

			// Manejo seguro de la contraseña
			if (cliente.getContrasena() != null && !cliente.getContrasena().isBlank()) {

				if (passwordEncoder.matches(cliente.getContrasena(), existing.getContrasena())) {
					throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la actual");
				}

				if (existing.getContrasenaAnterior() != null
						&& passwordEncoder.matches(cliente.getContrasena(), existing.getContrasenaAnterior())) {
					throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior");
				}

				existing.setContrasenaAnterior(existing.getContrasena());
				existing.setContrasena(passwordEncoder.encode(cliente.getContrasena()));
			}

			Cliente actualizado = repository.save(existing);

			boolean ahoraActivo = actualizado.getEstado();

			// Emitir eventos solo si el estado cambió
			if (estabaActivo && !ahoraActivo) {
				LOG.info("Publicando evento CLIENTE_DESACTIVADO para {}", actualizado.getClienteId());
				clienteEventProducer.publicarClienteDesactivado(actualizado.getClienteId());
			} else if (!estabaActivo && ahoraActivo) {
				LOG.info("Publicando evento CLIENTE_ACTIVADO para {}", actualizado.getClienteId());
				clienteEventProducer.publicarClienteActivado(actualizado.getClienteId());
			}

			return actualizado;
		}).orElseThrow(() -> new RuntimeException("Cliente no encontrado con id " + id));

	}

	@Override
	public void eliminar(Long id) {
		LOG.warn("Intentando eliminar cliente con id={}", id);

		repository.findById(id).ifPresentOrElse(cliente -> {
			LOG.info("Cliente encontrado: {}. Publicando evento de eliminación...", cliente.getClienteId());
			clienteEventProducer.publicarClienteEliminado(cliente.getClienteId());
			repository.delete(cliente);
			LOG.info("Cliente eliminado correctamente: {}", cliente.getClienteId());
		}, () -> {
			LOG.error("No se encontró el cliente con id={} para eliminar", id);
			throw new ResourceNotFoundException("Cliente no encontrado con id " + id);
		});
	}

	private String generarClienteId() {
		return "CLI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	@Override
	public Optional<Cliente> buscarPorClienteId(String clienteId) {
		return repository.findByClienteId(clienteId);

	}

	@Override
	public Cliente buscarPorIdOrThrow(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + id));
	}

}
