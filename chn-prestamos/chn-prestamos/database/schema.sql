IF DB_ID(N'chn_prestamos') IS NULL
BEGIN
    CREATE DATABASE chn_prestamos;
END;
GO

USE chn_prestamos;
GO

IF OBJECT_ID(N'clientes', N'U') IS NULL
BEGIN
    CREATE TABLE clientes (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nombre NVARCHAR(120) NOT NULL,
        apellido NVARCHAR(120) NOT NULL,
        numero_identificacion NVARCHAR(60) NOT NULL UNIQUE,
        fecha_nacimiento DATE NOT NULL,
        direccion NVARCHAR(250) NOT NULL,
        correo_electronico NVARCHAR(180) NOT NULL,
        telefono NVARCHAR(40) NOT NULL
    );
END;
GO

IF OBJECT_ID(N'solicitudes_prestamo', N'U') IS NULL
BEGIN
    CREATE TABLE solicitudes_prestamo (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        cliente_id BIGINT NOT NULL,
        monto_solicitado DECIMAL(18,2) NOT NULL,
        plazo_meses INT NOT NULL,
        destino NVARCHAR(1000) NOT NULL,
        estado NVARCHAR(20) NOT NULL DEFAULT 'EN_PROCESO',
        detalle_decision NVARCHAR(1000) NULL,
        fecha_creacion DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        fecha_decision DATETIME2 NULL,
        CONSTRAINT fk_solicitudes_clientes
            FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE,
        CONSTRAINT ck_solicitudes_estado
            CHECK (estado IN ('EN_PROCESO', 'APROBADO', 'RECHAZADO')),
        CONSTRAINT ck_solicitudes_monto CHECK (monto_solicitado > 0),
        CONSTRAINT ck_solicitudes_plazo CHECK (plazo_meses > 0)
    );
END;
GO

IF OBJECT_ID(N'prestamos_aprobados', N'U') IS NULL
BEGIN
    CREATE TABLE prestamos_aprobados (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        solicitud_id BIGINT NOT NULL UNIQUE,
        monto_capital DECIMAL(18,2) NOT NULL,
        plazo_meses INT NOT NULL,
        tasa_interes_anual DECIMAL(5,2) NOT NULL DEFAULT 0,
        fecha_aprobacion DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
        estado_pago NVARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
        CONSTRAINT fk_prestamos_solicitudes
            FOREIGN KEY (solicitud_id) REFERENCES solicitudes_prestamo(id) ON DELETE CASCADE,
        CONSTRAINT ck_prestamos_estado_pago
            CHECK (estado_pago IN ('PENDIENTE', 'PAGADO')),
        CONSTRAINT ck_prestamos_monto CHECK (monto_capital > 0)
    );
END;
GO

IF OBJECT_ID(N'pagos', N'U') IS NULL
BEGIN
    CREATE TABLE pagos (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        prestamo_id BIGINT NOT NULL,
        monto DECIMAL(18,2) NOT NULL,
        capital_aplicado DECIMAL(18,2) NULL,
        interes_aplicado DECIMAL(18,2) NULL,
        tipo_pago NVARCHAR(20) NOT NULL DEFAULT 'CUOTA',
        fecha_pago DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
        numero_recibo NVARCHAR(80) NOT NULL,
        CONSTRAINT fk_pagos_prestamos
            FOREIGN KEY (prestamo_id) REFERENCES prestamos_aprobados(id) ON DELETE CASCADE,
        CONSTRAINT ck_pagos_monto CHECK (monto > 0),
        CONSTRAINT ck_pagos_tipo
            CHECK (tipo_pago IN ('CUOTA', 'ABONO_CAPITAL'))
    );
END;
GO

UPDATE pagos
SET capital_aplicado = monto
WHERE capital_aplicado IS NULL;
GO

UPDATE pagos
SET interes_aplicado = 0
WHERE interes_aplicado IS NULL;
GO
