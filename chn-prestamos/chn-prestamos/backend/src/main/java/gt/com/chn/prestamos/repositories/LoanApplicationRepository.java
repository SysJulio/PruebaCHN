package gt.com.chn.prestamos.repositories;

import gt.com.chn.prestamos.models.LoanApplication;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio encargado de la gestión de acceso a datos para la entidad
 * LoanApplication (Solicitud de Préstamo).
 *
 * Extiende JpaRepository para proporcionar automáticamente las operaciones
 * básicas de persistencia como crear, consultar, actualizar y eliminar
 * registros de solicitudes de préstamo.
 */


public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByClientIdOrderByCreatedAtDesc(Long clientId);
}
