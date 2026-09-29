DROP DATABASE IF EXISTS ironforge_gym;
CREATE DATABASE ironforge_gym
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE ironforge_gym;

-- =========================================
-- TABLA: usuarios
-- =========================================
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'RECEPCIONISTA') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- =========================================
-- TABLA: clientes
-- =========================================
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(8) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(15) NULL
) ENGINE=InnoDB;

-- =========================================
-- TABLA: tipos_membresia
-- =========================================
CREATE TABLE tipos_membresia (
    id_tipo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    precio DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB;

-- =========================================
-- TABLA: membresias
-- =========================================
CREATE TABLE membresias (
    id_membresia INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_tipo INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,

    CONSTRAINT chk_membresias_fechas
        CHECK (fecha_fin >= fecha_inicio),

    CONSTRAINT fk_membresias_clientes
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_membresias_tipos
        FOREIGN KEY (id_tipo)
        REFERENCES tipos_membresia(id_tipo)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =========================================
-- TABLA: ingresos
-- =========================================
CREATE TABLE ingresos (
    id_ingreso INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_membresia INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ingresos_clientes
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_ingresos_membresias
        FOREIGN KEY (id_membresia)
        REFERENCES membresias(id_membresia)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_ingresos_usuarios
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;