package gt.com.chn.prestamos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) utilizado para recibir la información
 * necesaria para crear una nueva solicitud de préstamo.
 *
 * Este record se utiliza como cuerpo (Request Body) de las peticiones
 * enviadas al API para registrar una solicitud de préstamo.
 *
 * Validaciones:
 * - clientId: obligatorio.
 * - requestedAmount: obligatorio y debe ser mayor o igual a 1.00.
 * - termMonths: obligatorio y debe ser mayor o igual a 1.
 * - purpose: obligatorio y no puede estar vacío.
 */


public record CreateLoanApplicationRequest(
        @NotNull Long clientId,
        @NotNull @DecimalMin("1.00") BigDecimal requestedAmount,
        @NotNull @Min(1) Integer termMonths,
        @NotBlank String purpose
) {
}
