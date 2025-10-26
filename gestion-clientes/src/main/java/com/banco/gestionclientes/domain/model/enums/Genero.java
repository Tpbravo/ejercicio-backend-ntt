package com.banco.gestionclientes.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Genero {
	M("Masculino"), F("Femenino"), O("Otro");

	private final String etiqueta;

	Genero(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	@JsonValue
	public String getEtiqueta() {
		return etiqueta;
	}

	/**
	 * Permite convertir valores de texto del JSON (por ejemplo "masculino",
	 * "MASCULINO", "Masculino") al enum correcto.
	 */
	@JsonCreator
	public static Genero desdeTexto(String valor) {
		if (valor == null) {
			return null;
		}
		String normalizado = valor.trim().toLowerCase();
		for (Genero g : values()) {
			if (g.etiqueta.toLowerCase().equals(normalizado) || g.name().toLowerCase().equals(normalizado)) {
				return g;
			}
		}
		throw new IllegalArgumentException("Valor de género inválido: " + valor);
	}
}