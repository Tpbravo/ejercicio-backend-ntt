package com.banco.operacionesbancarias.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banco.operacionesbancarias.domain.model.Movimiento;
import com.banco.operacionesbancarias.domain.model.Cuenta;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaOrderByFechaDesc(Cuenta cuenta);
}
