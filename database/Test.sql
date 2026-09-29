USE ironforge_gym;

-- =========================================
-- 1. VERIFICAR TABLAS
-- =========================================
SHOW TABLES;

-- =========================================
-- 2. VER ESTRUCTURA DE TABLAS
-- =========================================
DESCRIBE usuarios;
DESCRIBE clientes;
DESCRIBE tipos_membresia;
DESCRIBE membresias;
DESCRIBE ingresos;

-- =========================================
-- 3. VER DATOS INSERTADOS
-- =========================================
SELECT * FROM usuarios;
SELECT * FROM clientes;
SELECT * FROM tipos_membresia;
SELECT * FROM membresias;
SELECT * FROM ingresos;

-- =========================================
-- 4. PROBAR BUSQUEDA DE CLIENTE POR DNI
-- RF-02
-- =========================================
SELECT *
FROM clientes
WHERE dni = '12345678';

-- =========================================
-- 5. CONSULTAR VIGENCIA DE MEMBRESIAS
-- RF-04
-- =========================================
SELECT
    m.id_membresia,
    c.dni,
    CONCAT(c.nombres, ' ', c.apellidos) AS cliente,
    tm.nombre AS tipo_membresia,
    m.fecha_inicio,
    m.fecha_fin,
    CASE
        WHEN CURDATE() < m.fecha_inicio THEN 'AUN_NO_VIGENTE'
        WHEN CURDATE() BETWEEN m.fecha_inicio AND m.fecha_fin THEN 'VIGENTE'
        ELSE 'VENCIDA'
    END AS estado_calculado
FROM membresias m
INNER JOIN clientes c ON m.id_cliente = c.id_cliente
INNER JOIN tipos_membresia tm ON m.id_tipo = tm.id_tipo
ORDER BY m.id_membresia;

-- =========================================
-- 6. CONSULTAR SOLO MEMBRESIAS VIGENTES
-- =========================================
SELECT
    m.id_membresia,
    c.dni,
    CONCAT(c.nombres, ' ', c.apellidos) AS cliente,
    tm.nombre AS tipo_membresia,
    m.fecha_inicio,
    m.fecha_fin
FROM membresias m
INNER JOIN clientes c ON m.id_cliente = c.id_cliente
INNER JOIN tipos_membresia tm ON m.id_tipo = tm.id_tipo
WHERE CURDATE() BETWEEN m.fecha_inicio AND m.fecha_fin
ORDER BY c.apellidos, c.nombres;

-- =========================================
-- 7. CONSULTAR HISTORIAL DE INGRESOS POR DNI
-- RF-09
-- Ejemplo: Ana
-- =========================================
SELECT
    i.id_ingreso,
    c.dni,
    CONCAT(c.nombres, ' ', c.apellidos) AS cliente,
    tm.nombre AS tipo_membresia,
    i.fecha_hora,
    CONCAT(u.nombres, ' ', u.apellidos) AS registrado_por
FROM ingresos i
INNER JOIN clientes c ON i.id_cliente = c.id_cliente
INNER JOIN membresias m ON i.id_membresia = m.id_membresia
INNER JOIN tipos_membresia tm ON m.id_tipo = tm.id_tipo
INNER JOIN usuarios u ON i.id_usuario = u.id_usuario
WHERE c.dni = '12345678'
ORDER BY i.fecha_hora DESC;

-- =========================================
-- 8. CONSULTAR MEMBRESIAS PROXIMAS A VENCER
-- RF-11
-- =========================================
SELECT
    c.dni,
    CONCAT(c.nombres, ' ', c.apellidos) AS cliente,
    tm.nombre AS tipo_membresia,
    m.fecha_fin
FROM membresias m
INNER JOIN clientes c ON m.id_cliente = c.id_cliente
INNER JOIN tipos_membresia tm ON m.id_tipo = tm.id_tipo
WHERE m.fecha_fin BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
ORDER BY m.fecha_fin ASC;

-- =========================================
-- 9. PROBAR LOGIN (CONSULTA SIMPLE)
-- RF-10 / RF-13 / RF-14
-- =========================================
SELECT *
FROM usuarios
WHERE username = 'admin01'
  AND password = 'admin123'
  AND activo = TRUE;

SELECT *
FROM usuarios
WHERE username = 'recep02'
  AND password = 'recep456'
  AND activo = TRUE;

-- =========================================
-- 10. VER RENOVACIONES DE UN CLIENTE
-- RF-12
-- Ejemplo: Diego
-- =========================================
SELECT
    c.dni,
    CONCAT(c.nombres, ' ', c.apellidos) AS cliente,
    m.id_membresia,
    tm.nombre AS tipo_membresia,
    m.fecha_inicio,
    m.fecha_fin
FROM membresias m
INNER JOIN clientes c ON m.id_cliente = c.id_cliente
INNER JOIN tipos_membresia tm ON m.id_tipo = tm.id_tipo
WHERE c.dni = '67890123'
ORDER BY m.fecha_inicio;

-- =========================================
-- 11. COMPROBAR RELACION INGRESO - CLIENTE - MEMBRESIA - USUARIO
-- =========================================
SELECT
    i.id_ingreso,
    c.dni,
    CONCAT(c.nombres, ' ', c.apellidos) AS cliente,
    i.id_membresia,
    i.fecha_hora,
    u.username AS usuario_registro
FROM ingresos i
INNER JOIN clientes c ON i.id_cliente = c.id_cliente
INNER JOIN usuarios u ON i.id_usuario = u.id_usuario
ORDER BY i.id_ingreso;