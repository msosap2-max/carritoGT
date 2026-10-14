-- =====================================================
-- Base de datos: tienda_db (MariaDB)
-- Tablas: usuarios, productos, ordenes, orden_detalle
-- =====================================================

CREATE DATABASE IF NOT EXISTS tienda_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE tienda_db;

-- -----------------------------------------------------
-- 1. USUARIOS (pensada para login)
-- -----------------------------------------------------
CREATE TABLE usuarios (
  id               INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre           VARCHAR(100) NOT NULL,
  correo           VARCHAR(150) NOT NULL,
  contrasena_hash  VARCHAR(255) NOT NULL,   -- NUNCA la contraseña real, solo su "huella" cifrada
  rol              ENUM('cliente','admin') NOT NULL DEFAULT 'cliente',
  activo           TINYINT(1) NOT NULL DEFAULT 1,  -- 1 = puede entrar, 0 = bloqueado
  ultimo_login     DATETIME NULL,
  creado_en        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_usuarios_correo (correo)    -- no se pueden repetir correos
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 2. PRODUCTOS
-- -----------------------------------------------------
CREATE TABLE productos (
  id           INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(150) NOT NULL,
  descripcion  TEXT NULL,
  precio       DECIMAL(10,2) NOT NULL,
  stock        INT UNSIGNED NOT NULL DEFAULT 0,
  imagen_url   VARCHAR(255) NULL,
  activo       TINYINT(1) NOT NULL DEFAULT 1,  -- 1 = visible en la tienda
  creado_en    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 3. ORDENES (el "recibo": quién compró y cuándo)
-- -----------------------------------------------------
CREATE TABLE ordenes (
  id          INT UNSIGNED NOT NULL AUTO_INCREMENT,
  usuario_id  INT UNSIGNED NOT NULL,
  estado      ENUM('pendiente','pagada','enviada','entregada','cancelada')
              NOT NULL DEFAULT 'pendiente',
  total       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  creado_en   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_ordenes_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 4. ORDEN_DETALLE (las "líneas" del recibo: qué productos y cuántos)
-- -----------------------------------------------------
CREATE TABLE orden_detalle (
  id               INT UNSIGNED NOT NULL AUTO_INCREMENT,
  orden_id         INT UNSIGNED NOT NULL,
  producto_id      INT UNSIGNED NOT NULL,
  cantidad         INT UNSIGNED NOT NULL,
  precio_unitario  DECIMAL(10,2) NOT NULL,  -- precio al momento de comprar
  PRIMARY KEY (id),
  CONSTRAINT fk_detalle_orden
    FOREIGN KEY (orden_id) REFERENCES ordenes(id) ON DELETE CASCADE,
  CONSTRAINT fk_detalle_producto
    FOREIGN KEY (producto_id) REFERENCES productos(id)
) ENGINE=InnoDB;
