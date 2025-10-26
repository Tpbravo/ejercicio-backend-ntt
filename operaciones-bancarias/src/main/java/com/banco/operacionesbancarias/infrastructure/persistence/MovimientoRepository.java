package com.banco.operacionesbancarias.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banco.operacionesbancarias.domain.model.Movimiento;
import com.banco.operacionesbancarias.domain.model.Cuenta;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

	List<Movimiento> findByCuentaOrderByFechaDesc(Cuenta cuenta);

	List<Movimiento> findByCuentaAndFechaBetweenOrderByFechaAsc(Cuenta cuenta, LocalDateTime fechaInicio,
			LocalDateTime fechaFin);

	List<Movimiento> findByCuentaIdOrderByFechaDesc(Long cuentaId);

	Optional<Movimiento> findTopByCuentaIdOrderByFechaDesc(Long cuentaId);

	void deleteAllByCuentaId(Long cuentaId);

	boolean existsByCuentaId(Long cuentaId);

}
