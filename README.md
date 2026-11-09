# Trabajo Semestral - Gestión de Oportunidades Comerciales y Eventos 🚀

Aplicación móvil Android desarrollada en **Kotlin** con **Jetpack Compose** y arquitectura **MVVM (Model-View-ViewModel)** para la gestión comercial de eventos corporativos, seguimiento de clientes, control de alertas internas y foto de perfil con cámara y galería.

---

## 📁 Estructura del Proyecto (`cl.duoc.primer_mvvm`)

La aplicación sigue una arquitectura limpia por capas y paquetes semánticos:

```
cl.duoc.primer_mvvm/
├── MainActivity.kt                      # #main_activity #navegacion #dashboard #animacion
├── model/
│   └── Modelos.kt                       # #modelos #entidades #enums
├── data/
│   ├── OportunidadRepository.kt         # #repositorio #datos_oportunidades #alertas
│   └── UsuarioRepository.kt             # #repositorio #datos_usuarios #autenticacion
└── ui/
    ├── auth/                            # Módulo de Autenticación y Formulario
    │   ├── LoginScreen.kt               # #login #autenticacion #cambio_clave #animacion
    │   ├── LoginViewModel.kt            # #login #viewmodel
    │   ├── LoginUiState.kt              # #login #uistate
    │   ├── RegistroScreen.kt            # #formulario #registro #datepicker #animacion
    │   ├── RegistroViewModel.kt         # #formulario #registro #viewmodel
    │   ├── RegistroUiState.kt           # #formulario #uistate
    │   ├── Validaciones.kt              # #validaciones #formulario
    │   └── GestionUsuariosScreen.kt     # #gestion_usuarios #admin #roles
    ├── profile/                         # Módulo de Perfil y Fotografía
    │   └── InicioScreen.kt              # #perfil #camara #galeria #aviso_camara #animacion
    ├── oportunidades/                  # Módulo de Gestión Comercial y Alertas
    │   ├── OportunidadesListScreen.kt   # #oportunidades #kpi_metricas #filtros #animacion
    │   ├── OportunidadDetalleScreen.kt  # #detalle_oportunidad #seguimiento #activacion
    │   ├── NuevaOportunidadScreen.kt    # #creacion_oportunidad #evento #organizacion
    │   ├── AlertasScreen.kt             # #alertas #aviso_alertas #notificaciones #animacion
    │   ├── OportunidadesViewModel.kt    # #oportunidades #viewmodel #alertas
    │   └── OportunidadesUiState.kt      # #oportunidades #uistate
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## ✨ Características Principales

### 📸 1. Perfil y Captura de Foto (`#camara`, `#galeria`, `#aviso_camara`)
- **Captura con Cámara**: Integración con `TakePicturePreview()` de Android Activity Contracts para tomar foto de perfil directamente con la cámara del dispositivo.
- **Aviso de Permiso de Cámara**: Verificación de permiso `Manifest.permission.CAMERA` en runtime con aviso mediante `Toast` si es denegado.
- **Galería de Fotos**: Selección de imágenes mediante `PickVisualMedia()` (Photo Picker).
- **Animación**: Transición animada al actualizar la foto de perfil.

### 🔐 2. Autenticación y Administración (`#login`, `#registro`, `#gestion_usuarios`, `#roles`)
- **Inicio de Sesión y Registro**: Formulario con validación de correo (`@...cl`), contraseña (mínimo 8 caracteres) y fecha de nacimiento con `DatePicker` (mayoría de edad +18).
- **Recuperación/Cambio de Contraseña**: Diálogo modal para restablecer claves.
- **Panel de Administración**: Gestión exclusiva para usuarios con rol **Admin** o **Supervisor** para modificar roles, crear cuentas y cambiar contraseñas.

### 💼 3. Oportunidades Comerciales (`#oportunidades`, `#kpi_metricas`, `#filtros`)
- **Listado y Filtros**: Búsqueda por texto y filtrado por prioridad o estado de la oportunidad.
- **Métricas KPI**: Tarjetas dinámicas con conteo de oportunidades Totales, Pendientes y Confirmadas.
- **Registro y Seguimiento**: Formulario completo para registrar eventos, empresas organizadoras, contactos corporativos y bitácora de gestiones.

### 🔔 4. Alertas y Notificaciones (`#alertas`, `#aviso_alertas`)
- **Supervisión de Alertas**: Avisos automáticos de eventos próximos y propuestas sin respuesta.
- **Badge Notificador**: Contador animado en la barra de navegación inferior con el total de alertas no leídas.

### 🎬 5. Transiciones y Animaciones Compose (`#animacion`)
- `AnimatedContent` entre pestañas de Login y Registro.
- `AnimatedContent` entre menú inferior y pantalla de detalle.
- `AnimatedVisibility` para mensajes de error y validación.
- `animateContentSize()` en tarjetas KPI y elementos de lista.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Arquitectura**: MVVM con StateFlow & ViewModel
- **Inyección/Componentes**: Activity Result Contracts (Cámara & Galería)
- **Etiquetas Identificadoras**: Comentarios descriptivos (`#camara`, `#boton`, `#animacion`, `#alertas`, `#campo_texto`, etc.)

---

## 🚀 Cómo Ejecutar el Proyecto

1. Abra el proyecto en **Android Studio (Ladybug / Jellyfish o superior)**.
2. Sincronice el proyecto con Gradle (`Gradle Sync`).
3. Compile el proyecto corriendo:
   ```bash
   ./gradlew assembleDebug
   ```
4. Ejecute en un emulador o dispositivo físico Android con cámara.
