# AGENTS.md — IronForge Gym

## 1. Proyecto

IronForge Gym es un proyecto académico del curso Técnicas de Programación
Orientada a Objetos de UPN.

El sistema será una aplicación de escritorio para gestionar:

- clientes
- tipos de membresía
- membresías
- ingresos al gimnasio
- usuarios del sistema
- autenticación
- control de acceso por rol

## 2. Tecnologías

- Java 21
- Maven
- MySQL
- JDBC
- JavaFX en la fase final
- Git / GitHub

GroupId Maven:

com.tpoo.upn

ArtifactId:

ironforge-gym

Paquete raíz Java:

com.tpoo.upn

## 3. Arquitectura obligatoria

La estructura principal es:

src/main/java/com/tpoo/upn/

- app
- model
- dao
- controller
- config
- session

No crear paquetes adicionales sin una justificación clara.

## 4. Modelos

El paquete:

com.tpoo.upn.model

contiene:

- Persona
- Cliente
- Usuario
- TipoMembresia
- Membresia
- Ingreso

Persona es una clase abstracta.

Cliente extiende Persona.

Usuario extiende Persona.

No utilizar Lombok.

Los atributos deberán ser privados.

Los modelos tendrán posteriormente:

- constructores
- getters
- setters
- métodos definidos por el UML

## 5. Modelo de base de datos

La base de datos MySQL contiene únicamente estas tablas principales:

- usuarios
- clientes
- tipos_membresia
- membresias
- ingresos

IMPORTANTE:

Persona NO es una tabla.

Persona existe únicamente para representar herencia en Java.

## 6. Relaciones principales

Cliente 1 --- 0..N Membresia

TipoMembresia 1 --- 0..N Membresia

Cliente 1 --- 0..N Ingreso

Membresia 1 --- 0..N Ingreso

Usuario 1 --- 0..N Ingreso

Un Ingreso debe conocer:

- Cliente
- Membresia
- Usuario que registró el ingreso

La membresía usada para autorizar un ingreso debe pertenecer al mismo
cliente del ingreso.

## 7. Estado de Membresia

NO crear un atributo persistente llamado:

estado

El estado se calcula utilizando:

fechaInicio
fechaFin

Estados conceptuales:

AUN_NO_VIGENTE
VIGENTE
VENCIDA

Reglas:

fecha actual < fechaInicio
→ AUN_NO_VIGENTE

fecha actual entre fechaInicio y fechaFin
→ VIGENTE

fecha actual > fechaFin
→ VENCIDA

## 8. DAO

El paquete:

com.tpoo.upn.dao

contendrá:

- UsuarioDAO
- ClienteDAO
- TipoMembresiaDAO
- MembresiaDAO
- IngresoDAO

Los DAO son responsables de la persistencia.

Las operaciones SQL deben existir solamente dentro de DAO.

No colocar:

- SELECT
- INSERT
- UPDATE
- DELETE
- PreparedStatement
- executeQuery
- executeUpdate

en:

- modelos
- controladores
- Main
- JavaFX

Los DAO utilizarán PreparedStatement.

## 9. ConexionDB

La conexión estará centralizada en:

com.tpoo.upn.config.ConexionDB

Los DAO utilizarán ConexionDB para obtener conexiones JDBC.

No repetir datos o código de conexión dentro de cada DAO.

No colocar contraseñas reales de MySQL en archivos versionados.

## 10. Controllers

El paquete:

com.tpoo.upn.controller

contendrá:

- UsuarioController
- ClienteController
- MembresiaController
- IngresoController

Los controllers manejan:

- reglas de negocio
- validaciones
- coordinación entre DAO
- permisos de usuario

Los controllers NO deben contener SQL.

## 11. Sesion

El paquete:

com.tpoo.upn.session

contendrá:

Sesion

Sesion representa al usuario actualmente autenticado.

Los controladores podrán consultar Sesion para aplicar las restricciones
por rol.

Roles permitidos:

ADMINISTRADOR
RECEPCIONISTA

## 12. Reglas de roles

RECEPCIONISTA puede:

- registrar clientes
- buscar clientes
- actualizar clientes
- registrar membresías
- renovar membresías
- consultar vigencia
- registrar ingresos

ADMINISTRADOR puede:

- consultar historial de ingresos
- consultar membresías próximas a vencer
- gestionar tipos de membresía
- crear usuarios
- activar o desactivar usuarios

## 13. Main

La aplicación tendrá:

com.tpoo.upn.app.Main

Antes de implementar JavaFX, Main será utilizado para demostrar
requerimientos funcionales mediante consola.

La entrega debe demostrar al menos 5 requerimientos funcionales.

No colocar SQL directamente en Main.

## 14. JavaFX

JavaFX corresponde a la última fase.

No agregar JavaFX antes de que:

- modelos funcionen
- ConexionDB funcione
- DAO funcionen
- Sesion funcione
- Controllers funcionen
- Main permita demostrar los RF

JavaFX debe utilizar los controllers.

La interfaz gráfica NO debe implementar lógica SQL.

## 15. Orden de desarrollo

Seguir estas fases:

Fase 0
Diseño de base de datos

Fase 1
Modelos:
Persona
Cliente
Usuario
TipoMembresia
Membresia
Ingreso

Fase 2
Scripts MySQL

Fase 3
ConexionDB

Fase 4
DAO

Fase 5
Sesion

Fase 6
Controllers

Fase 7
Main y demostración de al menos 5 RF

Fase 8
JavaFX

No adelantarse innecesariamente a fases futuras.

## 16. Reglas para agentes de IA

Antes de modificar código:

1. Revisar esta documentación.
2. Revisar la estructura actual del proyecto.
3. No inventar clases ni paquetes.
4. No modificar el UML o modelo de datos sin autorización.
5. No cambiar nombres de clases existentes.
6. No agregar frameworks no solicitados.
7. No agregar Spring.
8. No agregar Hibernate/JPA.
9. No agregar Lombok.
10. No convertir el proyecto a Spring Boot.
11. No agregar patrones arquitectónicos innecesarios.
12. Mantener el proyecto comprensible para estudiantes de POO.
13. Priorizar Java estándar, JDBC y Maven.
14. No implementar funcionalidades fuera del alcance.

Después de cualquier modificación importante ejecutar:

mvn clean compile

El proyecto debe terminar con:

BUILD SUCCESS

## 17. Git

No trabajar directamente sobre main.

Ramas principales:

main
develop

Las funcionalidades deben desarrollarse en ramas:

feature/...

Ejemplos:

feature/modelos
feature/conexion-db
feature/dao
feature/sesion
feature/controllers
feature/main-t2
feature/javafx

Las ramas feature deben integrarse primero en:

develop

y únicamente una versión estable pasa posteriormente a:

main