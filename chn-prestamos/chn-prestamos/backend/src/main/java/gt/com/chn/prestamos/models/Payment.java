package gt.com.chn.prestamos.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa un pago realizado sobre un préstamo aprobado.
 * Registra la información del pago, incluyendo monto, fecha,
 * tipo de pago y la distribución entre capital e intereses.
 */

@Entity
@Table(name = "pagos")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Préstamo asociado al pago.
     * Relación muchos a uno, ya que un préstamo puede tener múltiples pagos.
     */
   
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestamo_id", nullable = false)
    @JsonIgnoreProperties("payments")
    private ApprovedLoan loan;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "monto", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "capital_aplicado", precision = 18, scale = 2)
    private BigDecimal principalApplied;

    @Column(name = "interes_aplicado", precision = 18, scale = 2)
    private BigDecimal interestApplied;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false)
    private PaymentType paymentType = PaymentType.CUOTA;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate paymentDate = LocalDate.now();

    @NotBlank
    @Column(name = "numero_recibo", nullable = false)
    private String receiptNumber;

      // ==========================
    // Getters y Setters
    // ==========================
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ApprovedLoan getLoan() { return loan; }
    public void setLoan(ApprovedLoan loan) { this.loan = loan; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPrincipalApplied() { return principalApplied == null ? amount : principalApplied; }
    public void setPrincipalApplied(BigDecimal principalApplied) { this.principalApplied = principalApplied; }
    public BigDecimal getInterestApplied() { return interestApplied == null ? BigDecimal.ZERO : interestApplied; }
    public void setInterestApplied(BigDecimal interestApplied) { this.interestApplied = interestApplied; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
}
