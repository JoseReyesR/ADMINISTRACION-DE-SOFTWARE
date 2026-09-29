package reclamostottus.reclamos_backend.integration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Siembra las tablas de catálogo (roles, categorías, prioridades, estados,
 * tiendas, motivos) SOLO para las pruebas contra H2. En producción (MySQL)
 * estas filas ya existen — fueron insertadas a mano por el equipo de BD — y
 * el código de la app (SetupDataLoader, ReclamoService, etc.) solo referencia
 * sus IDs, nunca las crea.
 *
 * Se implementa como un CommandLineRunner (JDBC puro) en lugar de un
 * data.sql, porque un CommandLineRunner siempre se ejecuta después de que el
 * contexto de Spring —y el esquema de Hibernate— ya están completamente
 * inicializados, sin depender del orden (a veces inconsistente entre
 * versiones de Spring Boot) de spring.jpa.defer-datasource-initialization.
 *
 * @Order(1) garantiza que esto corra ANTES que SetupDataLoader (que no tiene
 * @Order, así que usa la prioridad más baja posible) y así los usuarios que
 * este último crea puedan referenciar un rol_id que ya existe.
 */
@Configuration
public class CatalogoSeedRunner {

    @Bean
    @Order(1)
    CommandLineRunner sembrarCatalogosDePrueba(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.update("INSERT INTO roles (id, nombre) VALUES (1, 'ROLE_CLIENTE')");
            jdbcTemplate.update("INSERT INTO roles (id, nombre) VALUES (2, 'ROLE_ADMIN')");
            jdbcTemplate.update("INSERT INTO roles (id, nombre) VALUES (3, 'ROLE_INVITADO')");

            jdbcTemplate.update("INSERT INTO categorias (id, nombre) VALUES (1, 'Producto defectuoso')");
            jdbcTemplate.update("INSERT INTO categorias (id, nombre) VALUES (2, 'Mal servicio')");

            jdbcTemplate.update("INSERT INTO prioridades (id, nombre) VALUES (1, 'Baja')");
            jdbcTemplate.update("INSERT INTO prioridades (id, nombre) VALUES (2, 'Media')");
            jdbcTemplate.update("INSERT INTO prioridades (id, nombre) VALUES (3, 'Alta')");

            jdbcTemplate.update("INSERT INTO estados_reclamo (id, nombre) VALUES (1, 'Registrado')");
            jdbcTemplate.update("INSERT INTO estados_reclamo (id, nombre) VALUES (2, 'En Proceso')");
            jdbcTemplate.update("INSERT INTO estados_reclamo (id, nombre) VALUES (3, 'Resuelto')");

            jdbcTemplate.update("INSERT INTO tiendas (id, nombre) VALUES (1, 'Tottus San Miguel')");
            jdbcTemplate.update("INSERT INTO tiendas (id, nombre) VALUES (2, 'Tottus Miraflores')");

            jdbcTemplate.update("INSERT INTO catalogo_motivos (id, nombre, tipo_solicitud) VALUES (1, 'Producto dañado', 'Reclamo')");
            jdbcTemplate.update("INSERT INTO catalogo_motivos (id, nombre, tipo_solicitud) VALUES (2, 'Demora en la entrega', 'Queja')");
        };
    }
}
