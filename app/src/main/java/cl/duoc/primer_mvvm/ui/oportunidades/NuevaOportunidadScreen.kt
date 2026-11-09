// #creacion_oportunidad #formulario_oportunidad #evento #organizacion
package cl.duoc.primer_mvvm.ui.oportunidades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.primer_mvvm.model.Prioridad

/**
 * #creacion_oportunidad #formulario_oportunidad
 * Formulario para el registro completo de una nueva oportunidad, evento y organización asociada.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaOportunidadScreen(
    onVolver: () -> Unit,
    onCrearOportunidad: (
        String, String, String, String, String, Prioridad, String,
        String, String, String, String, String, String, String,
        String, String, Int, String, String, String,
        String, String, String, String,
        String, String, String, String, String, String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    // Campos Oportunidad
    var nombreOportunidad by remember { mutableStateOf("") }
    var tipoOportunidad by remember { mutableStateOf("Feria empresarial") }
    var descripcion by remember { mutableStateOf("") }
    var fuenteInformacion by remember { mutableStateOf("Redes sociales / Web") }
    var responsableInterno by remember { mutableStateOf("Ejecutivo Comercial") }
    var prioridad by remember { mutableStateOf(Prioridad.ALTA) }
    var potencialComercial by remember { mutableStateOf("Alto") }

    // Campos Evento
    var nombreEvento by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf("15/04/2026") }
    var fechaTermino by remember { mutableStateOf("16/04/2026") }
    var horario by remember { mutableStateOf("09:00 - 18:00 hrs") }
    var direccion by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("Santiago") }
    var region by remember { mutableStateOf("Región Metropolitana") }
    var publicoEsperado by remember { mutableStateOf("Colaboradores y público general") }
    var publicoObjetivo by remember { mutableStateOf("Trabajadores interesados en salud dental") }
    var asistentesEstimados by remember { mutableStateOf("1000") }
    var condicionesParticipacion by remember { mutableStateOf("Stand de 3x2m con electricidad") }
    var requisitos by remember { mutableStateOf("Pendón e información corporativa") }
    var costosReferenciales by remember { mutableStateOf("$0 CLP (Gratuito)") }

    // Campos Organización
    var nombreOrganizacion by remember { mutableStateOf("") }
    var tipoOrganizacion by remember { mutableStateOf("Empresa Privada") }
    var rubroOrganizacion by remember { mutableStateOf("Comercial / Servicios") }
    var sitioWebOrganizacion by remember { mutableStateOf("www.organizacion-ficticia.cl") }

    // Campos Contacto
    var nombreContacto by remember { mutableStateOf("") }
    var cargoContacto by remember { mutableStateOf("Encargado de Bienestar") }
    var areaContacto by remember { mutableStateOf("Recursos Humanos") }
    var correoContacto by remember { mutableStateOf("contacto@ficticio.cl") }
    var telefonoContacto by remember { mutableStateOf("+56 9 9999 8888") }
    var observaciones by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Oportunidad") },
                // #boton #volver - Botón para regresar a la pantalla anterior
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección 1: Oportunidad
            Text(
                text = "1. Información de la Oportunidad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // #campo_texto #nombre_oportunidad - Campo obligatorio para nombre de la oportunidad
            OutlinedTextField(
                value = nombreOportunidad,
                onValueChange = {
                    nombreOportunidad = it
                    errorNombre = false
                },
                label = { Text("Nombre de la Oportunidad *") },
                isError = errorNombre,
                supportingText = { if (errorNombre) Text("El nombre es obligatorio") },
                modifier = Modifier.fillMaxWidth()
            )

            // #campo_texto #tipo_oportunidad - Tipo de oportunidad comercial
            OutlinedTextField(
                value = tipoOportunidad,
                onValueChange = { tipoOportunidad = it },
                label = { Text("Tipo de Oportunidad") },
                placeholder = { Text("Ej: Feria empresarial, Actividad municipal") },
                modifier = Modifier.fillMaxWidth()
            )

            // #campo_texto #descripcion - Descripción general de la oportunidad
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción general") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Prioridad:", fontWeight = FontWeight.SemiBold)
                    Prioridad.entries.forEach { p ->
                        // #radio_button #prioridad - Opción de selección de prioridad
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = prioridad == p,
                                onClick = { prioridad = p }
                            )
                            Text(p.name)
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    // #campo_texto #potencial_comercial - Potencial comercial estimado
                    OutlinedTextField(
                        value = potencialComercial,
                        onValueChange = { potencialComercial = it },
                        label = { Text("Potencial Comercial") }
                    )
                }
            }

            HorizontalDivider()

            // Sección 2: Evento
            Text(
                text = "2. Información del Evento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // #campo_texto #nombre_evento - Nombre del evento
            OutlinedTextField(
                value = nombreEvento,
                onValueChange = { nombreEvento = it },
                label = { Text("Nombre del Evento") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // #campo_texto #fecha_inicio - Fecha de inicio del evento
                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    label = { Text("Fecha Inicio") },
                    modifier = Modifier.weight(1f)
                )
                // #campo_texto #fecha_termino - Fecha de término del evento
                OutlinedTextField(
                    value = fechaTermino,
                    onValueChange = { fechaTermino = it },
                    label = { Text("Fecha Término") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // #campo_texto #comuna - Comuna del evento
                OutlinedTextField(
                    value = comuna,
                    onValueChange = { comuna = it },
                    label = { Text("Comuna") },
                    modifier = Modifier.weight(1f)
                )
                // #campo_texto #region - Región del evento
                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Región") },
                    modifier = Modifier.weight(1f)
                )
            }

            // #campo_texto #direccion - Dirección física
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección / Ubicación General") },
                modifier = Modifier.fillMaxWidth()
            )

            // #campo_texto #asistentes_estimados - Cantidad estimada de asistentes
            OutlinedTextField(
                value = asistentesEstimados,
                onValueChange = { asistentesEstimados = it },
                label = { Text("Asistentes Estimados") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // Sección 3: Organización
            Text(
                text = "3. Organización Responsable",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // #campo_texto #nombre_organizacion - Nombre de la organización o empresa
            OutlinedTextField(
                value = nombreOrganizacion,
                onValueChange = { nombreOrganizacion = it },
                label = { Text("Nombre Organización (Empresa/Municipalidad)") },
                modifier = Modifier.fillMaxWidth()
            )

            // #campo_texto #tipo_organizacion - Tipo de organización
            OutlinedTextField(
                value = tipoOrganizacion,
                onValueChange = { tipoOrganizacion = it },
                label = { Text("Tipo Organización") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // Sección 4: Contacto Corporativo Ficticio
            Text(
                text = "4. Contacto Corporativo Ficticio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // #campo_texto #nombre_contacto - Nombre del contacto
            OutlinedTextField(
                value = nombreContacto,
                onValueChange = { nombreContacto = it },
                label = { Text("Nombre Contacto") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // #campo_texto #cargo_contacto - Cargo del contacto
                OutlinedTextField(
                    value = cargoContacto,
                    onValueChange = { cargoContacto = it },
                    label = { Text("Cargo") },
                    modifier = Modifier.weight(1f)
                )
                // #campo_texto #area_contacto - Área del contacto
                OutlinedTextField(
                    value = areaContacto,
                    onValueChange = { areaContacto = it },
                    label = { Text("Área") },
                    modifier = Modifier.weight(1f)
                )
            }

            // #campo_texto #correo_contacto - Correo del contacto
            OutlinedTextField(
                value = correoContacto,
                onValueChange = { correoContacto = it },
                label = { Text("Correo Ficticio") },
                modifier = Modifier.fillMaxWidth()
            )

            // #campo_texto #observaciones - Observaciones adicionales
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones adicionales") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // #boton #guardar_oportunidad - Botón final para guardar la oportunidad comercial
            Button(
                onClick = {
                    if (nombreOportunidad.isBlank()) {
                        errorNombre = true
                        return@Button
                    }
                    onCrearOportunidad(
                        nombreOportunidad, tipoOportunidad, descripcion, fuenteInformacion, responsableInterno, prioridad, potencialComercial,
                        nombreEvento.ifBlank { nombreOportunidad }, fechaInicio, fechaTermino, horario, direccion, comuna, region,
                        publicoEsperado, publicoObjetivo, asistentesEstimados.toIntOrNull() ?: 500, condicionesParticipacion, requisitos, costosReferenciales,
                        nombreOrganizacion.ifBlank { "Organización no especificada" }, tipoOrganizacion, rubroOrganizacion, sitioWebOrganizacion,
                        nombreContacto, cargoContacto, areaContacto, correoContacto, telefonoContacto, observaciones
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Oportunidad")
            }
        }
    }
}
