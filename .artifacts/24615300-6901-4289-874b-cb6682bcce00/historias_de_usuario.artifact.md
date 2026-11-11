# 15 Historias de Usuario - Gestión de Oportunidades Comerciales y Eventos (DSY1105)

Este documento recopila las **15 historias de usuario** requeridas para la auditoría funcional y evaluación del proyecto, estructuradas bajo el formato estándar:
`Como [tipo de usuario], quiero [acción o funcionalidad], para [objetivo o beneficio].`

---

## 🔐 Módulo de Autenticación y Cuentas

### 1. Registro de Nuevo Usuario
- **Como** usuario nuevo (ejecutivo comercial),
- **quiero** registrarme en la aplicación completando un formulario con validación de correo corporativo y mayoría de edad (+18 con DatePicker),
- **para** crear mi cuenta y acceder al sistema de gestión comercial.

### 2. Inicio de Sesión Exitoso
- **Como** ejecutivo comercial registrado,
- **quiero** iniciar sesión ingresando mi correo y contraseña válidos,
- **para** acceder al panel principal y gestionar las oportunidades comerciales asignadas.

### 3. Validación de Cuenta Desactivada
- **Como** usuario con cuenta suspendida,
- **quiero** que el sistema bloquee mi acceso y muestre un mensaje de error si intento iniciar sesión,
- **para** impedir el uso no autorizado del sistema cuando el Administrador haya desactivado la cuenta.

### 4. Solicitud de Cambio de Contraseña
- **Como** usuario que olvidó su contraseña o desea actualizarla,
- **quiero** acceder a un diálogo de cambio de clave validando mi correo y nueva contraseña,
- **para** restablecer mis credenciales de acceso de forma segura.

---

## 📸 Módulo de Perfil y Fotografía

### 5. Captura de Foto de Perfil con Cámara
- **Como** usuario autenticado,
- **quiero** capturar una fotografía directamente con la cámara del dispositivo móvil (`TakePicturePreview`),
- **para** actualizar mi foto de perfil en tiempo real.

### 6. Selección de Foto de Perfil desde Galería
- **Como** usuario autenticado,
- **quiero** seleccionar una imagen desde la galería de fotos del dispositivo (`PickVisualMedia`),
- **para** establecer mi fotografía de perfil con una imagen existente.

### 7. Visualización de Información de Perfil y Cargo
- **Como** ejecutivo comercial,
- **quiero** visualizar en mi pantalla de inicio mis datos personales, cargo, departamento y fecha de registro,
- **para** corroborar la información institucional de mi cuenta.

---

## 💼 Módulo de Gestión Oportunidades Comerciales

### 8. Registro de Nueva Oportunidad y Evento
- **Como** ejecutivo comercial,
- **quiero** registrar una nueva oportunidad comercial indicando la organización, evento, contactos y descripción,
- **para** incorporar prospectos y eventos corporativos al flujo de ventas.

### 9. Visualización de Métricas KPI
- **Como** ejecutivo comercial o supervisor,
- **quiero** visualizar tarjetas KPI dinámicas con el conteo de oportunidades Totales, Pendientes y Confirmadas,
- **para** tener un resumen ejecutivo del estado comercial en tiempo real.

### 10. Filtrado y Búsqueda de Oportunidades
- **Como** usuario del sistema,
- **quiero** buscar oportunidades por texto y filtrarlas por estado, prioridad, tipo o comuna,
- **para** localizar rápidamente eventos y prospectos específicos.

### 11. Consulta de Detalle de Oportunidad
- **Como** ejecutivo comercial,
- **quiero** seleccionar una oportunidad del listado para acceder a su vista de detalle con toda la información de contacto y organización,
- **para** revisar los antecedentes antes de realizar una gestión.

### 12. Actualización de Seguimientos y Estado
- **Como** ejecutivo comercial,
- **quiero** registrar anotaciones en la bitácora de seguimientos y cambiar el estado de una oportunidad (ej. de En Negociación a Confirmada),
- **para** mantener actualizado el avance del ciclo comercial.

---

## 🔔 Módulo de Alertas y Notificaciones

### 13. Supervisión de Alertas Internas
- **Como** usuario del sistema,
- **quiero** consultar un listado de alertas automáticas para eventos próximos o propuestas sin respuesta,
- **para** tomar acciones preventivas y oportunas ante compromisos comerciales pendientes.

### 14. Badge Notificador en Barra de Navegación
- **Como** usuario de la aplicación,
- **quiero** ver un contador animado con el total de alertas no leídas en la barra de navegación inferior,
- **para** identificar visualmente si tengo notificaciones pendientes de atención.

---

## 🛡️ Módulo de Administración de Usuarios (Roles)

### 15. Gestión de Roles y Cuentas por el Administrador
- **Como** administrador o supervisor del sistema,
- **quiero** acceder a un panel exclusivo para modificar roles, activar/desactivar cuentas y actualizar datos de otros usuarios,
- **para** controlar los permisos y accesos del personal en la plataforma.
