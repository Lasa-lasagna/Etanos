-- ===========================
-- V1.3 - PARTNERS (Clientes y Proveedores)
-- ===========================

-- CLIENTES
CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    tipo_doc VARCHAR(10) NOT NULL,
    nro_doc VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    limite_credito DECIMAL(18,2) DEFAULT 0,
    dias_credito INT DEFAULT 0,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    version INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PROVEEDORES
CREATE TABLE proveedores (
    id SERIAL PRIMARY KEY,
    tipo_doc VARCHAR(10) NOT NULL,
    nro_doc VARCHAR(20) NOT NULL UNIQUE,
    razon_social VARCHAR(150) NOT NULL,
    nombre_comercial VARCHAR(150),
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    condicion_pago INT DEFAULT 0,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    version INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- FKs en tablas existentes
ALTER TABLE salidas  ADD CONSTRAINT fk_salidas_cliente   FOREIGN KEY (cliente_id) REFERENCES clientes(id);
ALTER TABLE entradas ADD CONSTRAINT fk_entradas_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id);