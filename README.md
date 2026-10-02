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
- **Ambos**: ver la lista de clientes para elegir uno (no permite al
  administrador registrar ni modificar clientes).

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

No se usan JUnit, Mockito ni otros frameworks de pruebas: la verificación es
compilar y ejecutar los casos de consola con `main`. La dependencia `junit 4.11`
del `pom.xml` y `src/test/.../AppTest.java` vienen del arquetipo de Maven y se
conservan sin uso.

---

## 3. Estructura

```
src/main/java/com/tpoo/upn/
├── app/          Casos Recepcion*/Admin* (cada uno con su main), Consola,
│                 PruebaConexion y PruebaSesion
├── controller/   UsuarioController, ClienteController, MembresiaController, IngresoController
├── service/      UsuarioService, ClienteService, MembresiaService, IngresoService
├── dao/          ConexionDB + los cinco DAO (único lugar con SQL)
├── session/      Sesion (usuario autenticado)
└── model/        Persona (abstracta), Cliente, Usuario, TipoMembresia, Membresia, Ingreso

database/         Create.sql, Insert.sql, Test.sql
docs/diagramas/   Diagrama de clases UML, MER e informe PDF
```

Cada operación sigue el camino **presentación → controller → service → DAO**:

- La **presentación** (hoy las clases de `app`; después las vistas JavaFX) lee
  datos, llama al controlador y muestra el resultado.
- Los **controladores** reciben los datos y delegan en su servicio. No leen
  teclado, no imprimen y no tienen reglas ni SQL; por eso la futura AppGUI podrá
  usarlos tal cual.
- Los **servicios** comprueban permisos y reglas del gimnasio.
- Los **DAO** son el único lugar con SQL. `ConexionDB` está en el paquete `dao`
  (en el UML aparece agrupada como «conexion»).
- En cada ejecución se crea **una** `Sesion`, se comparte entre los servicios y
  cada servicio se entrega a su controlador:

```java
Sesion sesion = new Sesion();
UsuarioController usuarioController = new UsuarioController(new UsuarioService(sesion));
IngresoController ingresoController = new IngresoController(new IngresoService(sesion));
```

`Persona` es abstracta y **no** tiene tabla propia; solo representa la herencia en Java.

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

## 5. Casos de consola (carpeta `app`)

La consola es **conceptual**: muestra cinco casos de uso que recorren
presentación → controller → service → DAO. La aplicación completa se hará en la
interfaz gráfica (JavaFX), que usará los mismos controladores y servicios. Las
operaciones sin caso de consola (renovar o registrar membresías, actualizar
clientes, activar/desactivar cuentas, registrar tipos) ya existen en los
controladores y servicios y se mostrarán en esa interfaz.

Cada caso es un programa pequeño con su propio `main`: pide usuario y contraseña,
realiza una operación y cierra la sesión. Los clientes se eligen **por número**
de una lista; no hace falta saber DNI ni consultar MySQL:

```text
Clientes registrados:
1. Ana Torres - DNI 12345678
2. Luis Quispe - DNI 23456789
0. Cancelar
Opcion:
```

Si se escribe algo que no es un número de la lista, el programa lo indica y
vuelve a preguntar.

### 5.1 Resumen y qué guarda cada uno

| # | Clase | Rol | Caso | Guarda en la base |
|---|---|---|---|---|
| 1 | `RecepcionMarcarAsistencia` | Recepcionista | Inicia sesión y **marca la asistencia** de un cliente | 1 ingreso, solo si respondes `s` y tiene membresía vigente |
| 2 | `RecepcionConsultarMembresia` | Recepcionista | Verifica el **estado de la membresía** de un cliente | Nada |
| 3 | `RecepcionRegistrarCliente` | Recepcionista | **Registra un cliente nuevo** | 1 cliente |
| 4 | `AdminConsultas` | Administrador | Membresías **próximas a vencer** o **historial** de ingresos | Nada |
| 5 | `AdminCrearUsuario` | Administrador | **Crea un usuario** nuevo | 1 cuenta |
| — | `PruebaConexion` | — | Abre y cierra una conexión con MySQL | Nada |
| — | `PruebaSesion` | — | La clase `Sesion` en memoria (no usa MySQL) | Nada |

Ningún caso borra ni modifica registros existentes.

### 5.2 Datos previos necesarios

1. MySQL iniciado y la base `ironforge_gym` creada (sección 4.1).
2. `ConexionDB` con los datos de tu MySQL (sección 4.2) y `mvn clean compile` en
   `BUILD SUCCESS`.
3. `PruebaConexion` debe mostrar `Conexion exitosa a la base de datos ironforge_gym`.
4. Una cuenta RECEPCIONISTA y una ADMINISTRADOR activas (las de `Insert.sql`
   sirven, sección 5.9).
5. Clientes con membresías: los de `Insert.sql`. Un cliente registrado con el
   caso 3 aparece en las listas, pero **no podrá marcar asistencia** hasta tener
   una membresía, que desde consola no se registra (se hará en la interfaz
   gráfica).

### 5.3 Cómo ejecutar un caso

**Desde VS Code:** abre la clase (por ejemplo `RecepcionMarcarAsistencia.java`) y pulsa **Run** encima
del método `main`. Los datos se escriben en la terminal que se abre.

**Desde la terminal (PowerShell):**

```powershell
mvn exec:java "-Dexec.mainClass=com.tpoo.upn.app.RecepcionMarcarAsistencia"
mvn exec:java "-Dexec.mainClass=com.tpoo.upn.app.RecepcionConsultarMembresia"
mvn exec:java "-Dexec.mainClass=com.tpoo.upn.app.RecepcionRegistrarCliente"
mvn exec:java "-Dexec.mainClass=com.tpoo.upn.app.AdminConsultas"
mvn exec:java "-Dexec.mainClass=com.tpoo.upn.app.AdminCrearUsuario"
```

Las comillas son **obligatorias** en PowerShell.

**Sin Maven exec** (después de `mvn clean compile`):

```powershell
java -cp "target/classes;$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\9.1.0\mysql-connector-j-9.1.0.jar" com.tpoo.upn.app.RecepcionMarcarAsistencia
```

No hay contraseñas escritas en el código. La contraseña se ve mientras se escribe.

### 5.4 Caso 1 — `RecepcionMarcarAsistencia`

1. Usuario y contraseña de un **recepcionista**.
2. Elegir el cliente por número (`0` cancela sin guardar nada).
3. El programa muestra sus membresías con su estado de hoy.
4. `Registrar el ingreso de <cliente>? (s/n)`: `n` termina sin guardar.

Con membresía vigente:

```text
INGRESO REGISTRADO
   Cliente: Ana Torres
   Fecha y hora: 02/10/2026 10:15:30
   Membresia usada: Mensual (vence 2026-10-22)
   Registrado por: <username>
Sesion cerrada.
```

Sin membresía vigente: `INGRESO RECHAZADO: El cliente no tiene una membresia vigente. No se guardo ningun registro.`

### 5.5 Caso 2 — `RecepcionConsultarMembresia`

Elige el cliente y verás sus períodos con el estado de hoy:

```text
Cliente: Diego Flores - DNI 67890123
Membresias (estado a la fecha de hoy):
   Mensual | del 2026-07-20 al 2026-08-19 | VENCIDA
   Mensual | del 2026-09-18 al 2026-10-18 | VIGENTE
```

### 5.6 Caso 3 — `RecepcionRegistrarCliente`

Pide DNI, nombres, apellidos y teléfono (Enter si no tiene). Resultado:
`CLIENTE REGISTRADO: <nombre> - DNI <dni> - tel. <telefono>`. Un DNI repetido o
mal escrito, o datos vacíos, se explican con `Operacion no realizada: <motivo>`
y no se guarda nada.

### 5.7 Caso 4 — `AdminConsultas`

- **1 Próximas a vencer**: membresías vigentes que vencen en los próximos 7 días,
  o `No existen membresias proximas a vencer.`
- **2 Historial**: elige el cliente; muestra sus ingresos con fecha, hora y quién
  los registró, o `No existen ingresos registrados para este cliente.`

### 5.8 Caso 5 — `AdminCrearUsuario`

Pide nombres, apellidos, username nuevo, contraseña y el rol por número
(1 Recepcionista, 2 Administrador, 0 Cancelar). Resultado:
`CUENTA CREADA: <username> (<nombre>) como <ROL>, activa: true`.

### 5.9 Datos de `Insert.sql`

| Username | Rol | Estado |
|---|---|---|
| `admin01` | ADMINISTRADOR | activo |
| `recep01` | RECEPCIONISTA | activo |
| `recep02` | RECEPCIONISTA | **inactivo** (no puede iniciar sesión) |

Las contraseñas de prueba están en `database/Insert.sql`; los casos no las cambian.

Clientes: Ana Torres, Luis Quispe, Carla Mendoza, Pedro Vargas, Rosa Salas y
Diego Flores (con una renovación). Sus estados (vigente, vencida, aún no vigente)
se calcularon respecto del día en que se cargó el script, así que pueden haber
cambiado; los casos 1 y 2 los muestran al elegir el cliente.

### 5.10 Cómo leer los resultados

| Mensaje | Significado |
|---|---|
| `INGRESO REGISTRADO`, `CLIENTE REGISTRADO`, `CUENTA CREADA` | La operación se guardó. |
| `INGRESO RECHAZADO: <motivo>` | El servicio no autorizó el ingreso; no se guardó nada. |
| `Operacion no realizada: <motivo>` | Datos inválidos, credenciales incorrectas o rol sin permiso. |
| `Error de base de datos...` | Problema de conexión o de MySQL, no una regla de negocio. Ver sección 10. |

---

## 6. Verificaciones

**Realizadas** (2026-10-02):

- `mvn clean compile` → `BUILD SUCCESS`.
- Revisión del código: los casos solo crean los servicios y ejecutan las
  operaciones mediante los controladores; los controladores no usan `Scanner`,
  `System.out`, JavaFX ni SQL.
- `PruebaSesion` y `PruebaConexion` ejecutadas.
- Recorridos **sin escritura** contra la base local, con entrada por tubería:
  `RecepcionMarcarAsistencia`, antes `Main` (cancelar con `0`; elegir cliente y responder `n`; opción no válida;
  administrador rechazado al consultar vigencia), `RecepcionConsultarMembresia`,
  `AdminConsultas` opciones 1 y 2, `RecepcionRegistrarCliente` con DNI inválido
  (rechazado antes de guardar) y `AdminCrearUsuario` cancelando el rol.

**Pendientes** (guardan datos; ejecutar solo sobre la base que el equipo confirme):

- `RecepcionMarcarAsistencia` confirmando un ingreso.
- `RecepcionRegistrarCliente` con datos válidos.
- `AdminCrearUsuario` eligiendo un rol.

---

## 7. Ampliación pendiente de reflejar en UML e informe

`ClienteService.listarClientes()` y `ClienteController.listarClientes()`
(`List<Cliente>`) se agregaron para elegir clientes de una lista en lugar de
escribir su DNI. Reutilizan `ClienteDAO.listar()` y están permitidas a ambos roles.
La clase auxiliar `app.Consola` (lectura y listas numeradas) y los casos
`Recepcion*`/`Admin*` tampoco figuran en el UML; son entradas de consola, como
`PruebaConexion` y `PruebaSesion`.

El UML muestra `app.Main` usando los cuatro controladores. Por ahora esa clase no
existe: el caso de marcar asistencia se llama `RecepcionMarcarAsistencia` y el
nombre `Main` queda reservado para la fase gráfica, donde será el punto de
entrada que crea la `Sesion`, los servicios y los controladores y abre la ventana.

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
5. **No hay una base de pruebas separada**: los casos que guardan datos
   escriben en la base a la que apunta `ConexionDB`.
6. **Desde la consola no se registran membresías**: un cliente nuevo no puede
   marcar asistencia hasta que la interfaz gráfica permita asignarle una.

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
| 6 | Los cuatro servicios | ✅ compila |
| 6b | Los cuatro controladores (delegan en los servicios) | ✅ compila · lecturas ejecutadas |
| 7 | Cinco casos de consola (`Recepcion*`, `Admin*`) | ✅ compila · casos que guardan datos pendientes de ejecutar |
| 8 | `Main` + AppGUI JavaFX (reutilizará controladores y servicios) | ⏳ pendiente |

---

## 10. Problemas frecuentes

| Síntoma | Causa y solución |
|---|---|
| `Unknown lifecycle phase ".mainClass=..."` | Faltan las comillas en PowerShell. Usa `mvn exec:java "-Dexec.mainClass=..."` |
| `Unknown database 'ironforge_gym'` | No se ejecutó `Create.sql`, o el nombre en `ConexionDB` no coincide. |
| `Access denied for user...` | Usuario o contraseña incorrectos en `ConexionDB`. |
| `Communications link failure` | El servicio de MySQL no está iniciado. |
| `Source option 7 is no longer supported` | Maven no recargó el `pom.xml`. Vuelve a ejecutar `mvn clean compile`. |
| `No hay clientes registrados...` | Registra uno con `RecepcionRegistrarCliente`. |
| `INGRESO RECHAZADO: El cliente no tiene una membresia vigente` | Es correcto si el caso 2 muestra que no tiene membresía `VIGENTE`. |
| `El DNI ya esta registrado` | Es correcto: escribe un DNI distinto. No sobrescribe clientes existentes. |

---

## 11. Git

Ramas: `main` (estable) y `develop` (integración). Las funcionalidades se desarrollan
en ramas `feature/...`, se integran en `develop` y solo una versión estable pasa a `main`.
