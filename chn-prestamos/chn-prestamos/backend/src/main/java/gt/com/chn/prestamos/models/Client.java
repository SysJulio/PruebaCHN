package gt.com.chn.prestamos.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import gt.com.chn.prestamos.models.LoanApplication;

/**
 * Entidad que representa a un cliente dentro del sistema de préstamos.
 * Contiene la información personal y de contacto del cliente,
 * así como el historial de solicitudes de préstamo asociadas.
 */

@Entity
@Table(name = "clientes")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nombre", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "apellido", nullable = false)
    private String lastName;

    @NotBlank
    @Column(name = "numero_identificacion", nullable = false, unique = true)
    private String identificationNumber;

    @NotNull
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate birthDate;

    @NotBlank
    @Column(name = "direccion", nullable = false)
    private String address;

    @Email
    @NotBlank
    @Column(name = "correo_electronico", nullable = false)
    private String email;

    @NotBlank
    @Column(name = "telefono", nullable = false)
    private String phone;

    /**
     * Lista de solicitudes de préstamo realizadas por el cliente.
     * La eliminación del cliente elimina automáticamente
     * todas las solicitudes asociadas.
     */
    
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanApplication> loanApplications = new ArrayList<>();

    
      // ==========================
    // Getters y Setters
    // ==========================
    
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getIdentificationNumber() { return identificationNumber; }
    public void setIdentificationNumber(String identificationNumber) { this.identificationNumber = identificationNumber; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
