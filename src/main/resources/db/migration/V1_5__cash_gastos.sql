-- ===========================
-- V1.5 - CASH Y GASTOS (Caja Diaria + Movimientos + Gastos Operativos)
-- ===========================

-- CAJA DIARIA
CREATE TABLE cajas_diarias (
    id SERIAL PRIMARY KEY,
    fecha DATE NOT NULL UNIQUE,
    usuario_id INT NOT NULL,
    monto_inicial DECIMAL(18,2) NOT NULL DEFAULT 0,
    monto_final_sistema DECIMAL(18,2) DEFAULT 0,
    monto_final_real DECIMAL(18,2),
    diferencia DECIMAL(18,2),
    estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA' CHECK (estado IN ('ABIERTA','CERRADA')),
    opened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP
);

-- MOVIMIENTOS DE CAJA
CREATE TABLE movimientos_caja (
    id SERIAL PRIMARY KEY,
    caja_diaria_id INT NOT NULL REFERENCES cajas_diarias(id),
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('INGRESO','EGRESO')),
    origen VARCHAR(20) NOT NULL CHECK (origen IN ('VENTA','COBRO','GASTO','OTRO')),
    ref_id INT,
    metodo_pago VARCHAR(20) NOT NULL
        CHECK (metodo_pago IN ('EFECTIVO','YAPE','PLIN','TRANSFERENCIA','MIXTO')),
    monto DECIMAL(18,2) NOT NULL,
    descripcion VARCHAR(255),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- GASTOS OPERATIVOS (categoría libre/texto)
CREATE TABLE gastos_operativos (
    id SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    monto DECIMAL(18,2) NOT NULL,
    metodo_pago VARCHAR(20) NOT NULL
        CHECK (metodo_pago IN ('EFECTIVO','YAPE','PLIN','TRANSFERENCIA','MIXTO')),
    proveedor_id INT REFERENCES proveedores(id),
    comprobante_url VARCHAR(500),
    usuario_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);