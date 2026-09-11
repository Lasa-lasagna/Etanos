-- ===========================
-- V1.4 - SALES FINANCIERO (Extender Salida + Cobros + Cuentas por Cobrar)
-- ===========================

-- ALTER salidas: campos financieros + bombona
ALTER TABLE salidas
    ADD COLUMN tipo_venta VARCHAR(20) NOT NULL DEFAULT 'CONTADO'
        CHECK (tipo_venta IN ('CONTADO','CREDITO')),
    ADD COLUMN metodo_pago VARCHAR(20) NOT NULL DEFAULT 'EFECTIVO'
        CHECK (metodo_pago IN ('EFECTIVO','YAPE','PLIN','TRANSFERENCIA','MIXTO')),
    ADD COLUMN entrega_bombona BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN fecha_vencimiento DATE,
    ADD COLUMN estado_factura VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
        CHECK (estado_factura IN ('PENDIENTE','EMITIDA','ANULADA','COBRADA')),
    ADD COLUMN numero_factura VARCHAR(30) UNIQUE,
    ADD COLUMN subtotal DECIMAL(18,2) DEFAULT 0,
    ADD COLUMN impuestos DECIMAL(18,2) DEFAULT 0,
    ADD COLUMN total DECIMAL(18,2) DEFAULT 0;

-- COBROS (abonos a ventas a crédito)
CREATE TABLE cobros (
    id SERIAL PRIMARY KEY,
    venta_id INT NOT NULL REFERENCES salidas(id),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    monto DECIMAL(18,2) NOT NULL,
    metodo_pago VARCHAR(20) NOT NULL
        CHECK (metodo_pago IN ('EFECTIVO','YAPE','PLIN','TRANSFERENCIA','MIXTO')),
    referencia VARCHAR(100),
    observacion TEXT,
    usuario_id INT
);

-- CUENTAS POR COBRAR (vista materializada)
CREATE MATERIALIZED VIEW cuentas_cobrar AS
SELECT
    c.id AS cliente_id,
    c.nombre AS cliente_nombre,
    c.nro_doc,
    c.limite_credito,
    COALESCE(SUM(CASE WHEN s.estado_factura IN ('PENDIENTE','EMITIDA') THEN s.total ELSE 0 END),0) AS saldo_pendiente,
    COALESCE(SUM(CASE WHEN s.tipo_venta='CREDITO' AND s.fecha_vencimiento < CURRENT_DATE
        AND s.estado_factura IN ('PENDIENTE','EMITIDA') THEN s.total ELSE 0 END),0) AS vencido_total,
    MAX(s.fecha_vencimiento) AS prox_vencimiento
FROM clientes c
LEFT JOIN salidas s ON s.cliente_id = c.id AND s.tipo_venta = 'CREDITO'
GROUP BY c.id, c.nombre, c.nro_doc, c.limite_credito;