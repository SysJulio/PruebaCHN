package gt.com.chn.prestamos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import gt.com.chn.prestamos.models.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) utilizado para registrar un pago
 * realizado sobre un préstamo aprobado.
 *
 * Este objeto transporta la información necesaria para almacenar
 * los datos de un pago efectuado por el cliente.
 *
 * Validaciones:
 * - amount: obligatorio y debe ser mayor a 0.00.
 * - paymentDate: fecha en que se realizó el pago.
 * - receiptNumber: obligatorio y no puede estar vacío.
 * - paymentType: tipo de pago realizado.
 */



public record PaymentRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        LocalDate paymentDate,
        @NotBlank String receiptNumber,
        PaymentType paymentType
) {
}
