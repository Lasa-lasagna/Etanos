-- ===========================================================
-- V1.1 - HARDENING SCHEMA (constraints, triggers, secuencias, version)
-- Complementa V1__Creacion.sql para concurrencia y auditoría
-- ===========================================================

-- ===========================
-- 1. COLUMNAS @Version (optimistic lock) en raíces de agregado
-- ===========================
ALTER TABLE entradas    ADD COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE salidas     ADD COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE ajustes     ADD COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE inventario  ADD COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE productos   ADD COLUMN version INT NOT NULL DEFAULT 0;

-- ===========================
-- 2. NOT NULL en numero de documentos
--    (la app asigna numero desde secuencia en @PrePersist)
-- ===========================
ALTER TABLE entradas ALTER COLUMN numero SET NOT NULL;
ALTER TABLE salidas  ALTER COLUMN numero SET NOT NULL;
ALTER TABLE ajustes  ALTER COLUMN numero SET NOT NULL;

-- ===========================
-- 3. CHECK: stock nunca negativo a nivel BD
-- ===========================
ALTER TABLE inventario
    ADD CONSTRAINT chk_inventario_stock_no_negativo CHECK (stock_actual >= 0);

-- ===========================
-- 4. UNIQUE en kardex(referencia_tipo, referencia_id)
--    Evita kardex duplicado en reintentos/concurrencia
-- ===========================
ALTER TABLE kardex
    ADD CONSTRAINT uk_kardex_referencia UNIQUE (referencia_tipo, referencia_id);

-- ===========================
-- 5. PRECISION de costos (PECPS/UEPS necesitan 4-6 decimales)
-- ===========================
ALTER TABLE kardex      ALTER COLUMN costo_unitario TYPE DECIMAL(18,6);
ALTER TABLE productos  ALTER COLUMN costo_promedio TYPE DECIMAL(18,6);
ALTER TABLE productos  ALTER COLUMN precio_compra  TYPE DECIMAL(18,6);
ALTER TABLE productos  ALTER COLUMN precio_venta   TYPE DECIMAL(18,6);
ALTER TABLE entrada_detalle ALTER COLUMN precio_compra TYPE DECIMAL(18,6);
ALTER TABLE entrada_detalle ALTER COLUMN subtotal       TYPE DECIMAL(18,6);
ALTER TABLE salida_detalle  ALTER COLUMN precio_venta   TYPE DECIMAL(18,6);
ALTER TABLE salida_detalle  ALTER COLUMN subtotal       TYPE DECIMAL(18,6);

-- ===========================
-- 6. SECUENCIAS para numeración de documentos
-- ===========================
CREATE SEQUENCE IF NOT EXISTS entradas_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS salidas_seq  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS ajustes_seq  START WITH 1 INCREMENT BY 1;

-- ===========================
-- 7. TRIGGERS updated_at (para SQL nativo / Flyway)
--    Hibernate @UpdateTimestamp solo cubre operaciones por JPA
-- ===========================
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_marcas_updated_at ON marcas;
CREATE TRIGGER trg_marcas_updated_at BEFORE UPDATE ON marcas
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_productos_updated_at ON productos;
CREATE TRIGGER trg_productos_updated_at BEFORE UPDATE ON productos
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_inventario_updated_at ON inventario;
CREATE TRIGGER trg_inventario_updated_at BEFORE UPDATE ON inventario
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
