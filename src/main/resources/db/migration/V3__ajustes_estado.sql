-- V3: Añadir estado a ajustes (alineado con entradas/salidas)
ALTER TABLE ajustes ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR'
    CHECK (estado IN ('BORRADOR', 'CONFIRMADO', 'ANULADO'));
