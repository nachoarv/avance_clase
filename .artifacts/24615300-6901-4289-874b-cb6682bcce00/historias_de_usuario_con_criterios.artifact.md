# 15 Historias de Usuario con Criterios de Aceptación - DSY1105

Este documento presenta las **15 historias de usuario** oficiales para la aplicación **Gestión de Oportunidades Comerciales y Eventos**, incluyendo sus respectivos **Criterios de Aceptación** verificables para la evaluación funcional y pruebas.

---

## 🔐 Módulo de Autenticación y Cuentas

### 1. Registro de Nuevo Usuario
- **Historia de Usuario:** Como usuario nuevo (ejecutivo comercial), quiero registrarme en la aplicación completando un formulario con validación de correo corporativo y mayoría de edad (+18 con DatePicker), para crear mi cuenta y acceder al sistema de gestión comercial.
- **Criterios de Aceptación:**
  1. El formulario valida que el correo termine en `@empresa.cl` o formato válido de correo.
  2. La fecha de nacimiento seleccionada mediante el DatePicker valida que el usuario sea mayor de 18 años.
  3. Las contraseñas deben coincidir y tener al menos 8 caracteres.
  4. Al registrarse exitosamente, los datos se almacenan en el repositorio y se redirige al login.

### 2. Inicio de Sesión Exitoso
- **Historia de Usuario:** Como ejecutivo comercial registrado, quiero iniciar sesión ingresando mi correo y contraseña válidos, para acceder al panel principal y gestionar las oportunidades comerciales asignadas.
- **Criterios de Aceptación:**
  1. El sistema valida que el correo exista en el repositorio de usuarios.
  2. La contraseña ingresada debe coincidir exactamente con la registrada.
  3. Al autenticarse correctamente, se navega hacia la pantalla principal (Dashboard) mostrando el nombre y rol del usuario.

### 3. Validación de Cuenta Desactivada
- **Historia de Usuario:** Como usuario con cuenta suspendida, quiero que el sistema bloquee mi acceso y muestre un mensaje de error si intento iniciar sesión, para impedir el uso no autorizado del sistema cuando el Administrador haya desactivado la cuenta.
- **Criterios de Aceptación:**
  1. Si un usuario tiene el flag `activo = false`, el sistema rechaza el inicio de sesión.
  2. Se muestra un mensaje claro indicando que la cuenta se encuentra desactivada por el administrador.

### 4. Solicitud de Cambio de Contraseña
- **Historia de Usuario:** Como usuario que olvidó su contraseña o desea actualizarla, quiero acceder a un diálogo de cambio de clave validando mi correo y nueva contraseña, para restablecer mis credenciales de acceso de forma segura.
- **Criterios de Aceptación:**
  1. El usuario puede abrir un diálogo modal para recuperar/cambiar contraseña.
  2. Si la cuenta es privilegiada (Admin/Supervisor), el sistema solicita la palabra clave maestra de seguridad (`EMPRESA2026`).
  3. La nueva contraseña debe cumplir con el largo mínimo (8 caracteres).

---

## 📸 Módulo de Perfil y Fotografía

### 5. Captura de Foto de Perfil con Cámara
- **Historia de Usuario:** Como usuario autenticado, quiero capturar una fotografía directamente con la cámara del dispositivo móvil (`TakePicturePreview`), para actualizar mi foto de perfil en tiempo real.
- **Criterios de Aceptación:**
  1. Se solicita el permiso de cámara en tiempo runtime (`Manifest.permission.CAMERA`).
  2. Al presionar el botón de cámara, se abre el intent de captura fotográfica.
  3. La imagen capturada se muestra inmediatamente como la foto de perfil del usuario en la interfaz.

### 6. Selección de Foto de Perfil desde Galería
- **Historia de Usuario:** Como usuario autenticado, quiero seleccionar una imagen desde la galería de fotos del dispositivo (`PickVisualMedia`), para establecer mi fotografía de perfil con una imagen existente.
- **Criterios de Aceptación:**
  1. Al presionar el botón de galería, se abre el selector visual nativo de Android.
  2. Al seleccionar una imagen válida, esta reemplaza la foto de perfil actual con una transición animada.

### 7. Visualización de Información de Perfil y Cargo
- **Historia de Usuario:** Como ejecutivo comercial, quiero visualizar en mi pantalla de inicio mis datos personales, cargo, departamento y fecha de registro, para corroborar la información institucional de mi cuenta.
- **Criterios de Aceptación:**
  1. La pantalla de inicio muestra el nombre completo, rol, correo, teléfono y departamento obtenidos del repositorio de usuarios.

---

## 💼 Módulo de Gestión de Oportunidades Comerciales

### 8. Registro de Nueva Oportunidad y Evento
- **Historia de Usuario:** Como ejecutivo comercial, quiero registrar una nueva oportunidad comercial indicando la organización, evento, contactos y descripción, para incorporar prospectos y eventos corporativos al flujo de ventas.
- **Criterios de Aceptación:**
  1. El formulario permite ingresar nombre de oportunidad, descripción, tipo de evento, fecha, comuna y organización.
  2. Al guardar, la nueva oportunidad aparece reflejada en el listado general en tiempo real.

### 9. Visualización de Métricas KPI
- **Historia de Usuario:** Como ejecutivo comercial o supervisor, quiero visualizar tarjetas KPI dinámicas con el conteo de oportunidades Totales, Pendientes y Confirmadas, para tener un resumen ejecutivo del estado comercial en tiempo real.
- **Criterios de Aceptación:**
  1. Se muestran tarjetas superiores con contadores numéricos actualizados automáticamente según el estado de las oportunidades.

### 10. Filtrado y Búsqueda de Oportunidades
- **Historia de Usuario:** Como usuario del sistema, quiero buscar oportunidades por texto y filtrarlas por estado, prioridad, tipo o comuna, para localizar rápidamente eventos y prospectos específicos.
- **Criterios de Aceptación:**
  1. La barra de búsqueda filtra en tiempo real por título, descripción u organización.
  2. Los chips de filtro permiten acotar por estado (Pendiente, Confirmada, etc.) y prioridad (Alta, Media, Baja).

### 11. Consulta de Detalle de Oportunidad
- **Historia de Usuario:** Como ejecutivo comercial, quiero seleccionar una oportunidad del listado para acceder a su vista de detalle con toda la información de contacto y organización, para revisar los antecedentes antes de realizar una gestión.
- **Criterios de Aceptación:**
  1. Al hacer clic en una oportunidad se abre la pantalla de detalle mostrando organización, contactos, fecha del evento y bitácora.

### 12. Actualización de Seguimientos y Estado
- **Historia de Usuario:** Como ejecutivo comercial, quiero registrar anotaciones en la bitácora de seguimientos y cambiar el estado de una oportunidad (ej. de En Negociación a Confirmada), para mantener actualizado el avance del ciclo comercial.
- **Criterios de Aceptación:**
  1. El usuario puede añadir un nuevo seguimiento con fecha y observaciones.
  2. El estado de la oportunidad se actualiza de forma inmediata en el flujo de datos.

---

## 🔔 Módulo de Alertas y Notificaciones

### 13. Consulta de Alertas Internas
- **Historia de Usuario:** Como usuario del sistema, quiero consultar un listado de alertas automáticas para eventos próximos o propuestas sin respuesta, para tomar acciones preventivas y oportunas ante compromisos comerciales pendientes.
- **Criterios de Aceptación:**
  1. La pantalla de alertas lista todas las notificaciones pendientes con indicador visual de estado (leída/no leída).

### 14. Badge Notificador en Barra de Navegación
- **Historia de Usuario:** Como usuario de la aplicación, quiero ver un contador animado con el total de alertas no leídas en la barra de navegación inferior, para identificar visualmente si tengo notificaciones pendientes de atención.
- **Criterios de Aceptación:**
  1. El icono de alertas en la barra inferior muestra un badge rojo con la cantidad exacta de alertas sin leer.

---

## 🛡️ Módulo de Administración de Usuarios (Roles)

### 15. Gestión de Roles y Cuentas por el Administrador
- **Historia de Usuario:** Como administrador o supervisor del sistema, quiero acceder a un panel exclusivo para modificar roles, activar/desactivar cuentas y actualizar datos de otros usuarios, para controlar los permisos y accesos del personal en la plataforma.
- **Criterios de Aceptación:**
  1. El panel de gestión de usuarios es accesible únicamente por roles Admin o Supervisor.
  2. Permite cambiar el rol de un usuario, alternar su estado activo/inactivo y eliminar registros.
