package com.banco.operacionesbancarias.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.service.CuentaService;
import com.banco.operacionesbancarias.infrastructure.persistence.CuentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaServiceImpl implements CuentaService {

	private final CuentaRepository cuentaRepository;

	@Override
	public Cuenta crear(Cuenta cuenta) {
		if (cuentaRepository.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
			throw new RuntimeException("El número de cuenta ya existe");
		}
		return cuentaRepository.save(cuenta);
	}

	@Override
	public List<Cuenta> listar() {
		return cuentaRepository.findAll();
	}

	@Override
	public Optional<Cuenta> buscarPorId(Long id) {
		return cuentaRepository.findById(id);
	}

	@Override
	public Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
		return cuentaRepository.findByNumeroCuenta(numeroCuenta);
	}

	@Override
	public Cuenta actualizar(Long id, Cuenta cuentaActualizada) {
		return cuentaRepository.findById(id).map(cuenta -> {
			cuenta.setTipoCuenta(cuentaActualizada.getTipoCuenta());
			cuenta.setEstado(cuentaActualizada.getEstado());
			return cuentaRepository.save(cuenta);
		}).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
	}

	@Override
	public void eliminar(Long id) {
		cuentaRepository.deleteById(id);
	}
}
