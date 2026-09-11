-- ===========================
-- SCRIPT POSTGRESQL - MODULO INVENTARIO
-- IDs en INT (SERIAL) en lugar de BIGINT
-- ===========================

-- ===========================
-- ENUMS (removed - use VARCHAR with CHECK constraints for JPA compatibility)
-- ===========================

-- ===========================
-- MARCAS
-- ===========================
CREATE TABLE marcas (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    estado BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===========================
-- PRODUCTOS
-- ===========================
CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    marca_id INT REFERENCES marcas(id),

    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,

    precio_compra DECIMAL(12,2) DEFAULT 0,
    precio_venta DECIMAL(12,2) DEFAULT 0,
    costo_promedio DECIMAL(12,2) DEFAULT 0,

    estado BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ===========================
-- INVENTARIO (stock actual, sin almacenes)
-- ===========================
CREATE TABLE inventario (
    id SERIAL PRIMARY KEY,
    producto_id INT NOT NULL UNIQUE REFERENCES productos(id),

    stock_actual INT DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ===========================
-- ENTRADAS
-- ===========================
CREATE TABLE entradas (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(30) UNIQUE,

    proveedor_id INT,

    fecha TIMESTAMP,

    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR' CHECK (estado IN ('BORRADOR', 'CONFIRMADO', 'ANULADO')),

    usuario_id INT,

    observacion TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE entrada_detalle (
    id SERIAL PRIMARY KEY,

    entrada_id INT NOT NULL REFERENCES entradas(id),
    producto_id INT NOT NULL REFERENCES productos(id),

    cantidad INT,
    precio_compra DECIMAL(12,2),
    subtotal DECIMAL(12,2)
);


-- ===========================
-- SALIDAS
-- ===========================
CREATE TABLE salidas (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(30) UNIQUE,

    cliente_id INT,

    fecha TIMESTAMP,

    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR' CHECK (estado IN ('BORRADOR', 'CONFIRMADO', 'ANULADO')),

    usuario_id INT,

    observacion TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE salida_detalle (
    id SERIAL PRIMARY KEY,

    salida_id INT NOT NULL REFERENCES salidas(id),
    producto_id INT NOT NULL REFERENCES productos(id),

    cantidad INT,
    precio_venta DECIMAL(12,2),
    subtotal DECIMAL(12,2)
);


-- ===========================
-- AJUSTES
-- ===========================
CREATE TABLE ajustes (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(30) UNIQUE,

    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('INCREMENTO', 'DISMINUCION')),

    fecha TIMESTAMP,

    motivo VARCHAR(255),

    usuario_id INT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ajuste_detalle (
    id SERIAL PRIMARY KEY,

    ajuste_id INT NOT NULL REFERENCES ajustes(id),
    producto_id INT NOT NULL REFERENCES productos(id),

    cantidad INT
);


-- ===========================
-- KARDEX (historico, un registro por linea de detalle)
-- ===========================
-- NOTA: referencia_id es polimorfico (puede apuntar a entrada_detalle.id,
-- salida_detalle.id o ajuste_detalle.id segun tipo_movimiento).
-- Postgres no permite FK condicional a distintas tablas, por eso NO tiene
-- REFERENCES aqui. La integridad se controla desde la aplicacion.
-- Si prefieres integridad referencial estricta, se necesitaria una tabla
-- kardex por tipo de movimiento o una tabla intermedia "documentos".
CREATE TABLE kardex (
    id SERIAL PRIMARY KEY,

    producto_id INT NOT NULL REFERENCES productos(id),

    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('ENTRADA', 'SALIDA', 'AJUSTE+', 'AJUSTE-')),

    cantidad INT,

    stock_anterior INT,
    stock_nuevo INT,

    costo_unitario DECIMAL(12,2),

    referencia_tipo VARCHAR(50),
    referencia_id INT,

    usuario_id INT,

    observacion VARCHAR(255),

    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ===========================
-- INDICES RECOMENDADOS
-- ===========================
CREATE INDEX idx_kardex_producto ON kardex(producto_id);
CREATE INDEX idx_kardex_referencia ON kardex(referencia_tipo, referencia_id);
CREATE INDEX idx_entrada_detalle_entrada ON entrada_detalle(entrada_id);
CREATE INDEX idx_salida_detalle_salida ON salida_detalle(salida_id);
CREATE INDEX idx_ajuste_detalle_ajuste ON ajuste_detalle(ajuste_id);