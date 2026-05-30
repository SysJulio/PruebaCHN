package gt.com.chn.prestamos.repositories;

import gt.com.chn.prestamos.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio encargado de la gestión de acceso a datos para la entidad
 * Client (Cliente).
 *
 * Extiende JpaRepository para proporcionar automáticamente operaciones
 * CRUD (Crear, Leer, Actualizar y Eliminar) sobre la tabla de clientes.
 *
 * El tipo Client representa la entidad gestionada y Long corresponde
 * al tipo de dato de su llave primaria (ID).
 */

public interface ClientRepository extends JpaRepository<Client, Long> {
}
