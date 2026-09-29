USE ironforge_gym;

-- =========================================
-- USUARIOS
-- =========================================
INSERT INTO usuarios (nombres, apellidos, username, password, rol, activo) VALUES
('Carlos', 'Ramirez', 'admin01', 'admin123', 'ADMINISTRADOR', TRUE),
('Lucia', 'Torres', 'recep01', 'recep123', 'RECEPCIONISTA', TRUE),
('Mario', 'Lopez', 'recep02', 'recep456', 'RECEPCIONISTA', FALSE);

-- =========================================
-- CLIENTES
-- =========================================
INSERT INTO clientes (dni, nombres, apellidos, telefono) VALUES
('12345678', 'Ana', 'Torres', '987654321'),
('23456789', 'Luis', 'Quispe', '999111222'),
('34567890', 'Carla', 'Mendoza', '988777666'),
('45678901', 'Pedro', 'Vargas', '977888999'),
('56789012', 'Rosa', 'Salas', '966555444'),
('67890123', 'Diego', 'Flores', '955444333');

-- =========================================
-- TIPOS DE MEMBRESIA
-- =========================================
INSERT INTO tipos_membresia (nombre, precio) VALUES
('Mensual', 120.00),
('Trimestral', 300.00),
('Semestral', 550.00);

-- =========================================
-- MEMBRESIAS
-- Casos:
-- 1 Ana    -> vigente
-- 2 Luis   -> vencida
-- 3 Carla  -> aun no vigente
-- 4 Pedro  -> vigente
-- 5 Diego  -> membresia antigua vencida
-- 6 Diego  -> renovacion vigente
-- 7 Rosa   -> vigente y proxima a vencer
-- =========================================
INSERT INTO membresias (id_cliente, id_tipo, fecha_inicio, fecha_fin) VALUES
(1, 1, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY)),
(2, 1, DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(3, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 33 DAY)),
(4, 2, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 85 DAY)),
(6, 1, DATE_SUB(CURDATE(), INTERVAL 70 DAY), DATE_SUB(CURDATE(), INTERVAL 40 DAY)),
(6, 1, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY)),
(5, 3, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY));

-- =========================================
-- INGRESOS
-- Solo para clientes con membresias validas
-- usuario que registra = recep01 (id_usuario = 2)
-- =========================================
INSERT INTO ingresos (id_cliente, id_membresia, id_usuario, fecha_hora) VALUES
(1, 1, 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 1, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 4, 2, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(5, 7, 2, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(6, 6, 2, NOW());