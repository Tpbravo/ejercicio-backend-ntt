package com.banco.operacionesbancarias.domain.service;

import java.util.List;
import java.util.Optional;

import com.banco.operacionesbancarias.domain.model.Cuenta;

public interface CuentaService {
	Cuenta crear(Cuenta cuenta);

	List<Cuenta> listar();

	Optional<Cuenta> buscarPorId(Long id);

	Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta);

	Cuenta actualizar(Long id, Cuenta cuenta);

	void eliminar(Long id);

	void inactivarCuentasDeCliente(String clienteId);

	void activarCuentasDeCliente(String clienteId);

	void eliminarCuentasDeCliente(String clienteId);

	void enriquecerConNombreCliente(Cuenta cuenta);
	
	
}
