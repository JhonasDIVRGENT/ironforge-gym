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
├── app/          Main y demostraciones Prueba* (cada una con su propio main)
├── model/        Persona (abstracta), Cliente, Usuario, TipoMembresia, Membresia, Ingreso
├── service/      UsuarioService, ClienteService, MembresiaService, IngresoService
├── dao/          ConexionDB + los cinco DAO (único lugar con SQL)
└── session/      Sesion (usuario autenticado)

database/         Create.sql, Insert.sql, Test.sql
docs/diagramas/   Diagrama de clases, MER e informe PDF
```

Reglas de la arquitectura:

- El SQL vive **solo** en los DAO. Modelos, servicios y demostraciones no lo tocan.
- Las reglas del gimnasio y los permisos están en los servicios; la futura
  interfaz JavaFX usará esos mismos servicios.
- `Persona` es abstracta y **no** tiene tabla propia; solo representa la herencia en Java.
- Las relaciones entre entidades se representan con referencias a objetos.
- En cada ejecución, una sola instancia de `Sesion` se comparte entre los servicios.

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

## 5. Demostraciones (carpeta `app`)

No hay un menú general: cada funcionalidad tiene un programa pequeño con su propio
`main`, dentro de `src/main/java/com/tpoo/upn/app/`. Todos usan los servicios; ninguno
tiene SQL ni llama a los DAO.

| Clase | Qué demuestra | Rol | Registra en la base |
|---|---|---|---|
| `Main` | Secuencia principal: sesión, cliente, búsqueda, membresía, vigencia, ingreso, cierre (RF-10, 01, 02, 05, 04, 06, 07, 08) | RECEPCIONISTA | 1 cliente, 1 membresía, 1 ingreso |
| `PruebaClientes` | Registro, búsqueda, actualización, DNI duplicado y datos vacíos (RF-01, 02, 03) | RECEPCIONISTA | 1 cliente |
| `PruebaMembresias` | Tipos, registro, fechas inválidas, renovación y vigencia (RF-15, 05, 12, 04) | RECEPCIONISTA | 2 membresías |
| `PruebaIngresos` | Ingreso rechazado y autorizado, usuario y fecha/hora (RF-06, 07, 08) | RECEPCIONISTA | 1 ingreso |
| `PruebaUsuarios` | Sesión, crear cuenta, desactivar/activar, cuenta inactiva, sin permiso (RF-10, 13, 14, 16) | ADMINISTRADOR | 1 cuenta de demostración |
| `PruebaConsultas` | Historial, próximas a vencer, tipos (RF-09, 11, 15) | ADMINISTRADOR | Nada, salvo un tipo nuevo opcional |
| `PruebaConexion` | Abre y cierra una conexión con MySQL | — | Nada |
| `PruebaSesion` | La clase `Sesion` en memoria (no usa MySQL) | — | Nada |

### 5.1 Antes de empezar

1. MySQL debe estar iniciado y la base `ironforge_gym` creada (sección 4.1).
2. `ConexionDB` debe tener los datos de tu MySQL (sección 4.2).
3. Compila con `mvn clean compile`. Debe terminar en `BUILD SUCCESS`.
4. Comprueba la conexión ejecutando `PruebaConexion`. Debe mostrar
   `Conexion exitosa a la base de datos ironforge_gym`.
5. Ten a mano una cuenta RECEPCIONISTA y una ADMINISTRADOR activas
   (las de `Insert.sql` sirven; ver 5.11).

⚠️ Salvo `PruebaConexion`, `PruebaSesion` y `PruebaConsultas` (si no registras un
tipo), las demostraciones **guardan datos nuevos** en la base a la que apunta
`ConexionDB`. Ninguna borra ni modifica registros que ya existían.

### 5.2 Cómo ejecutar una demostración

**Desde VS Code:** abre la clase (por ejemplo `PruebaClientes.java`) y pulsa
**Run** encima del método `main`. Los datos se escriben en la terminal que se abre.

**Desde la terminal (PowerShell):**

```powershell
mvn exec:java "-Dexec.mainClass=com.tpoo.upn.app.PruebaClientes"
```

Cambia el nombre de la clase para ejecutar las demás. Las comillas son
**obligatorias** en PowerShell.

**Sin Maven exec** (después de `mvn clean compile`):

```powershell
java -cp "target/classes;$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\9.1.0\mysql-connector-j-9.1.0.jar" com.tpoo.upn.app.PruebaClientes
```

Cada programa pide usuario y contraseña por teclado; no hay contraseñas escritas
en el código. La contraseña se ve mientras se escribe.

### 5.3 Recorrido recomendado

Este orden usa solo clientes **creados por las propias demostraciones**, así el
resultado no depende de las fechas de `Insert.sql`:

| Paso | Programa | Datos que escribes |
|---|---|---|
| 1 | `PruebaClientes` | DNI `55550001` → cliente **sin** membresía |
| 2 | `PruebaClientes` | DNI `55550002` → cliente que tendrá membresía |
| 3 | `PruebaMembresias` | DNI `55550002` |
| 4 | `PruebaIngresos` | Sin vigente: `55550001` · Con vigente: `55550002` |
| 5 | `PruebaConsultas` | Historial de `55550002` · Enter en el tipo nuevo |
| 6 | `PruebaUsuarios` | Username nuevo: `demo_recep01` |
| 7 | `Main` | DNI `55550003` |

Para repetir el recorrido otro día usa DNI y username que no existan
(`55550004`, `55550005`…, `demo_recep02`…).

### 5.4 `Main` — demostración principal

Pide, en este orden:

1. `Username del recepcionista:` y `Contrasena (visible al escribir):`
2. `DNI nuevo del cliente (8 digitos, que no exista):`

Resultado esperado:

```text
1. Sesion iniciada: <nombre> (RECEPCIONISTA)
2. Cliente registrado correctamente.
3. Busqueda por DNI: Cliente Demostracion | DNI <dni>
4. Membresia <tipo> registrada del <hoy> al <hoy + 29 dias>
5. Vigencia de la membresia <id>: VIGENTE
6. Ingreso registrado el <fecha y hora> por <username> con la membresia <id>
7. Sesion cerrada.
```

Usa el primer tipo de membresía de la lista. Si el DNI ya existe, se detiene en el
paso 2 con `Demostracion detenida: El DNI ya esta registrado` y no guarda nada.

### 5.5 `PruebaClientes`

Pide: usuario y contraseña de un **recepcionista** y un **DNI nuevo**.

Resultado esperado:

```text
1. Busqueda previa: Cliente no encontrado.
2. Registrado y encontrado: Cliente Prueba Clientes | tel. 999000111 | id <id>
3. Actualizado: Cliente Prueba Actualizado | tel. 999000222 | mismo id <id>
4. DNI duplicado rechazado como se esperaba: El DNI ya esta registrado
5. Datos vacios rechazados como se esperaba: Los nombres y apellidos son obligatorios
```

El paso 3 muestra el mismo id: la actualización no crea otro cliente, así que sus
membresías e ingresos se conservan.

### 5.6 `PruebaMembresias`

Pide: usuario y contraseña de un **recepcionista** y el **DNI de un cliente existente**
(por ejemplo, el creado con `PruebaClientes`).

Resultado esperado:

```text
1. Tipos disponibles:
   id <id> | <nombre> | S/ <precio>
2. Registrada membresia <tipo> del <hoy> al <hoy + 29 dias>
3. Fechas invalidas rechazadas como se esperaba: La fecha de fin no puede ser menor a la fecha de inicio
4. Renovacion registrada del <hoy + 30 dias> al <hoy + 59 dias>
5. Vigencia hoy (los periodos anteriores se conservan):
   id <id> | <inicio> a <fin> | VIGENTE
   id <id> | <inicio> a <fin> | AUN_NO_VIGENTE
```

En el paso 5 aparecen también las membresías anteriores del cliente, si las tenía.

### 5.7 `PruebaIngresos`

Pide: usuario y contraseña de un **recepcionista**, luego:

1. `DNI de un cliente SIN membresia vigente:` (en el recorrido, `55550001`)
2. `DNI de un cliente CON membresia vigente:` (en el recorrido, `55550002`)

Resultado esperado:

```text
1. Ingreso rechazado como se esperaba: El cliente no tiene una membresia vigente
2. Ingreso autorizado con id <id> para <cliente>, membresia <id> (vence <fecha>)
3. Registrado por <username> (usuario de la sesion)
   Fecha y hora <fecha y hora> (reloj del sistema)
```

El intento del paso 1 no deja ningún registro. Si el primer DNI no existe, el
programa avisa `Cliente no encontrado` y termina sin registrar nada.

### 5.8 `PruebaUsuarios`

Pide: usuario y contraseña de un **administrador**, luego un **username nuevo** y una
contraseña para la cuenta de demostración.

Resultado esperado:

```text
1. Sesion iniciada como ADMINISTRADOR
2. Cuenta demo_recep01 creada como RECEPCIONISTA, activa: true
3. Cuenta demo_recep01 desactivada.
4. Acceso rechazado como se esperaba: La cuenta esta inactiva
5. Cuenta reactivada: sesion iniciada como RECEPCIONISTA
6. Operacion sin permiso rechazada como se esperaba: Solo el administrador puede realizar esta operacion
7. Sesion cerrada. Hay sesion activa: false
```

Solo cambia el estado de la cuenta que acaba de crear y la deja **activa** al final.
Si el username ya existe, se detiene en el paso 2 sin tocar ninguna cuenta.

### 5.9 `PruebaConsultas`

Pide: usuario y contraseña de un **administrador**, luego:

1. `DNI para consultar el historial:` (en el recorrido, `55550002`)
2. `Nombre de un tipo NUEVO (Enter para no registrar ninguno):`
3. Solo si escribiste un nombre: `Precio, por ejemplo 99.90:`

Resultado esperado:

```text
1. Sesion iniciada como ADMINISTRADOR
2. Historial de ingresos (<cantidad>):
   <fecha y hora> | registrado por <username>
3. Membresias vigentes que vencen en los proximos 7 dias:
   <cliente> | DNI <dni> | vence <fecha>
4. Tipos de membresia:
   id <id> | <nombre> | S/ <precio>
```

Si no hay resultados muestra `No existen ingresos registrados para este cliente` o
`No existen membresias proximas a vencer`. Pulsando Enter en el dato 2 el programa
solo lee información. Si escribes un nombre, registra el tipo (paso 5) y vuelve a
mostrar la lista.

### 5.10 Cómo leer los resultados

| Mensaje | Significado |
|---|---|
| `... rechazado como se esperaba: <motivo>` | La regla del servicio impidió la operación. Es lo correcto. |
| `FALLO: ...` | Se aceptó algo que debía rechazarse. Hay un error en las reglas. |
| `Demostracion detenida: <motivo>` | Una operación necesaria fue rechazada (credenciales incorrectas, rol equivocado, DNI ya registrado…). Revisa los datos y vuelve a ejecutar. |
| `Error de base de datos...` | Problema de conexión o de MySQL, no una regla de negocio. Ver sección 8. |

### 5.11 Datos de `Insert.sql`

| Username | Rol | Estado |
|---|---|---|
| `admin01` | ADMINISTRADOR | activo |
| `recep01` | RECEPCIONISTA | activo |
| `recep02` | RECEPCIONISTA | **inactivo** |

Las contraseñas de prueba están en `database/Insert.sql`.

Clientes: DNIs `12345678` (Ana), `23456789` (Luis), `34567890` (Carla), `45678901`
(Pedro), `56789012` (Rosa) y `67890123` (Diego, con una renovación). Sus estados
(vigente, vencida, aún no vigente) se calcularon respecto del día en que se cargó el
script, así que pueden haber cambiado. Compruébalos con la consulta 5 de `Test.sql`
antes de usarlos en `PruebaIngresos`. No los uses como DNI nuevo.

---

## 6. Limitaciones conocidas



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

## 7. Estado del proyecto

| Fase | Contenido | Estado |
|---|---|---|
| 0 | Diseño de base de datos (MER) | ✅ |
| 1 | Modelos | ✅ |
| 2 | Scripts MySQL | ✅ |
| 3 | ConexionDB | ✅ |
| 4 | Los cinco DAO | ✅ |
| 5 | Sesion | ✅ |
| 6 | Los cuatro servicios (UML Sem07) | ✅ compila |
| 7 | Main breve y demostraciones Prueba* | ✅ compila · ejecución con MySQL pendiente |
| 8 | JavaFX | ⏳ pendiente |



---

## 8. Problemas frecuentes

| Síntoma | Causa y solución |
|---|---|
| `Unknown lifecycle phase ".mainClass=..."` | Faltan las comillas en PowerShell. Usa `mvn exec:java "-Dexec.mainClass=..."` |
| `Unknown database 'ironforge_gym'` | No se ejecutó `Create.sql`, o el nombre en `ConexionDB` no coincide. |
| `Access denied for user...` | Usuario o contraseña incorrectos en `ConexionDB`. |
| `Communications link failure` | El servicio de MySQL no está iniciado. |
| `Source option 7 is no longer supported` | Maven no recargó el `pom.xml`. Vuelve a ejecutar `mvn clean compile`. |
| La demostración dice que el DNI ya está registrado | Es correcto: escribe un DNI distinto. No sobrescribe clientes existentes. |

---

## 9. Git

Ramas: `main` (estable) y `develop` (integración). Las funcionalidades se desarrollan
en ramas `feature/...`, se integran en `develop` y solo una versión estable pasa a `main`.
