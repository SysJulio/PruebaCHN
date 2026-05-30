package gt.com.chn.prestamos.controllers;

import gt.com.chn.prestamos.dto.CreateLoanApplicationRequest;
import gt.com.chn.prestamos.dto.DecisionRequest;
import gt.com.chn.prestamos.dto.PaymentRequest;
import gt.com.chn.prestamos.models.ApprovedLoan;
import gt.com.chn.prestamos.models.LoanApplication;
import gt.com.chn.prestamos.services.LoanService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controlador REST encargado de gestionar las operaciones
 * relacionadas con solicitudes de préstamos, aprobación,
 * rechazo y registro de pagos.
 */


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoanController {
    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @GetMapping("/loan-applications")
    public List<LoanApplication> applications(@RequestParam(required = false) Long clientId) {
        return service.listApplications(clientId);
    }

     /**
     * Obtiene el listado de solicitudes de préstamo.
     * Puede filtrarse por cliente utilizando el parámetro clientId.
     *
     * Endpoint:
     * GET /api/loan-applications
     *
     * @param clientId Identificador opcional del cliente.
     * @return Lista de solicitudes de préstamo.
     */
    
    @PostMapping("/loan-applications")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanApplication createApplication(@Valid @RequestBody CreateLoanApplicationRequest request) {
        return service.createApplication(request);
    }

    /**
     * Aprueba una solicitud de préstamo existente.
     *
     * Endpoint:
     * POST /api/loan-applications/{id}/approve
     *
     * @param id Identificador de la solicitud.
     * @param request Datos de aprobación.
     * @return Solicitud actualizada con estado aprobado.
     */
    

    @PostMapping("/loan-applications/{id}/approve")
    public LoanApplication approve(@PathVariable Long id, @Valid @RequestBody DecisionRequest request) {
        return service.approve(id, request);
    }

    
    /**
     * Rechaza una solicitud de préstamo existente.
     *
     * Endpoint:
     * POST /api/loan-applications/{id}/reject
     *
     * @param id Identificador de la solicitud.
     * @param request Datos de rechazo.
     * @return Solicitud actualizada con estado rechazado.
     */
    

    @PostMapping("/loan-applications/{id}/reject")
    public LoanApplication reject(@PathVariable Long id, @Valid @RequestBody DecisionRequest request) {
        return service.reject(id, request);
    }

    /**
     * Obtiene el listado de préstamos aprobados.
     * Puede filtrarse por cliente.
     *
     * Endpoint:
     * GET /api/approved-loans
     *
     * @param clientId Identificador opcional del cliente.
     * @return Lista de préstamos aprobados.
     */
    

    @GetMapping("/approved-loans")
    public List<ApprovedLoan> approvedLoans(@RequestParam(required = false) Long clientId) {
        return service.listApprovedLoans(clientId);
    }

     /**
     * Registra un pago sobre un préstamo aprobado.
     *
     * Endpoint:
     * POST /api/approved-loans/{loanId}/payments
     *
     * @param loanId Identificador del préstamo aprobado.
     * @param request Información del pago realizado.
     * @return Préstamo actualizado después de registrar el pago.
     */
    
    
    @PostMapping("/approved-loans/{loanId}/payments")
    public ApprovedLoan registerPayment(@PathVariable Long loanId, @Valid @RequestBody PaymentRequest request) {
        return service.registerPayment(loanId, request);
    }
}
