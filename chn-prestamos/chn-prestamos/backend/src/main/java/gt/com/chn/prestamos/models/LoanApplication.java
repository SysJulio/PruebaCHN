package gt.com.chn.prestamos.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gt.com.chn.prestamos.models.Client;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Entidad que representa una solicitud de préstamo realizada por un cliente.
 * Contiene la información del monto solicitado, plazo, motivo de la solicitud,
 * estado actual y la posible aprobación del préstamo.
 */

@Entity
@Table(name = "solicitudes_prestamo")
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
     /**
     * Cliente que realiza la solicitud de préstamo.
     * Relación muchos a uno, ya que un cliente puede tener varias solicitudes.
     */
    
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties("loanApplications")
    private Client client;

    /**
     * Monto solicitado por el cliente.
     * Debe ser mayor a cero.
     */
    
    @NotNull
    @DecimalMin("1.00")
    @Column(name = "monto_solicitado", nullable = false, precision = 18, scale = 2)
    private BigDecimal requestedAmount;

    /**
     * Cantidad de meses solicitados para el préstamo.
     * Debe ser mayor a cero.
     */

    @NotNull
    @Min(1)
    @Column(name = "plazo_meses", nullable = false)
    private Integer termMonths;

    @NotBlank
    @Column(name = "destino", nullable = false, length = 1000)
    private String purpose;

     /**
     * Estado actual de la solicitud.
     * Por defecto se crea en estado EN_PROCESO.
     */
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private ApplicationStatus status = ApplicationStatus.EN_PROCESO;

    @Column(name = "detalle_decision", length = 1000)
    private String decisionDetails;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "fecha_decision")
    private LocalDateTime decidedAt;

     /**
     * Préstamo aprobado asociado a esta solicitud.
     * Relación uno a uno.
     */
    
    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("application")
    private ApprovedLoan approvedLoan;

    
       // ==========================
    // Getters y Setters
    // ==========================
    
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public String getDecisionDetails() { return decisionDetails; }
    public void setDecisionDetails(String decisionDetails) { this.decisionDetails = decisionDetails; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
    public ApprovedLoan getApprovedLoan() { return approvedLoan; }
    public void setApprovedLoan(ApprovedLoan approvedLoan) { this.approvedLoan = approvedLoan; }
}
