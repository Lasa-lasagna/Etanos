# Gloria — gestión para comercio independiente

Sistema para llevar el día a día de un comercio pequeño: kardex (entradas, salidas y ajustes de stock), ventas al contado y a crédito, cobros, caja diaria y gastos operativos. Backend en Spring Boot + frontend en React.

## Tech Stack

| Capa | Tecnología |
|---|---|
| Backend | Spring Boot 4.1, Java 21, JPA/Hibernate, Flyway, Lombok, MapStruct |
| Frontend | Vite + React + TypeScript |
| BD | PostgreSQL |
| Auth | JWT |

## Estructura del proyecto

```
src/main/java/com/lasa/gloria/
├── partners/     (clientes, proveedores)
├── inventory/    (kardex, entradas, salidas, ajustes, stock)
├── sales/        (ventas, cobros)
├── cash/         (caja diaria, gastos, movimientos)
└── common/       (excepciones, seguridad JWT)
```

## Cómo levantar el proyecto

```bash
# Backend
cd gloria
./mvnw spring-boot:run

# Frontend
cd gloria-front
npm install
npm run dev
```

La base de datos es PostgreSQL en `localhost:5432` — Flyway aplica las migraciones automáticamente al arrancar.

### Variables de entorno (`.env`)

| Variable | Obligatoria | Descripción |
|---|---|---|
| `DB_NAME`, `DB_PORT`, `DB_URL`, `DB_USER`, `DB_PASSWORD` | Sí | Conexión a PostgreSQL |
| `JWT_SECRET` | Sí | Secreto para firmar los tokens JWT (mínimo 32 caracteres, valor propio). La app **no arranca** si está vacío o conserva el valor de ejemplo |

## Reglas de negocio clave

- Venta al contado: requiere caja abierta y registra el ingreso en caja automáticamente.
- Venta a crédito: queda como pendiente y se cobra por partes con cobros.
- Anular venta: devuelve el stock y revierte los movimientos de caja.
- Gasto: solo se registra con la caja de hoy abierta; al eliminar queda marcado como anulado sin tocar la caja.
