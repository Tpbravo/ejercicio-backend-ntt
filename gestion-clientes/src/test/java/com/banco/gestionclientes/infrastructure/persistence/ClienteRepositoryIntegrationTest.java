package com.banco.gestionclientes.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.banco.gestionclientes.domain.model.Cliente;
import com.banco.gestionclientes.domain.model.enums.Genero;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ComponentScan(basePackages = "com.banco.gestionclientes.infrastructure.persistence", excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = org.springframework.stereotype.Service.class))
@DisplayName("Integración pura JPA - ClienteRepository")
class ClienteRepositoryIntegrationTest {

	@Autowired
	private ClienteRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	@DisplayName("Debe guardar y recuperar un cliente por clienteId")
	void debeGuardarYRecuperarCliente() {
		Cliente cliente = new Cliente();
		cliente.setClienteId("CLI-999");
		cliente.setContrasena("secret123");
		cliente.setEstado(true);
		cliente.setNombre("Pedro Gómez");
		cliente.setGenero(Genero.M);
		cliente.setEdad(35);
		cliente.setIdentificacion("DOC123");
		cliente.setDireccion("Calle 1");
		cliente.setTelefono("123456");

		repository.save(cliente);

		Optional<Cliente> resultado = repository.findByClienteId("CLI-999");
		assertTrue(resultado.isPresent());
		assertEquals("Pedro Gómez", resultado.get().getNombre());
	}

	@Test
	@DisplayName("Debe lanzar excepción si el clienteId es duplicado")
	void debeFallarPorClienteIdDuplicado() {
		Cliente c1 = new Cliente();
		c1.setClienteId("CLI-123");
		c1.setContrasena("password1");
		c1.setEstado(true);
		c1.setNombre("Ana");
		c1.setGenero(Genero.F);
		c1.setEdad(25);
		c1.setIdentificacion("ID1");
		repository.save(c1);

		Cliente c2 = new Cliente();
		c2.setClienteId("CLI-123");
		c2.setContrasena("password2");
		c2.setEstado(true);
		c2.setNombre("Bea");
		c2.setGenero(Genero.F);
		c2.setEdad(26);
		c2.setIdentificacion("ID2");

		assertThrows(DataIntegrityViolationException.class, () -> {
			repository.saveAndFlush(c2);
			entityManager.flush();
		});
	}
}