# IronForge Gym

Proyecto académico de gestión de gimnasio para el curso de Técnicas de Programación
Orientada a Objetos de UPN. Grupo 15.

Aplicación de escritorio en Java que gestiona clientes, membresías, ingresos al
gimnasio y usuarios del sistema, con control de acceso por rol y persistencia en MySQL.

---

## 1. Qué hace el programa

| Área | Operaciones |
|---|---|
| **Clientes** | Registrar, buscar por DNI y actualizar datos. El DNI es único y debe tener 8 dígitos. |
| **Membresías** | Registrar un período, renovar (se inserta uno nuevo y se conservan los anteriores) y consultar la vigencia. |
| **Tipos de membresía** | Registrar y listar (mensual, trimestral, semestral…). El nombre es único. |
| **Ingresos** | Registrar el acceso de un cliente solo si tiene una membresía vigente, y consultar su historial. |
| **Usuarios** | Iniciar sesión, crear cuentas del personal y activarlas o desactivarlas. |
| **Permisos** | Cada operación está restringida al rol que corresponde. |

### Roles

- **RECEPCIONISTA**: registrar, buscar y actualizar clientes; registrar y renovar
  membresías; consultar vigencia; registrar ingresos; consultar tipos.
- **ADMINISTRADOR**: crear usuarios y cambiar su estado; consultar historiales de
  ingresos; consultar membresías próximas a vencer; registrar tipos de membresía.

Los roles **no** se heredan: un administrador no puede registrar clientes ni ingresos,
y un recepcionista no puede consultar historiales.

### Estado de una membresía

No se guarda en la base de datos: se **calcula** comparando una fecha con el período.

- fecha < fechaInicio → `AUN_NO_VIGENTE`
- fechaInicio ≤ fecha ≤ fechaFin → `VIGENTE`
- fecha > fechaFin → `VENCIDA`

---

## 2. Tecnologías

- Java 21
- Maven
- MySQL 8 + JDBC (`com.mysql:mysql-connector-j:9.1.0`)
- JavaFX — pendiente, fase final

---

## 3. Estructura

```
src/main/java/com/tpoo/upn/
├── app/          Main (demostración por consola), PruebaConexion, PruebaSesion
├── model/        Persona (abstracta), Cliente, Usuario, TipoMembresia, Membresia, Ingreso
├── dao/          ConexionDB + los cinco DAO (único lugar con SQL)
├── controller/   UsuarioController, ClienteController, MembresiaController, IngresoController
└── session/      Sesion (usuario autenticado)

database/         Create.sql, Insert.sql, Test.sql
docs/diagramas/   Diagrama de clases, MER e informe PDF
```

Reglas de la arquitectura:

- El SQL vive **solo** en los DAO. Modelos, controladores y `Main` no lo tocan.
- `Persona` es abstracta y **no** tiene tabla propia; solo representa la herencia en Java.
- Las relaciones entre entidades se representan con referencias a objetos.
- Una sola instancia de `Sesion` se comparte entre los cuatro controladores.

---

## 4. Puesta en marcha

### 4.1 Crear la base de datos

⚠️ **`Create.sql` empieza con `DROP DATABASE`: borra todo lo que exista.**
Ejecútalo solo la primera vez o cuando quieras empezar de cero.

En MySQL Workbench o en la consola de MySQL, en este orden:

```sql
-- 1. Crea la base y las cinco tablas
source database/Create.sql;

-- 2. Carga los datos de prueba (usuarios, clientes, tipos, membresías, ingresos)
source database/Insert.sql;

-- 3. Opcional: consultas de comprobación
source database/Test.sql;
```

### 4.2 Configurar las credenciales

Abre `src/main/java/com/tpoo/upn/dao/ConexionDB.java` y ajusta:

```java
private static final String URL     = "jdbc:mysql://localhost:3306/ironforge_gym";
private static final String USUARIO = "root";
private static final String CLAVE   = "tu_clave_de_mysql";
```

### 4.3 Compilar

```sh
mvn clean compile
```

Debe terminar en `BUILD SUCCESS`.

---

## 5. Comandos para probar

En PowerShell las comillas alrededor de `-Dexec.mainClass=...` son **obligatorias**;
sin ellas PowerShell parte el argumento y Maven da el error
`Unknown lifecycle phase ".mainClass=..."`.

### Probar solo la conexión

```powershell
mvn exec:java "-Dexec.mainClass=com.tpoo.upn.app.PruebaConexion"
```

Salida esperada: `Conexion exitosa a la base de datos ironforge_gym`

### Probar la clase Sesion (sin MySQL)

```powershell
mvn exec:java "-Dexec.mainClass=com.tpoo.upn.app.PruebaSesion"
```

Recorre ocho casos: sesión vacía, inicio con cada rol, cierre, rechazo de usuario
nulo e inactivo y consulta de roles sin sesión.

### Demostración completa de los requerimientos

```powershell
mvn exec:java "-Dexec.mainClass=com.tpoo.upn.app.Main"
```

---

## 6. Datos que hay que introducir en la demostración

`Main` pide seis datos, en este orden:

| # | Dato | Qué escribir |
|---|---|---|
| 1 | Username del recepcionista | `recep01` |
| 2 | Contraseña | `recep123` |
| 3 | DNI nuevo del cliente de demostración | 8 dígitos que **no existan** todavía, p. ej. `55550001` |
| 4 | Id del tipo de membresía | Uno de los que aparecen en la lista que imprime (con `Insert.sql`: 1, 2 o 3) |
| 5 | Username del administrador | `admin01` |
| 6 | Contraseña | `admin123` |

Nombres, apellidos y teléfono del cliente son ficticios y los pone el propio programa.

### Cuentas cargadas por `Insert.sql`

| Username | Contraseña | Rol | Estado |
|---|---|---|---|
| `admin01` | `admin123` | ADMINISTRADOR | activo |
| `recep01` | `recep123` | RECEPCIONISTA | activo |
| `recep02` | `recep456` | RECEPCIONISTA | **inactivo** (sirve para probar el rechazo del RF-10) |

### Clientes de prueba

DNIs `12345678` (Ana, vigente), `23456789` (Luis, vencida), `34567890` (Carla, aún no
vigente), `45678901` (Pedro, vigente), `56789012` (Rosa, próxima a vencer) y
`67890123` (Diego, con una renovación).

**No los uses como DNI de demostración**: ya existen y el programa te pedirá otro.

---

## 7. Qué demuestra `Main`

Once pasos seguidos que cubren nueve requerimientos funcionales:

| Paso | RF | Qué demuestra |
|---|---|---|
| 1 | RF-10 | Inicio de sesión del recepcionista |
| 2 | RF-01 | Registro de un cliente, con el ID que genera MySQL |
| 3 | RF-02 | Búsqueda por DNI |
| 4 | RF-07 | **Rechazo** de un ingreso sin membresía vigente |
| 5 | RF-05 | Registro de una membresía (hoy → hoy + 30 días) |
| 6 | RF-04 | Consulta de vigencia con el estado calculado |
| 7 | RF-06/07/08 | Ingreso autorizado, con fecha y empleado puestos por el controlador |
| 8 | RF-01 | **Rechazo** de un DNI duplicado |
| 9 | RF-16 | **Rechazo** por falta de permisos (recepcionista pidiendo un historial) |
| 10 | — | Cierre de sesión y cambio a administrador |
| 11 | RF-09 | Historial del cliente, comprobando que el intento del paso 4 no dejó registro |

Al terminar imprime un resumen con el resultado real de cada paso. Si alguno falla,
lo marca como `FALLO`; nunca afirma que todo funcionó si no fue así.

⚠️ **La demostración crea datos reales** (un cliente, una membresía y un ingreso) y
los deja guardados para que puedan comprobarse después.

### Repetir la demostración

Ejecuta el mismo comando y **escribe un DNI distinto** (`55550002`, `55550003`…).
Cada corrida crea sus propios registros y **no borra ni modifica los anteriores**.

---

## 8. Limitaciones conocidas



1. **Las contraseñas se guardan y comparan en texto plano**, tal como vienen en
   `Insert.sql`. Lo correcto sería almacenar un hash con salt (BCrypt o similar).
2. **La contraseña es visible al escribirla.** Se usó `Scanner` en lugar de
   `System.console().readPassword()` porque esa API no acepta teclas en algunas
   terminales de Windows.
3. **Las credenciales de MySQL están en el código**, dentro de `ConexionDB`. En un
   proyecto real irían en un archivo de configuración fuera del control de versiones.
4. **`precio` usa `double`** en lugar de `BigDecimal`, siguiendo el UML. Suficiente
   aquí, pero `BigDecimal` sería lo correcto para dinero.

---

## 9. Estado del proyecto

| Fase | Contenido | Estado |
|---|---|---|
| 0 | Diseño de base de datos (MER) | ✅ |
| 1 | Modelos | ✅ |
| 2 | Scripts MySQL | ✅ |
| 3 | ConexionDB | ✅ |
| 4 | Los cinco DAO | ✅ |
| 5 | Sesion | ✅ |
| 6 | Los cuatro controladores | ✅ |
| 7 | Main con demostración de los RF | ✅ |
| 8 | JavaFX | ⏳ pendiente |



---

## 10. Problemas frecuentes

| Síntoma | Causa y solución |
|---|---|
| `Unknown lifecycle phase ".mainClass=..."` | Faltan las comillas en PowerShell. Usa `mvn exec:java "-Dexec.mainClass=..."` |
| `Unknown database 'ironforge_gym'` | No se ejecutó `Create.sql`, o el nombre en `ConexionDB` no coincide. |
| `Access denied for user...` | Usuario o contraseña incorrectos en `ConexionDB`. |
| `Communications link failure` | El servicio de MySQL no está iniciado. |
| `Source option 7 is no longer supported` | Maven no recargó el `pom.xml`. Vuelve a ejecutar `mvn clean compile`. |
| La demostración dice que el DNI ya existe | Es correcto: escribe un DNI distinto. No sobrescribe clientes existentes. |

---

## 11. Git

Ramas: `main` (estable) y `develop` (integración). Las funcionalidades se desarrollan
en ramas `feature/...`, se integran en `develop` y solo una versión estable pasa a `main`.
