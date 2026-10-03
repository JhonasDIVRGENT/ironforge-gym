# IronForge Gym

Proyecto final del curso Técnicas de Programación Orientada a Objetos (UPN, 2026).
Grupo 15. Docente: MSc. Victor Muguerza.

Integrantes:

- Jonathan Calderón Gamarra (N00571575)
- Karla Mariel Quispe Padilla (N00353037)
- Andre Aaron Colchado Robles (N00418772)
- Dara Rina Borja Goñi (N00422554)

Es un sistema de escritorio para el gimnasio IronForge: registra clientes,
membresías e ingresos, y controla qué puede hacer cada usuario según su rol
(recepcionista o administrador). Está hecho en Java 21 con JavaFX y MySQL.

Los requerimientos, las historias de usuario y el diagrama de clases están en
el informe: `docs/diagramas/Avance_Sem7_InformeProyectoFinal__Grupo15_IronForgeAPP.pdf`.

## Qué se necesita

- JDK 21
- Maven
- MySQL 8
- VS Code con Extension Pack for Java (opcional, también funciona por consola)

## Cómo ejecutarlo

### 1. Crear la base de datos

Los scripts están en la carpeta `database`. En MySQL Workbench ejecutar, en este orden:

1. `Create.sql`: crea la base `ironforge_gym` y sus 5 tablas.
2. `Insert.sql`: carga usuarios, clientes, tipos y membresías de prueba.

Ojo: `Create.sql` empieza con `DROP DATABASE`, así que borra la base si ya existía.

### 2. Poner los datos de su MySQL

Abrir `src/main/java/com/tpoo/upn/dao/ConexionDB.java` y cambiar estos tres
valores por los de su MySQL:

```java
private static final String URL     = "jdbc:mysql://localhost:3306/ironforge_gym";
private static final String USUARIO = "root";              // su usuario de MySQL
private static final String CLAVE   = "su_contraseña";      // su contraseña de MySQL
```

Si su MySQL usa otro puerto, cambiar `3306` en la URL.

### 3. Abrir la interfaz gráfica

Desde la carpeta del proyecto:

```powershell
mvn clean compile
mvn javafx:run
```

En VS Code también se puede abrir `src/main/java/com/tpoo/upn/app/Main.java` y
darle **Run**. Hay que ejecutar `Main`, no `AppGUI`.

### 4. Iniciar sesión

`Insert.sql` crea estas cuentas (las contraseñas están en el mismo archivo):

| Usuario | Rol | Qué puede hacer |
| --- | --- | --- |
| `recep01` | Recepcionista | Clientes, membresías e ingresos |
| `admin01` | Administrador | Consultas, tipos de membresía y usuarios |
| `recep02` | Recepcionista | Está inactiva, sirve para ver que el login la rechaza |

## Programas de consola

Además de la interfaz, en `src/main/java/com/tpoo/upn/app` hay cinco programas
pequeños que hacen lo mismo desde la terminal: `RecepcionMarcarAsistencia`,
`RecepcionConsultarMembresia`, `RecepcionRegistrarCliente`, `AdminConsultas` y
`AdminCrearUsuario`. Se ejecutan con **Run** en VS Code o así:

```powershell
mvn exec:java "-Dexec.mainClass=com.tpoo.upn.app.RecepcionMarcarAsistencia"
```

`PruebaConexion` sirve para comprobar que la conexión a MySQL funciona.

## Organización del código

```text
src/main/java/com/tpoo/upn/
├── app/       Main, AppGUI y los programas de consola
├── gui/       controladores de las pantallas (XxxViewController)
├── service/   reglas del gimnasio y permisos por rol
├── dao/       ConexionDB y las consultas SQL
├── session/   usuario que inició sesión
└── model/     Persona, Cliente, Usuario, TipoMembresia, Membresia, Ingreso
src/main/resources/com/tpoo/upn/   pantallas (.fxml), estilos (.css) e imágenes
```

Las pantallas y la consola llaman a los servicios, y los servicios usan los DAO.
Todo el SQL está en el paquete `dao`.

## Si algo falla

| Error | Solución |
| --- | --- |
| `Access denied for user` | Revisar usuario y contraseña en `ConexionDB`. |
| `Unknown database 'ironforge_gym'` | Falta ejecutar `Create.sql`. |
| `Communications link failure` | MySQL no está iniciado. |
| `JavaFX runtime components are missing` | Se ejecutó `AppGUI`; ejecutar `Main` o `mvn javafx:run`. |

Repositorio: <https://github.com/JhonasDIVRGENT/ironforge-gym>
