package com.banco.operacionesbancarias.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banco.operacionesbancarias.api.dto.ClienteDTO;
import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.service.CuentaService;
import com.banco.operacionesbancarias.infrastructure.client.ClienteServiceClient;
import com.banco.operacionesbancarias.infrastructure.persistence.CuentaRepository;
import com.banco.operacionesbancarias.infrastructure.persistence.MovimientoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CuentaServiceImpl implements CuentaService {

	private final CuentaRepository cuentaRepository;
	private final MovimientoRepository movimientoRepository;
	private final ClienteServiceClient clienteServiceClient;

	@Override
	public Cuenta crear(Cuenta cuenta) {
		// Validar duplicados
		if (cuentaRepository.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
			throw new RuntimeException("El número de cuenta ya existe");
		}

		// Validar cliente externo
		ClienteDTO cliente = clienteServiceClient.obtenerClientePorClienteId(cuenta.getClienteId()).block();
		if (cliente == null) {
			throw new RuntimeException("No se encontró el cliente con ID: " + cuenta.getClienteId());
		}

		// Enriquecer información de cuenta
		cuenta.setClienteNombre(cliente.getNombre());
		if (cuenta.getSaldoInicial() == null) {
			cuenta.setSaldoInicial(BigDecimal.ZERO);
		}

		// Guardar cuenta sin registrar movimiento inicial
		Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
		log.info("Cuenta {} creada con saldo inicial de {}", cuentaGuardada.getNumeroCuenta(),
				cuentaGuardada.getSaldoInicial());

		return cuentaGuardada;

	}

	@Override
	public List<Cuenta> listar() {
		List<Cuenta> cuentas = cuentaRepository.findAll();
		cuentas.forEach(this::enriquecerConNombreCliente);
		return cuentas;
	}

	@Override
	public Optional<Cuenta> buscarPorId(Long id) {
		return cuentaRepository.findById(id).map(cuenta -> {
			enriquecerConNombreCliente(cuenta);
			return cuenta;
		});
	}

	@Override
	public Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
		return cuentaRepository.findByNumeroCuenta(numeroCuenta).map(cuenta -> {
			enriquecerConNombreCliente(cuenta);
			return cuenta;
		});
	}

	@Override
	public Cuenta actualizar(Long id, Cuenta cuentaActualizada) {
		Cuenta cuentaExistente = cuentaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

		// Validar campos inmutables
		if (!cuentaExistente.getNumeroCuenta().equals(cuentaActualizada.getNumeroCuenta())) {
			throw new RuntimeException("El número de cuenta no puede ser modificado");
		}

		if (!cuentaExistente.getClienteId().equals(cuentaActualizada.getClienteId())) {
			throw new RuntimeException("El cliente asociado no puede ser modificado");
		}

		// Verificar si hay movimientos antes de modificar el saldo inicial
		boolean tieneMovimientos = movimientoRepository.existsByCuentaId(cuentaExistente.getId());
		if (tieneMovimientos && cuentaActualizada.getSaldoInicial() != null
				&& cuentaActualizada.getSaldoInicial().compareTo(cuentaExistente.getSaldoInicial()) != 0) {
			throw new RuntimeException("No se puede modificar el saldo inicial de una cuenta con movimientos");
		}

		// Reemplazar campos mutables
		cuentaExistente.setTipoCuenta(cuentaActualizada.getTipoCuenta());
		cuentaExistente.setEstado(cuentaActualizada.getEstado());

		if (!tieneMovimientos && cuentaActualizada.getSaldoInicial() != null) {
			cuentaExistente.setSaldoInicial(cuentaActualizada.getSaldoInicial());
		}

		return cuentaRepository.save(cuentaExistente);
	}

	@Override
	public void eliminar(Long id) {
		cuentaRepository.findById(id).ifPresentOrElse(cuenta -> {
			log.info("Eliminando movimientos asociados a la cuenta {}", cuenta.getNumeroCuenta());
			movimientoRepository.deleteAllByCuentaId(cuenta.getId());

			log.info("Eliminando cuenta {}", cuenta.getNumeroCuenta());
			cuentaRepository.delete(cuenta);

			log.info("Cuenta {} y sus movimientos asociados eliminados correctamente", cuenta.getNumeroCuenta());
		}, () -> {
			throw new RuntimeException("No se encontró la cuenta con id " + id);
		});
	}

	@Override
	public void inactivarCuentasDeCliente(String clienteId) {
		cuentaRepository.marcarCuentasInactivas(clienteId);
	}

	@Override
	public void activarCuentasDeCliente(String clienteId) {
		cuentaRepository.marcarCuentasActivas(clienteId);
	}

	@Override
	public void eliminarCuentasDeCliente(String clienteId) {
		List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);

		if (cuentas.isEmpty()) {
			log.warn("No se encontraron cuentas para el cliente {}", clienteId);
			return;
		}

		log.info("Eliminando {} cuentas del cliente {}", cuentas.size(), clienteId);

		cuentas.forEach(cuenta -> eliminar(cuenta.getId()));
	}
	@Override
	public void enriquecerConNombreCliente(Cuenta cuenta) {
		try {
			ClienteDTO cliente = clienteServiceClient.obtenerClientePorClienteId(cuenta.getClienteId()).block();

			if (cliente != null) {
				cuenta.setClienteNombre(cliente.getNombre());
			} else {
				cuenta.setClienteNombre("(Cliente no encontrado)");
				log.warn("No se encontró cliente con ID {}", cuenta.getClienteId());
			}
		} catch (Exception e) {
			cuenta.setClienteNombre("(Error al consultar cliente)");
			log.error("Error obteniendo nombre para clienteId {}: {}", cuenta.getClienteId(), e.getMessage());
		}
	}
}
