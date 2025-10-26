package com.banco.operacionesbancarias.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.banco.operacionesbancarias.domain.model.Cuenta;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

	Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

	boolean existsByNumeroCuenta(String numeroCuenta);

	List<Cuenta> findByClienteId(String clienteId);

	@Modifying
	@Query("UPDATE Cuenta c SET c.estado = false WHERE c.clienteId = :clienteId")
	void marcarCuentasInactivas(@Param("clienteId") String clienteId);

	@Modifying
	@Query("UPDATE Cuenta c SET c.estado = true WHERE c.clienteId = :clienteId")
	void marcarCuentasActivas(String clienteId);
}
