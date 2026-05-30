package gt.com.chn.prestamos.repositories;

import gt.com.chn.prestamos.models.ApprovedLoan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio encargado de la gestión de acceso a datos para la entidad
 * ApprovedLoan (Préstamo Aprobado).
 *
 * Extiende JpaRepository, proporcionando operaciones CRUD básicas como:
 * guardar, actualizar, eliminar, buscar por ID y listar registros.
 */


public interface ApprovedLoanRepository extends JpaRepository<ApprovedLoan, Long> {
    List<ApprovedLoan> findByApplicationClientIdOrderByApprovalDateDesc(Long clientId);
}
