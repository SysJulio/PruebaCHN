package gt.com.chn.prestamos.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un préstamo aprobado dentro del sistema.
 * Almacena la información financiera del préstamo, el estado de pago
 * y el historial de pagos realizados por el cliente.
 */


@Entity
@Table(name = "prestamos_aprobados")
public class ApprovedLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Solicitud de préstamo asociada.
     * Relación uno a uno con la solicitud original.
     */
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "solicitud_id", nullable = false, unique = true)
    @JsonIgnoreProperties("approvedLoan")
    private LoanApplication application;

    @Column(name = "monto_capital", nullable = false, precision = 18, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "plazo_meses", nullable = false)
    private Integer termMonths;

    @Column(name = "tasa_interes_anual", nullable = false, precision = 5, scale = 2)
    private BigDecimal annualInterestRate;

    @Column(name = "fecha_aprobacion", nullable = false)
    private LocalDate approvalDate = LocalDate.now();

      /**
     * Estado actual del pago del préstamo.
     */
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDIENTE;

   
   /**
     * Lista de pagos realizados sobre el préstamo.
     * Se eliminan automáticamente cuando el préstamo es eliminado.
     */
   
   
    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("loan")
    private List<Payment> payments = new ArrayList<>();

    public BigDecimal getTotalPaid() {
        return payments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPrincipalPaid() {
        return payments.stream().map(Payment::getPrincipalApplied).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getInterestPaid() {
        return payments.stream().map(Payment::getInterestApplied).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPendingBalance() {
        return principalAmount.subtract(getPrincipalPaid()).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

   
   /**
     * Calcula la cuota mensual utilizando la fórmula de amortización.
     *
     * @return valor de la cuota mensual.
     */
   
   
    public BigDecimal getMonthlyPayment() {
        BigDecimal monthlyRate = annualInterestRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principalAmount.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        }
        double rate = monthlyRate.doubleValue();
        double payment = principalAmount.doubleValue() * rate / (1 - Math.pow(1 + rate, -termMonths));
        return BigDecimal.valueOf(payment).setScale(2, RoundingMode.HALF_UP);
    }

    
    /**
     * Calcula el monto de la cuota actual considerando
     * saldo pendiente e intereses generados.
     *
     * @return monto de la cuota actual.
     */
    
    
    public BigDecimal getCurrentInstallmentAmount() {
        BigDecimal pendingBalance = getPendingBalance();
        if (pendingBalance.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal monthlyRate = annualInterestRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal currentInterest = pendingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
        return getMonthlyPayment().min(pendingBalance.add(currentInterest)).setScale(2, RoundingMode.HALF_UP);
    }

   
     // ==========================
    // Getters y Setters
    // ==========================
   
   
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LoanApplication getApplication() { return application; }
    public void setApplication(LoanApplication application) { this.application = application; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
    public BigDecimal getAnnualInterestRate() { return annualInterestRate; }
    public void setAnnualInterestRate(BigDecimal annualInterestRate) { this.annualInterestRate = annualInterestRate; }
    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
}
