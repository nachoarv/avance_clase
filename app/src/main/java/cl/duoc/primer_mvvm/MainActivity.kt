// #main_activity #navegacion #scaffold #tabs #dashboard #aviso_alertas #animacion #badge_pulse
package cl.duoc.primer_mvvm

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.primer_mvvm.ui.auth.LoginScreen
import cl.duoc.primer_mvvm.ui.auth.RegistroScreen
import cl.duoc.primer_mvvm.ui.profile.InicioScreen
import cl.duoc.primer_mvvm.ui.oportunidades.*
import cl.duoc.primer_mvvm.ui.theme.PrimermvvmTheme

/**
 * #main_activity #navegacion #animacion
 * Actividad principal de la aplicación con Jetpack Compose y transiciones animadas de alto impacto.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrimermvvmTheme {
                MainApp()
            }
        }
    }
}

/**
 * #navegacion #tabs #animacion - Control de flujo animado entre pantallas de Autenticación (Login/Registro) y Dashboard.
 */
@Composable
fun MainApp() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var usuarioActual by remember { mutableStateOf("") }
    var rolActual by remember { mutableStateOf("Ejecutivo Comercial") }
    var selectedAuthTab by remember { mutableIntStateOf(0) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        // #animacion #login_dashboard - Transición suave de escala y desvanecimiento entre login y dashboard
        AnimatedContent(
            targetState = isLoggedIn,
            transitionSpec = {
                fadeIn(animationSpec = tween(450, easing = FastOutSlowInEasing)) +
                        scaleIn(initialScale = 0.92f, animationSpec = tween(450)) togetherWith
                        fadeOut(animationSpec = tween(350)) +
                        scaleOut(targetScale = 1.05f, animationSpec = tween(350))
            },
            label = "LoginToDashboardTransition"
        ) { loggedIn ->
            if (loggedIn) {
                MainDashboardApp(
                    usuario = usuarioActual,
                    rol = rolActual,
                    onCerrarSesion = {
                        isLoggedIn = false
                        usuarioActual = ""
                        rolActual = "Ejecutivo Comercial"
                        selectedAuthTab = 0
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    TabRow(selectedTabIndex = selectedAuthTab) {
                        // #tab #login - Pestaña para formulario de Iniciar sesión
                        Tab(
                            selected = selectedAuthTab == 0,
                            onClick = { selectedAuthTab = 0 },
                            text = { Text("Iniciar sesión") }
                        )
                        // #tab #registro - Pestaña para formulario de Registro
                        Tab(
                            selected = selectedAuthTab == 1,
                            onClick = { selectedAuthTab = 1 },
                            text = { Text("Registrarse") }
                        )
                    }

                    // #animacion #tabs_auth - Transición animada horizontal entre Login y Registro
                    AnimatedContent(
                        targetState = selectedAuthTab,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width } + fadeOut()
                                )
                            } else {
                                (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> width } + fadeOut()
                                )
                            }
                        },
                        label = "AuthTabsAnimation"
                    ) { tabIndex ->
                        when (tabIndex) {
                            0 -> LoginScreen(
                                onLoginExitoso = { usuario, rol ->
                                    usuarioActual = usuario
                                    rolActual = rol
                                    isLoggedIn = true
                                },
                                onIrARegistro = { selectedAuthTab = 1 }
                            )
                            1 -> RegistroScreen(
                                onIrALogin = { selectedAuthTab = 0 },
                                onRegistroExitoso = { usuario ->
                                    usuarioActual = usuario
                                    rolActual = "Ejecutivo Comercial"
                                    isLoggedIn = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * #dashboard #navegacion #scaffold #aviso_alertas #animacion #badge_pulse
 * Dashboard principal con barra de navegación inferior animada e indicador pulsante de alertas.
 */
@Composable
fun MainDashboardApp(
    usuario: String,
    rol: String,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OportunidadesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentNavDestination by remember { mutableIntStateOf(0) } // 0: Lista, 1: Alertas, 2: Nueva, 3: Perfil
    var perfilBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // #animacion #badge_pulse - Transición infinita para pulsar el badge de notificación cuando hay alertas pendientes
    val infiniteTransition = rememberInfiniteTransition(label = "BadgePulseTransition")
    val badgeScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BadgePulseScale"
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                // #boton #nav_oportunidades - Botón de navegación a Oportunidades
                NavigationBarItem(
                    selected = currentNavDestination == 0 && uiState.oportunidadSeleccionadaId == null,
                    onClick = {
                        viewModel.seleccionarOportunidad(null)
                        currentNavDestination = 0
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Oportunidades") },
                    label = { Text("Oportunidades") }
                )
                // #boton #nav_alertas #aviso_alertas #animacion #badge_pulse - Botón de navegación a Alertas con badge pulsante animado
                NavigationBarItem(
                    selected = currentNavDestination == 1 && uiState.oportunidadSeleccionadaId == null,
                    onClick = {
                        viewModel.seleccionarOportunidad(null)
                        currentNavDestination = 1
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.totalAlertasSinLeer > 0) {
                                    Badge(
                                        modifier = Modifier.scale(if (uiState.totalAlertasSinLeer > 0) badgeScale else 1.0f)
                                    ) {
                                        Text(uiState.totalAlertasSinLeer.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alertas")
                        }
                    },
                    label = { Text("Alertas") }
                )
                // #boton #nav_nueva - Botón de navegación a Nueva Oportunidad
                NavigationBarItem(
                    selected = currentNavDestination == 2 && uiState.oportunidadSeleccionadaId == null,
                    onClick = {
                        viewModel.seleccionarOportunidad(null)
                        currentNavDestination = 2
                    },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Nueva") },
                    label = { Text("Nueva") }
                )
                // #boton #nav_perfil #camara - Botón de navegación al Perfil y Foto de perfil con cámara
                NavigationBarItem(
                    selected = currentNavDestination == 3 && uiState.oportunidadSeleccionadaId == null,
                    onClick = {
                        viewModel.seleccionarOportunidad(null)
                        currentNavDestination = 3
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val selId = uiState.oportunidadSeleccionadaId

            // #animacion #transicion_vistas - Transición animada suave entre pantallas del dashboard y detalle
            AnimatedContent(
                targetState = selId to currentNavDestination,
                transitionSpec = {
                    fadeIn(animationSpec = tween(350)) + scaleIn(initialScale = 0.96f) togetherWith
                            fadeOut(animationSpec = tween(250))
                },
                label = "DashboardScreensAnimation"
            ) { (selectedOportunidadId, navDest) ->
                if (selectedOportunidadId != null) {
                    OportunidadDetalleScreen(
                        oportunidadId = selectedOportunidadId,
                        uiState = uiState,
                        usuarioActual = usuario,
                        rol = rol,
                        onVolver = { viewModel.seleccionarOportunidad(null) },
                        onCambiarEstado = { nuevoEstado ->
                            viewModel.actualizarEstadoOportunidad(selectedOportunidadId, nuevoEstado)
                        },
                        onEditarOportunidad = { opId, nomOp, tipOp, descOp, fuenteOp, prio, pot, nomEv, fIni, fTer, hor, dir, com, reg, asis, obs ->
                            viewModel.editarOportunidadCompleta(opId, nomOp, tipOp, descOp, fuenteOp, prio, pot, nomEv, fIni, fTer, hor, dir, com, reg, asis, obs)
                        },
                        onAgregarSeguimiento = { resp, tipo, res, prox, fechaProx, obs ->
                            viewModel.agregarSeguimiento(selectedOportunidadId, usuario.ifBlank { resp }, tipo, res, prox, fechaProx, obs)
                        },
                        onRegistrarResultado = { fecha, asis, cont, inter, insc, obs, eval ->
                            viewModel.registrarResultadoActivacion(selectedOportunidadId, fecha, asis, cont, inter, insc, obs, eval)
                        }
                    )
                } else {
                    when (navDest) {
                        0 -> OportunidadesListScreen(
                            uiState = uiState,
                            onSearchTextChange = { viewModel.onBusquedaTextoChange(it) },
                            onFilterEstadoChange = { viewModel.onFiltroEstadoChange(it) },
                            onFilterPrioridadChange = { viewModel.onFiltroPrioridadChange(it) },
                            onLimpiarFiltros = { viewModel.limpiarFiltros() },
                            onOportunidadClick = { id -> viewModel.seleccionarOportunidad(id) },
                            onNuevaOportunidadClick = { currentNavDestination = 2 }
                        )
                        1 -> AlertasScreen(
                            uiState = uiState,
                            onMarcarLeida = { alertaId -> viewModel.marcarAlertaLeida(alertaId) },
                            onVerOportunidad = { opId -> viewModel.seleccionarOportunidad(opId) }
                        )
                        2 -> NuevaOportunidadScreen(
                            onVolver = { currentNavDestination = 0 },
                            onCrearOportunidad = { nomOp, tipOp, desc, fuentes, resp, prio, pot, nomEv, fIni, fTer, hor, dir, com, reg, pubEsp, pubObj, asis, cond, req, cost, nomOrg, tipOrg, rubOrg, webOrg, nomCont, cargCont, areaCont, corrCont, telCont, obs ->
                                viewModel.crearOportunidadCompleta(
                                    nomOp, tipOp, desc, fuentes, resp.ifBlank { usuario }, prio, pot,
                                    nomEv, fIni, fTer, hor, dir, com, reg,
                                    pubEsp, pubObj, asis, cond, req, cost,
                                    nomOrg, tipOrg, rubOrg, webOrg,
                                    nomCont, cargCont, areaCont, corrCont, telCont, obs
                                )
                                currentNavDestination = 0
                            }
                        )
                        3 -> InicioScreen(
                            usuario = usuario,
                            rol = rol,
                            uiState = uiState,
                            onCerrarSesion = onCerrarSesion,
                            perfilBitmap = perfilBitmap,
                            onPerfilBitmapChange = { perfilBitmap = it },
                            onVerOportunidad = { opId -> viewModel.seleccionarOportunidad(opId) }
                        )
                    }
                }
            }
        }
    }
}
