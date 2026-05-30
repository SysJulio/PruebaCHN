package gt.com.chn.prestamos.repositories;

import gt.com.chn.prestamos.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio encargado de la gestión de acceso a datos para la entidad
 * Payment (Pago).
 *
 * Extiende JpaRepository para proporcionar automáticamente las operaciones
 * CRUD (Crear, Leer, Actualizar y Eliminar) sobre los registros de pagos.
 *
 * El tipo Payment representa la entidad gestionada y Long corresponde
 * al tipo de dato de la clave primaria (ID) de la entidad.
 */


public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
