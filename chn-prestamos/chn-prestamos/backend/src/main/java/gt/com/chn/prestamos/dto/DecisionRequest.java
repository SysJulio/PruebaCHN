package gt.com.chn.prestamos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) utilizado para recibir la información
 * relacionada con la decisión tomada sobre una solicitud de préstamo.
 *
 * Este objeto puede utilizarse para aprobar o rechazar una solicitud,
 * almacenando los detalles de la decisión y la tasa de interés anual
 * cuando corresponda.
 *
 * Validaciones:
 * - details: obligatorio y no puede estar vacío.
 * - annualInterestRate: debe ser mayor o igual a 0.00.
 */


public record DecisionRequest(
        @NotBlank String details,
        @DecimalMin("0.00") BigDecimal annualInterestRate
) {
}
