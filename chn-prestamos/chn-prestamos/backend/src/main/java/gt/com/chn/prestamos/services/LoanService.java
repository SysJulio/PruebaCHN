package gt.com.chn.prestamos.services;

import gt.com.chn.prestamos.models.Client;
import gt.com.chn.prestamos.repositories.ClientRepository;
import gt.com.chn.prestamos.common.NotFoundException;
import gt.com.chn.prestamos.dto.CreateLoanApplicationRequest;
import gt.com.chn.prestamos.dto.DecisionRequest;
import gt.com.chn.prestamos.dto.PaymentRequest;
import gt.com.chn.prestamos.models.ApplicationStatus;
import gt.com.chn.prestamos.models.ApprovedLoan;
import gt.com.chn.prestamos.models.LoanApplication;
import gt.com.chn.prestamos.models.Payment;
import gt.com.chn.prestamos.models.PaymentStatus;
import gt.com.chn.prestamos.models.PaymentType;
import gt.com.chn.prestamos.repositories.ApprovedLoanRepository;
import gt.com.chn.prestamos.repositories.LoanApplicationRepository;
import gt.com.chn.prestamos.repositories.PaymentRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * Servicio encargado de gestionar la lógica de negocio
 * relacionada con solicitudes de préstamos, aprobación,
 * rechazo y registro de pagos.
 */

@Service
public class LoanService {
    private final ClientRepository clients;
    private final LoanApplicationRepository applications;
    private final ApprovedLoanRepository approvedLoans;
    private final PaymentRepository payments;

    public LoanService(ClientRepository clients, LoanApplicationRepository applications,
                       ApprovedLoanRepository approvedLoans, PaymentRepository payments) {
        this.clients = clients;
        this.applications = applications;
        this.approvedLoans = approvedLoans;
        this.payments = payments;
    }

    
      /**
     * Obtiene el listado de solicitudes de préstamo.
     * Si se proporciona un cliente, filtra por dicho cliente.
     */
    

    public List<LoanApplication> listApplications(Long clientId) {
        if (clientId == null) {
            return applications.findAll();
        }
        return applications.findByClientIdOrderByCreatedAtDesc(clientId);
    }

   
   /**
     * Obtiene el listado de préstamos aprobados.
     * Si se proporciona un cliente, filtra por dicho cliente.
     */
   
   
    public List<ApprovedLoan> listApprovedLoans(Long clientId) {
        if (clientId == null) {
            return approvedLoans.findAll();
        }
        return approvedLoans.findByApplicationClientIdOrderByApprovalDateDesc(clientId);
    }

   
    /**
     * Crea una nueva solicitud de préstamo.
     * Verifica que el cliente exista antes de registrar la solicitud.
     */
   
   
    @Transactional
    public LoanApplication createApplication(CreateLoanApplicationRequest request) {
        Client client = clients.findById(request.clientId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
        LoanApplication application = new LoanApplication();
        application.setClient(client);
        application.setRequestedAmount(request.requestedAmount());
        application.setTermMonths(request.termMonths());
        application.setPurpose(request.purpose());
        return applications.save(application);
    }

    /**
     * Aprueba una solicitud de préstamo.
     * Genera automáticamente el préstamo aprobado.
     */
   

    @Transactional
    public LoanApplication approve(Long id, DecisionRequest request) {
        LoanApplication application = findApplication(id);
        requireInProcess(application);
        application.setStatus(ApplicationStatus.APROBADO);
        application.setDecisionDetails(request.details());
        application.setDecidedAt(LocalDateTime.now());

        ApprovedLoan loan = new ApprovedLoan();
        loan.setApplication(application);
        loan.setPrincipalAmount(application.getRequestedAmount());
        loan.setTermMonths(application.getTermMonths());
        loan.setAnnualInterestRate(request.annualInterestRate() == null ? BigDecimal.ZERO : request.annualInterestRate());
        application.setApprovedLoan(loan);
        return applications.save(application);
    }

     /**
     * Rechaza una solicitud de préstamo.
     */
    
    @Transactional
    public LoanApplication reject(Long id, DecisionRequest request) {
        LoanApplication application = findApplication(id);
        requireInProcess(application);
        application.setStatus(ApplicationStatus.RECHAZADO);
        application.setDecisionDetails(request.details());
        application.setDecidedAt(LocalDateTime.now());
        return applications.save(application);
    }

     /**
     * Registra un pago para un préstamo aprobado.
     * Puede ser una cuota normal o un abono a capital.
     */
    
    
    @Transactional
    public ApprovedLoan registerPayment(Long loanId, PaymentRequest request) {
        ApprovedLoan loan = approvedLoans.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Prestamo aprobado no encontrado"));
        if (loan.getPaymentStatus() == PaymentStatus.PAGADO) {
            throw new IllegalStateException("El prestamo ya se encuentra pagado");
        }
        PaymentType paymentType = request.paymentType() == null ? PaymentType.CUOTA : request.paymentType();
        BigDecimal pendingPrincipal = loan.getPendingBalance();
        BigDecimal interestApplied = paymentType == PaymentType.ABONO_CAPITAL ? BigDecimal.ZERO : calculateMonthlyInterest(loan, pendingPrincipal);
        BigDecimal principalApplied = request.amount().subtract(interestApplied).setScale(2, RoundingMode.HALF_UP);

        if (paymentType == PaymentType.ABONO_CAPITAL) {
            principalApplied = request.amount();
        }
        if (principalApplied.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("El pago no cubre el interes de la cuota");
        }
        if (request.amount().compareTo(pendingPrincipal.add(interestApplied)) > 0) {
            throw new IllegalStateException("El pago supera el saldo pendiente");
        }
        if (principalApplied.compareTo(pendingPrincipal) > 0) {
            principalApplied = pendingPrincipal;
            interestApplied = request.amount().subtract(principalApplied).max(BigDecimal.ZERO);
        }

        Payment payment = new Payment();
        payment.setLoan(loan);
        payment.setAmount(request.amount());
        payment.setPrincipalApplied(principalApplied);
        payment.setInterestApplied(interestApplied);
        payment.setPaymentType(paymentType);
        payment.setPaymentDate(request.paymentDate() == null ? LocalDate.now() : request.paymentDate());
        payment.setReceiptNumber(request.receiptNumber());
        payments.save(payment);
        loan.getPayments().add(payment);

        if (loan.getPendingBalance().compareTo(BigDecimal.ZERO) == 0) {
            loan.setPaymentStatus(PaymentStatus.PAGADO);
        }
        return approvedLoans.save(loan);
    }

     /**
     * Calcula el interés mensual sobre el saldo pendiente.
     *
     * Fórmula:
     * saldo * (tasa anual / 100 / 12)
     */
    
    
    private BigDecimal calculateMonthlyInterest(ApprovedLoan loan, BigDecimal balance) {
        BigDecimal monthlyRate = loan.getAnnualInterestRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        return balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
    }

    
    /**
     * Busca una solicitud por ID.
     * Lanza excepción si no existe.
     */
    
    private LoanApplication findApplication(Long id) {
        return applications.findById(id).orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));
    }

    
     /**
     * Verifica que la solicitud se encuentre en estado EN_PROCESO.
     * Impide aprobar o rechazar una solicitud ya resuelta.
     */
    
    private void requireInProcess(LoanApplication application) {
        if (application.getStatus() != ApplicationStatus.EN_PROCESO) {
            throw new IllegalStateException("La solicitud ya fue resuelta");
        }
    }
}
