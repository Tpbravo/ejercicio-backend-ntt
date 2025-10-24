-- ==========================================================
--CREACIÓN DE ROLES (Usuarios de conexión)
-- ==========================================================
-- Rol para el microservicio de gestión de clientes
CREATE ROLE ms_clientes LOGIN PASSWORD 'ms_clientes_pwd';

-- Rol para el microservicio de operaciones bancarias
CREATE ROLE ms_operaciones LOGIN PASSWORD 'ms_operaciones_pwd';

-- Rol de solo lectura (para reportes o auditoría)
CREATE ROLE report_readonly LOGIN PASSWORD 'readonly_pwd';

-- ==========================================================
--CREACIÓN DE BASES DE DATOS
-- ==========================================================
-- NOTA: 
-- Se utiliza el proveedor de locales 'ICU' con configuración 'es-ES'
-- (LOCALE_PROVIDER = 'icu', ICU_LOCALE = 'es-ES') para garantizar que las
-- operaciones de ordenamiento (ORDER BY) respeten el alfabeto español:
--   - La letra 'Ñ' se ordena correctamente entre 'N' y 'O'.
--   - Las vocales acentuadas ('Á', 'É', 'Í', 'Ó', 'Ú') mantienen su posición normal.
--
CREATE DATABASE gestion_clientes_db
  WITH OWNER = ms_clientes
       ENCODING = 'UTF8'
       ICU_LOCALE = 'es-ES'
       LOCALE_PROVIDER = icu
       TEMPLATE = template0;

-- Base de datos para las operaciones bancarias (cuentas y movimientos)
CREATE DATABASE operaciones_bancarias_db
  WITH OWNER = ms_operaciones
       ENCODING = 'UTF8'
       ICU_LOCALE = 'es-ES'
       LOCALE_PROVIDER = icu
       TEMPLATE = template0;

-- ==========================================================
-- CONFIGURACIÓN EN gestion_clientes_db
-- ==========================================================
\connect gestion_clientes_db;

-- Zona horaria local
ALTER DATABASE gestion_clientes_db SET TIMEZONE TO 'America/Guayaquil';

-- Crear schema principal y asignarlo al rol ms_clientes
CREATE SCHEMA IF NOT EXISTS core AUTHORIZATION ms_clientes;

-- Definir el search_path por defecto
ALTER ROLE ms_clientes IN DATABASE gestion_clientes_db SET search_path TO core,public;

-- Permitir acceso de lectura al rol de reportes
GRANT CONNECT ON DATABASE gestion_clientes_db TO report_readonly;
GRANT USAGE ON SCHEMA core TO report_readonly;

-- ==========================================================
-- CONFIGURACIÓN EN operaciones_bancarias_db
-- ==========================================================
\connect operaciones_bancarias_db;

-- Zona horaria local
ALTER DATABASE operaciones_bancarias_db SET TIMEZONE TO 'America/Guayaquil';

-- Crear schema principal y asignarlo al rol ms_operaciones
CREATE SCHEMA IF NOT EXISTS core AUTHORIZATION ms_operaciones;

-- Definir el search_path por defecto
ALTER ROLE ms_operaciones IN DATABASE operaciones_bancarias_db SET search_path TO core,public;

-- Permitir acceso de lectura al rol de reportes
GRANT CONNECT ON DATABASE operaciones_bancarias_db TO report_readonly;
GRANT USAGE ON SCHEMA core TO report_readonly;


-- ==========================================================
-- EXTENSIONES COMUNES
-- ==========================================================
\connect gestion_clientes_db;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

\connect operaciones_bancarias_db;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
