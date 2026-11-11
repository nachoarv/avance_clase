# Evaluación Parcial 4 - Encargo (DSY1105)
## Nombre del Proyecto: Gestión de Oportunidades Comerciales y Eventos (App Móvil MVVM & Spring Boot Microservicios) 🚀

---

## 👥 Integrantes del Equipo
- **Ignacio Recabarren**
- **Benjamin Zúñiga**
- **Asignatura**: DSY1105 - Desarrollo de Aplicaciones Móviles
- **Sección / Profesor**: Duoc UC

---

## 📋 Descripción y Funcionalidades
Aplicación móvil Android desarrollada en **Kotlin** con **Jetpack Compose** y arquitectura **MVVM (Model-View-ViewModel)**. Permite la gestión comercial de eventos corporativos, seguimiento de clientes, control de alertas internas, panel de administración de usuarios y captura de foto de perfil con cámara y galería.

### 🌟 Módulos Principales:
1. **Autenticación y Registro (`#login`, `#registro`, `#gestion_usuarios`, `#roles`)**:
   - Inicio de sesión con validación de credenciales y estado activo de la cuenta.
   - Registro de usuarios con validación de mayoría de edad (`DatePicker`) y formato de correo corporativo (`@...cl`).
   - Panel de administración exclusivo para roles **Admin** y **Supervisor** (gestión de cuentas, roles, desactivación y cambios de clave seguros con palabra clave).
2. **Perfil y Fotografía (`#camara`, `#galeria`, `#aviso_camara`)**:
   - Captura de foto de perfil con la cámara del dispositivo (`TakePicturePreview`).
   - Selección de imágenes mediante galería (`PickVisualMedia`).
3. **Gestión de Oportunidades Comerciales (`#oportunidades`, `#kpi_metricas`, `#filtros`)**:
   - Listado con métricas KPI dinámicas (Totales, Pendientes, Confirmadas).
   - Filtros avanzados por estado, prioridad, tipo y comuna.
   - Detalle de oportunidad, bitácora de seguimientos y activación de eventos.
4. **Alertas y Notificaciones (`#alertas`, `#aviso_alertas`)**:
   - Sistema de alertas automáticas para eventos próximos y propuestas sin respuesta.

---

## 🔗 Endpoints Utilizados (Microservicios & API Externa)

### 🧩 1. Microservicios Propios (Spring Boot + Base de Datos H2/PostgreSQL)
- `POST /api/auth/login` - Autenticación de credenciales y validación de estado activo.
- `GET /api/oportunidades` - Listado y sincronización en tiempo real de oportunidades comerciales.
- `POST /api/oportunidades` - Creación y registro de nuevas oportunidades y eventos.
- `PUT /api/oportunidades/{id}` - Actualización de estado y seguimientos (CRUD completo).
- `GET /api/usuarios` - Administración y gestión de roles y cuentas.

### 🌍 2. API Externa (Consumo vía Retrofit)
- `GET https://apis.digital.gob.cl/dpa/comunas` o `https://jsonplaceholder.typicode.com/` - Sincronización de comunas, regiones y datos de apoyo externos integrados al flujo de creación de oportunidades sin afectar la persistencia local.

---

## 🧪 Pruebas Unitarias (`JUnit` & `ViewModel`/`Repository`)
Se han implementado pruebas unitarias exhaustivas que cubren más del 80% de la lógica de negocio en los siguientes módulos:
- `UsuarioRepositoryTest.kt`: Validación de búsqueda de usuarios, verificación de roles Admin/Supervisor, validación de clave maestra y cambio de contraseña.
- `LoginViewModelTest.kt`: Pruebas de inicio de sesión exitoso, contraseña errónea y bloqueo de cuentas desactivadas.
- `RegistroViewModelTest.kt`: Validación de formularios de registro, campos obligatorios, contraseñas coincidentes y restricciones de edad.
- `OportunidadRepositoryTest.kt`: Verificación de carga inicial de datos ficticios y catálogos comerciales.

---

## 📦 Generación de APK Firmado (`.jks`)
Para empaquetar la aplicación para su distribución y publicación:
1. Generar la llave de firmada (`Keystore`):
   ```bash
   keytool -genkey -v -keystore release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-release-key
   ```
2. Configurar en `app/build.gradle.kts`:
   ```kotlin
   signingConfigs {
       create("release") {
           storeFile = file("release-key.jks")
           storePassword = "password_jks"
           keyAlias = "my-release-key"
           keyPassword = "password_jks"
       }
   }
   buildTypes {
       release {
           signingConfig = signingConfigs.getByName("release")
           minifyEnabled = true
       }
   }
   ```
3. Compilar el APK firmado:
   ```bash
   ./gradlew assembleRelease
   ```
   El archivo ejecutable firmado se genera en `app/build/outputs/apk/release/app-release.apk`.

---

## 👥 Planificación y Control de Versiones (Trello & GitHub)
- **GitHub**: Repositorio con historial de commits detallados por cada integrante, ramas temáticas y pull requests colaborativos.
- **Trello**: Tablero Kanban estructurado con tareas distribuidas por sprints, asignadas a los integrantes del equipo y evidencias de progreso técnico.

---

## 🚀 Pasos para Ejecutar
1. Abra el proyecto en **Android Studio**.
2. Sincronice las dependencias con Gradle (`Gradle Sync`).
3. Ejecute los microservicios Spring Boot en su entorno local (puerto `8080`).
4. Conecte un dispositivo físico o emulador Android y ejecute:
   ```bash
   ./gradlew assembleDebug
   ```
