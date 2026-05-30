package gt.com.chn.prestamos.common;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Manejador global de excepciones para toda la API.
 * Permite capturar errores específicos y devolver respuestas
 * HTTP personalizadas en formato JSON.
 */

@RestControllerAdvice
public class ApiExceptionHandler {

  /**
     * Captura excepciones de tipo NotFoundException.
     * Se utiliza cuando un recurso solicitado no existe.
     */

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Map<String, Object>> notFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("timestamp", Instant.now(), "message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<Map<String, Object>> conflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("timestamp", Instant.now(), "message", ex.getMessage()));
    }

   /**
     * Captura errores de validación generados por las anotaciones
     * de Bean Validation (@Valid, @NotBlank, @Email, etc.).
     *
     * Obtiene todos los campos inválidos y construye un mapa
     * con el nombre del campo y el mensaje de error correspondiente.
     */
   
   
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(java.util.stream.Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now(), "errors", errors));
    }
}
