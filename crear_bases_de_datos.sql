-- ═══════════════════════════════════════════════════════════════
-- ShopFlow - Script de creación de bases de datos
-- Ejecutar en XAMPP (phpMyAdmin o MySQL Shell) ANTES de
-- levantar cualquier microservicio.
-- ═══════════════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS shopflow_autenticacion CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS shopflow_usuario       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS shopflow_producto      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS shopflow_inventario    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS shopflow_carrito       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS shopflow_orden         CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS shopflow_pago          CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS shopflow_envio         CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS shopflow_notificacion  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS shopflow_resena        CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SELECT 'Bases de datos ShopFlow creadas correctamente.' AS resultado;
