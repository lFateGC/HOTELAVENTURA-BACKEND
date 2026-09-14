# Hotel Aventura — Backend (API REST Spring Boot)

> **Curso:** Herramientas de Desarrollo (Sección 39171) — Servicio API REST y lógica de negocio para el sistema hotelero Hotel Aventura.  
> **Docente:** Agullas Suares, Marlene Pilar  
> **Año Académico:** 2026  
> **Repositorio Oficial Backend:** [https://github.com/lFateGC/HOTELAVENTURA-BACKEND](https://github.com/lFateGC/HOTELAVENTURA-BACKEND)  
> **Repositorio Oficial Frontend:** [https://github.com/lFateGC/HOTELAVENTURA-FRONTEND](https://github.com/lFateGC/HOTELAVENTURA-FRONTEND)

---

## 1. Encabezado del Proyecto
* **Nombre del Proyecto:** Hotel Aventura — Backend (API REST de Reservas y Gestión de Habitaciones)
* **Curso:** Herramientas de Desarrollo (Sección 39171)
* **Docente:** Agullas Suares, Marlene Pilar
* **Tipo de Aplicación:** API RESTful robusta y desacoplada con persistencia relacional en la nube (Spring Boot 4 + Java 21 LTS)

---

## 2. Descripción del Proyecto
Módulo Backend del sistema **Hotel Aventura**. Centraliza la lógica de negocio, reglas transaccionales, seguridad y persistencia de datos del hotel. Reemplaza la manipulación manual de registros asegurando transacciones ACID que erradican el overbooking accidental, gestionan el ciclo de vida de las habitaciones (disponible, ocupada, limpieza, mantenimiento), calculan tarifas automáticas de Check-In (Pernocte / Day Use), controlan el stock del punto de venta y auditan cada acción por roles.

---

## 3. Integrantes y Roles

| Integrante | Rol Scrum / Módulo Asignado en Backend | Rama Git Asignada |
|:---|:---|:---:|
| **Garay Carlos, Kenny Sebastian** | **Líder de Proyecto & Scrum Master** / **Acceso, Seguridad, Autenticación & Auditoría**<br>*(Modelos de usuario, roles RBAC, filtros JWT, interceptores de auditoría y endpoints `/api/auth/**` y `/api/auditoria/**`)* | `Kenny` |
| **Urbano Pilco, Héctor Ivan** *(U22244557)* | Desarrollador Backend / **Servicios del Hotel (Limpieza, Mantenimiento & Control de Averías)**<br>*(Entidades de tareas de limpieza, máquina de estados de habitación, registro y resolución de incidencias/averías y endpoints `/api/operaciones/**`)* | `Hector` |
| **Asto Condori, David Josue** | Desarrollador Backend & Core / **Recepción y Habitaciones (Rack en Tiempo Real)**<br>*(Entidades de habitación, reservas, validación de overbooking y endpoints `/api/habitaciones/**` y `/api/check-in/**`)* | `David` |
| **Barrera Chavez, Erik Eduardo** | Desarrollador Backend & Analítica / **Huéspedes y Panel de Control (Dashboard & KPIs)**<br>*(Entidad de huéspedes, historial y endpoints analíticos `/api/huespedes/**` y `/api/dashboard/kpis`)* | `Erik` |
| **Mariño Avila, Eliseo** | Desarrollador Backend & Transacciones / **Punto de Venta (POS), Inventario & Control de Caja**<br>*(Catálogo de productos, control de stock y endpoints transaccionales `/api/pos/**` y `/api/caja/**`)* | `Eliseo` |

---

## 4. Tecnologías Usadas
* **Lenguaje:** [Java 21 LTS](https://www.oracle.com/java/) — Programación orientada a objetos moderna con virtual threads y tipos inmutables.
* **Framework:** [Spring Boot 4.1.1](https://spring.io/projects/spring-boot) — Framework de referencia empresarial para microservicios y APIs REST.
* **Librerías y Módulos Spring:**
  * `spring-boot-starter-webmvc`: Exposición de controladores REST y serialización JSON.
  * `spring-boot-starter-data-jpa`: Capa de persistencia relacional y mapeo objeto-relacional (ORM) con Hibernate.
  * `spring-boot-starter-security`: Control de acceso, protección de endpoints y autorización por roles (RBAC).
  * `spring-boot-starter-validation`: Validación de contratos de entrada con Bean Validation.
  * `org.postgresql:postgresql`: Driver JDBC para conexión transaccional con PostgreSQL.
  * `org.projectlombok:lombok`: Reducción de código repetitivo (Getters, Setters, Builders, Constructores).
* **Gestor de Construcción:** [Gradle 9.x](https://gradle.org/) con Gradle Wrapper (`gradlew`)
* **Base de Datos Cloud:** PostgreSQL alojado en **Supabase** con cifrado SSL obligatorio

---

## 5. Estructura de Carpetas

```
HOTELAVENTURA-BACKEND/
├── gradle/wrapper/          # Binarios y configuración del wrapper de Gradle
├── src/
│   ├── main/
│   │   ├── java/com/hotelaventura/
│   │   │   ├── config/      # Configuración de Seguridad, CORS, Auditoría
│   │   │   ├── controller/  # Controladores REST (/api/habitaciones, /api/auth, /api/pos, etc.)
│   │   │   ├── dto/         # Request y Response Data Transfer Objects validados
│   │   │   ├── entity/      # Entidades JPA (@Entity, @Table, relaciones relacionales)
│   │   │   ├── exception/   # Manejo global de excepciones (@RestControllerAdvice)
│   │   │   ├── repository/  # Interfaces Spring Data JPA
│   │   │   ├── service/     # Interfaces de reglas de negocio
│   │   │   │   └── impl/    # Implementación de los servicios
│   │   │   └── util/        # Utilidades transversales
│   │   └── resources/
│   │       └── application.properties   # Configuración de puerto, perfiles y Supabase JDBC
│   └── test/                # Pruebas unitarias e integración con JUnit Platform
├── .gitattributes           # Configuración de atributos Git para compatibilidad de finales de línea
├── .gitignore               # Exclusiones de Git (build/, .gradle/, etc.)
├── build.gradle             # Definición de dependencias y plugins de Spring Boot
├── gradlew                  # Script ejecutable en entornos Linux / macOS / Bash
├── gradlew.bat              # Script ejecutable en Windows PowerShell / CMD
├── HELP.md                  # Referencias y guías técnicas de Spring Boot
├── README.md                # Documentación oficial del repositorio Backend
└── settings.gradle          # Configuración de nombre del proyecto Gradle
```

---

## 6. Cómo Ejecutar el Proyecto Localmente

### Requisitos Previos:
* **Java Development Kit (JDK):** Versión 21 LTS instalada y variable de entorno `JAVA_HOME` configurada ([Descargar JDK 21](https://adoptium.net/))
* **Git:** Cliente Git configurado en el sistema
* **Conexión a Internet:** Para resolver dependencias en Maven Central y conectar con la base de datos Supabase

### Pasos de Instalación y Ejecución:

#### 1. Clonar el repositorio oficial de Backend
```bash
git clone https://github.com/lFateGC/HOTELAVENTURA-BACKEND.git
cd HOTELAVENTURA-BACKEND
```

#### 2. Compilar y verificar dependencias
* **En Windows (PowerShell / CMD):**
  ```powershell
  .\gradlew.bat compileJava
  ```
* **En Linux / macOS / Git Bash:**
  ```bash
  ./gradlew compileJava
  ```

#### 3. Iniciar el servidor Spring Boot
* **En Windows (PowerShell / CMD):**
  ```powershell
  .\gradlew.bat bootRun
  ```
* **En Linux / macOS / Git Bash:**
  ```bash
  ./gradlew bootRun
  ```

#### 4. Acceder al servicio
El servidor Backend estará escuchando peticiones en:
```
http://localhost:8080/
```

---

## 7. Ramas y Flujo de Trabajo Git (Guía Paso a Paso para el Equipo)

### 7.1. Ramas Oficiales del Proyecto
* **Rama Principal (`main`):** Rama de producción oficial del Backend, estable y protegida. **Está prohibido hacer commits directos a `main`**. Todo cambio entra exclusivamente mediante Pull Request revisado y probado.
* **Ramas Asignadas por Desarrollador:**
  * `Kenny` ➔ **Kenny Garay** (Modelos de usuario, roles RBAC, filtros JWT, auditoría y endpoints `/api/auth/**`)
  * `Hector` ➔ **Héctor Urbano** (Entidades de limpieza, incidencias/averías y endpoints `/api/operaciones/**`)
  * `David` ➔ **David Asto** (Entidades de habitación, reservas, control de overbooking y endpoints `/api/habitaciones/**` y `/api/check-in/**`)
  * `Erik` ➔ **Erik Barrera** (Entidad de huéspedes, historial y endpoints analíticos `/api/huespedes/**` y `/api/dashboard/kpis`)
  * `Eliseo` ➔ **Eliseo Mariño** (Catálogo de productos, control de stock y endpoints transaccionales `/api/pos/**` y `/api/caja/**`)

---

### 7.2. Guía Práctica de Trabajo con Git y Visual Studio Code

#### Paso 1: Clonar el repositorio y abrir en VS Code
Tienes dos alternativas para clonar el proyecto en tu máquina:

* **Opción A: Desde Visual Studio Code (Recomendado):**
  1. Abre **Visual Studio Code**.
  2. Presiona `Ctrl + Shift + P` para abrir la paleta de comandos.
  3. Escribe `Git: Clone` y presiona **Enter**.
  4. Pega la URL oficial del Backend:
     ```
     https://github.com/lFateGC/HOTELAVENTURA-BACKEND.git
     ```
  5. Selecciona la carpeta donde quieres almacenar el proyecto (ejemplo: `C:\Proyectos`).
  6. Al terminar la descarga, haz clic en el aviso **"Open" / "Abrir repositorio"**.

* **Opción B: Desde la Terminal (PowerShell / Git Bash / CMD):**
  ```bash
  # 1. Ve a la carpeta donde guardas tus proyectos
  cd C:\Users\TuUsuario\Desktop\Proyectos

  # 2. Clona el repositorio Backend
  git clone https://github.com/lFateGC/HOTELAVENTURA-BACKEND.git

  # 3. Entra a la carpeta del proyecto
  cd HOTELAVENTURA-BACKEND

  # 4. Abre el proyecto en VS Code
  code .
  ```

---

#### Paso 2: Cambiarte a tu Rama de Trabajo Asignada

Cada integrante desarrolla sus controladores, servicios y entidades en su propia rama para evitar sobrescrituras accidentales:

* **Desde la Terminal:**
  ```bash
  # Ver en qué rama estás actualmente
  git branch

  # Si tu rama ya existe en el remoto (ejemplo para Héctor):
  git checkout Hector

  # Si vas a crear tu rama localmente a partir de lo último de main:
  git checkout -b Hector origin/main
  ```
  *(Reemplaza `Hector` por tu respectivo nombre: `David`, `Erik`, `Eliseo` o `Kenny`)*

* **Desde la Interfaz de VS Code:**
  1. En la **esquina inferior izquierda** de VS Code, haz clic sobre el nombre de la rama actual (por ejemplo, `main`).
  2. En el menú que aparece arriba, selecciona tu rama asignada (o elige *"Create new branch..."* e ingresa tu nombre).

---

#### Paso 3: ¿Cómo sincronizar tu rama con lo último de `main`? *(¡Paso Fundamental!)*
A medida que el equipo avanza, nuevas entidades y servicios se integrarán a `main`. Para tener siempre el código más reciente y evitar conflictos al programar:

```bash
# 1. Asegúrate de estar en tu rama personal
git checkout Hector

# 2. Trae y fusiona los últimos cambios de main hacia tu rama
git pull origin main
```

> **¿Qué hace este comando?** Descarga de GitHub todas las actualizaciones ya aprobadas en `main` y las une con tu código local, asegurando compatibilidad con los servicios de los demás compañeros.
> 
> *En VS Code:* Presiona `Ctrl + Shift + P` ➔ escribe `Git: Pull From...` ➔ elige `origin` ➔ selecciona `origin/main`.

---

#### Paso 4: Trabajar, confirmar cambios (Commits) y buenas prácticas

Durante el desarrollo de tus endpoints o lógica de negocio:

1. **Revisar el estado de los archivos modificados:**
   ```bash
   git status
   ```
2. **Agregar los archivos al área de preparación:**
   ```bash
   git add .
   ```
3. **Crear el commit con mensaje descriptivo:**
   ```bash
   git commit -m "feat(habitaciones): creación de entidad Room y repositorio JPA"
   ```
   *Prefijos recomendados para los commits:*
   * `feat:` Nueva funcionalidad o endpoint (ej. `feat(auth): login con token JWT`)
   * `fix:` Corrección de errores (ej. `fix(checkin): validar sobrealquiler de habitación`)
   * `refactor:` Optimización o reestructuración de clases sin cambiar comportamiento
   * `docs:` Cambios en documentación técnica o comentarios JavaDoc

* **Hacer el Commit en VS Code con interfaz gráfica:**
  1. Abre la pestaña **Control de código fuente** (*Source Control*) en la barra lateral izquierda (`Ctrl + Shift + G`).
  2. Escribe el mensaje de commit en el campo superior.
  3. Haz clic en el botón azul **Commit** (o presiona `Ctrl + Enter`).

---

#### Paso 5: Subir tus avances a GitHub (`git push`)

Antes de subir cambios, **siempre verifica que el Backend compile limpiamente**:

```bash
# 1. Validar la compilación Java y dependencias
# En Windows:
.\gradlew.bat compileJava

# En Linux / Mac / Git Bash:
./gradlew compileJava

# 2. Subir tu rama a GitHub
git push origin Hector
```
*(Reemplaza `Hector` por el nombre de tu rama)*

* **En VS Code:** Haz clic en **"Sync Changes" / "Sincronizar cambios"** o en los tres puntos `...` ➔ **Push**.

---

#### Paso 6: Crear un Pull Request (PR) en GitHub hacia `main`

Al completar una tarea o funcionalidad de tu módulo:

1. Ingresa a GitHub: [HOTELAVENTURA-BACKEND](https://github.com/lFateGC/HOTELAVENTURA-BACKEND).
2. Haz clic en el aviso amarillo **"Compare & pull request"** (o en la pestaña **Pull requests** ➔ **New pull request**).
3. Selecciona la dirección de integración:
   * **base:** `main` ⬅️ **compare:** `TuNombre` *(ej. `Hector`)*.
4. Escribe un título explicativo (ejemplo: `feat: API REST de registro y consulta de averías`).
5. Describe brevemente los endpoints o entidades creadas.
6. En la barra lateral derecha, en **Reviewers**, selecciona al Líder del Proyecto (**Kenny Garay** / `lFateGC`).
7. Haz clic en **"Create pull request"**.
8. El Líder validará las pruebas, revisará la arquitectura y aprobará el *merge* hacia `main`.

---

#### Paso 7: ¿Qué hacer si hay un conflicto al hacer pull?
Si al ejecutar `git pull origin main` aparece un aviso de **CONFLICT**:
1. Abre los archivos en conflicto en **VS Code**.
2. Encima del código resaltado verás los botones de resolución:
   * **Accept Current Change:** Conserva tu cambio local.
   * **Accept Incoming Change:** Acepta el cambio que vino de `main`.
   * **Accept Both Changes:** Mantiene ambos fragmentos.
3. Guarda el archivo (`Ctrl + S`) y en la terminal ejecuta:
   ```bash
   git add .
   git commit -m "merge: resolver conflictos con main"
   git push origin TuNombre
   ```

---

### 7.3. Reglas de Oro para el Equipo
1. 🚫 **NUNCA hagas commits directos sobre la rama `main`**. Cada integrante trabaja exclusivamente en su rama personal.
2. 🔄 **SIEMPRE haz `git pull origin main`** antes de empezar a programar para trabajar sobre la última versión estable.
3. 🧪 **SIEMPRE ejecuta `.\gradlew.bat compileJava`** antes de hacer push para asegurar que no se suba código roto.
4. 💬 **Haz commits frecuentes y claros**, documentando cada clase o funcionalidad añadida.

---

## 8. Estado del Proyecto / Avance Actual (Avance 1)

### Implementado en este Avance 1:
* **Entorno y Herramientas:** Configuración completa de Spring Boot 4.1.1 con Java 21 LTS y Gradle 9.x.
* **Separación de Repositorios:** Repositorio Backend desacoplado e independiente del Frontend.
* **Configuración de Dependencias:** Integración resuelta de Spring Web, Spring Security, Spring Data JPA, PostgreSQL Driver y Lombok.
* **Verificación de Compilación:** Compilación `compileJava` verificada al 100% libre de errores.
* **Gestión de Proyecto en GitHub:** Matriz de integrantes, roles, ramas individuales y seguimiento de backlog con GitHub Issues.

### Pendiente para Siguientes Avances:
* **Sprint 2 (Backend Core 100% — Enfoque Prioritario):** Conexión con base de datos en la nube **Supabase (PostgreSQL)**, creación de entidades JPA, repositorios, servicios y APIs REST para pruebas de funcionamiento de los servicios/microservicios del proyecto.
* **Sprint 4 (Despliegue y QA):** Levantamiento y despliegue del servicio Backend a la red mediante **Render** (o plataforma afín) conectado a Supabase, pruebas de concurrencia y sustentación final.

---

## 9. Enlace a los Repositorios del Proyecto

* **Repositorio Backend (Este Repositorio):** [https://github.com/lFateGC/HOTELAVENTURA-BACKEND](https://github.com/lFateGC/HOTELAVENTURA-BACKEND)
* **Repositorio Frontend (React 19 + TypeScript + Vite):** [https://github.com/lFateGC/HOTELAVENTURA-FRONTEND](https://github.com/lFateGC/HOTELAVENTURA-FRONTEND)
* **Gestión de Backlog & Issues:** [GitHub Issues del Proyecto](https://github.com/lFateGC/HOTELAVENTURA-BACKEND/issues)