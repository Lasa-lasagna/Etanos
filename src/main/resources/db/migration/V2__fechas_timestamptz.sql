-- V2: Migrar timestamps de operación a TIMESTAMPTZ (UTC)
-- Dev limpio: no hay data que preservar, conversión directa.
ALTER TABLE entradas ALTER COLUMN fecha TYPE TIMESTAMPTZ;
ALTER TABLE salidas  ALTER COLUMN fecha TYPE TIMESTAMPTZ;
ALTER TABLE ajustes  ALTER COLUMN fecha TYPE TIMESTAMPTZ;
ALTER TABLE kardex   ALTER COLUMN fecha TYPE TIMESTAMPTZ;

ALTER TABLE cobros   ALTER COLUMN fecha TYPE TIMESTAMPTZ;
ALTER TABLE movimientos_caja ALTER COLUMN fecha TYPE TIMESTAMPTZ;
ALTER TABLE cajas_diarias  ALTER COLUMN opened_at TYPE TIMESTAMPTZ;
ALTER TABLE cajas_diarias  ALTER COLUMN closed_at TYPE TIMESTAMPTZ;
ALTER TABLE gastos_operativos ALTER COLUMN created_at TYPE TIMESTAMPTZ;
