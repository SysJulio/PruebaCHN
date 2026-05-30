# CHN Prestamos Bancarios

Sistema web para gestionar clientes, solicitudes de prestamos, aprobaciones, rechazos, prestamos aprobados y pagos en efectivo.

## Tecnologias

- Backend: Java 17, Spring Boot, JPA/Hibernate
- Frontend: React, Vite, Nginx
- Base de datos: SQL Server con Transact-SQL
- Despliegue: Docker Compose

## Ejecutar con Docker Compose

```bash
docker compose up --build
```

Servicios:

- Frontend: http://localhost:5173
- Backend API: http://localhost:8081/api
- SQL Server: localhost:1433

Credenciales locales de base de datos:

- Usuario: `sa`
- Password: `ChnPass123!`
- Base de datos: `chn_prestamos`

## Funcionalidades

- Crear, listar, editar y eliminar clientes.
- Eliminar un cliente borra sus solicitudes, prestamos aprobados y pagos asociados.
- Crear solicitudes de prestamo con monto, plazo y destino.
- Listar solicitudes por estado: en proceso, aprobado o rechazado.
- Aprobar o rechazar solicitudes registrando el detalle de decision.
- Generar automaticamente un prestamo aprobado al aprobar una solicitud.
- Registrar pagos en efectivo para prestamos aprobados.
- Calcular cuota fija mensual con tasa anual y plazo.
- Mostrar plan de pagos estimado con capital, interes y saldo por mes.
- Registrar pagos como cuota fija o abono directo a capital.
- Desglosar cada pago en capital aplicado e interes aplicado.
- Calcular saldo de capital pendiente y marcar el prestamo como pagado al completar el saldo.

## Endpoints principales

- `GET /api/clients`
- `POST /api/clients`
- `PUT /api/clients/{id}`
- `DELETE /api/clients/{id}`
- `GET /api/loan-applications`
- `POST /api/loan-applications`
- `POST /api/loan-applications/{id}/approve`
- `POST /api/loan-applications/{id}/reject`
- `GET /api/approved-loans`
- `POST /api/approved-loans/{loanId}/payments`

## Estructura del backend

El backend esta organizado por responsabilidades:

- `controllers`: endpoints REST.
- `services`: reglas de negocio.
- `repositories`: acceso a datos con JPA.
- `models`: entidades y enumeraciones.
- `dto`: objetos de entrada para las solicitudes.
- `common`: manejo de errores compartido.

## Documentacion

- Script Transact-SQL: `database/schema.sql`
- Diagrama entidad/relacion: `docs/diagrama-er.md`

## Modelo de base de datos

Las tablas y columnas estan nombradas en espanol para mantener consistencia con el caso del examen:

- `clientes`
- `solicitudes_prestamo`
- `prestamos_aprobados`
- `pagos`
