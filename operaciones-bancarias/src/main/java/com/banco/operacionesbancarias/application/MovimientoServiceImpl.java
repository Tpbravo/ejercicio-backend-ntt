package com.banco.operacionesbancarias.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banco.operacionesbancarias.api.dto.MovimientoDTO;
import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.model.Movimiento;
import com.banco.operacionesbancarias.domain.model.enums.TipoMovimientoEnum;
import com.banco.operacionesbancarias.domain.service.MovimientoService;
import com.banco.operacionesbancarias.infrastructure.persistence.CuentaRepository;
import com.banco.operacionesbancarias.infrastructure.persistence.MovimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoServiceImpl implements MovimientoService {

	private final MovimientoRepository movimientoRepository;
	private final CuentaRepository cuentaRepository;

	@Override
	public Movimiento registrarMovimiento(MovimientoDTO dto) {

		// Buscar cuenta
		Cuenta cuenta = cuentaRepository.findByNumeroCuenta(dto.getNumeroCuenta())
				.orElseThrow(() -> new RuntimeException("Cuenta no encontrada con número " + dto.getNumeroCuenta()));

		// Verificar si la cuenta está activa
		if (Boolean.FALSE.equals(cuenta.getEstado())) {
			throw new RuntimeException("No se pueden registrar movimientos en una cuenta inactiva");
		}

		// 1) obtener saldo base (último movimiento o saldoInicial)
		BigDecimal saldoBase = movimientoRepository.findTopByCuentaIdOrderByFechaDesc(cuenta.getId())
				.map(Movimiento::getSaldo).orElse(cuenta.getSaldoInicial());

		// 2) normalizar signo segun tipo
		BigDecimal valor = dto.getValor();
		if (dto.getTipoMovimiento() == TipoMovimientoEnum.RETIRO && valor.signum() > 0) {
			valor = valor.negate();
		} else if (dto.getTipoMovimiento() == TipoMovimientoEnum.DEPOSITO && valor.signum() < 0) {
			valor = valor.abs();
		}

		// 3) calcular saldo resultante
		BigDecimal nuevoSaldo = saldoBase.add(valor);
		if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
			throw new RuntimeException("Saldo no disponible para realizar la operación");
		}

		// 4) crear movimiento con el SALDO resultante (campo "saldo" del PDF)
		Movimiento mov = Movimiento.builder().fecha(LocalDateTime.now()).tipoMovimiento(dto.getTipoMovimiento())
				.valor(valor).saldo(nuevoSaldo) //
				.cuenta(cuenta).build();

		return movimientoRepository.save(mov);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Movimiento> listarPorCuenta(Long cuentaId) {
		Cuenta cuenta = cuentaRepository.findById(cuentaId)
				.orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
		return movimientoRepository.findByCuentaOrderByFechaDesc(cuenta);
	}

	@Override
	public Movimiento actualizarMovimiento(Long id, MovimientoDTO dto) {
		Movimiento movimiento = movimientoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado"));

		Long cuentaId = movimiento.getCuenta().getId();

		// Obtener todos los movimientos ordenados por fecha descendente
		List<Movimiento> movimientos = movimientoRepository.findByCuentaIdOrderByFechaDesc(cuentaId);

		if (movimientos.isEmpty()) {
			throw new IllegalStateException("No existen movimientos para esta cuenta");
		}

		// Si NO es el último y tampoco el único movimiento, bloquear actualización
		Movimiento ultimoMovimiento = movimientos.get(0);
		if (!ultimoMovimiento.getId().equals(movimiento.getId()) && movimientos.size() > 1) {
			throw new IllegalStateException("Solo se puede actualizar el último o el único movimiento de la cuenta");
		}

		// Actualizar campos válidos
		movimiento.setTipoMovimiento(dto.getTipoMovimiento());
		movimiento.setValor(dto.getValor());
		movimiento.setFecha(dto.getFecha());

		return movimientoRepository.save(movimiento);
	}

	@Override
	public void eliminarMovimiento(Long id) {
		Movimiento movimiento = movimientoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado"));

		Long cuentaId = movimiento.getCuenta().getId();

		List<Movimiento> movimientos = movimientoRepository.findByCuentaIdOrderByFechaDesc(cuentaId);

		if (movimientos.isEmpty()) {
			throw new IllegalStateException("No existen movimientos para esta cuenta");
		}

		Movimiento ultimoMovimiento = movimientos.get(0);
		if (!ultimoMovimiento.getId().equals(movimiento.getId()) && movimientos.size() > 1) {
			throw new IllegalStateException("Solo se puede eliminar el último o el único movimiento de la cuenta");
		}

		movimientoRepository.delete(movimiento);
	}
}
