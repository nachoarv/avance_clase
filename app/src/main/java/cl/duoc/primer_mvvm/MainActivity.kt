package cl.duoc.primer_mvvm

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.primer_mvvm.`01_formulario`.RegistroScreen
import cl.duoc.primer_mvvm.`02_login`.InicioScreen
import cl.duoc.primer_mvvm.`02_login`.LoginScreen
import cl.duoc.primer_mvvm.`03_oportunidades`.*
import cl.duoc.primer_mvvm.ui.theme.PrimermvvmTheme

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

@Composable
fun MainApp() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var usuarioActual by remember { mutableStateOf("") }
    var rolActual by remember { mutableStateOf("Ejecutivo Comercial") }
    var selectedAuthTab by remember { mutableIntStateOf(0) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (isLoggedIn) {
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
                    Tab(
                        selected = selectedAuthTab == 0,
                        onClick = { selectedAuthTab = 0 },
                        text = { Text("Iniciar sesión") }
                    )
                    Tab(
                        selected = selectedAuthTab == 1,
                        onClick = { selectedAuthTab = 1 },
                        text = { Text("Registrarse") }
                    )
                }

                when (selectedAuthTab) {
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

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentNavDestination == 0 && uiState.oportunidadSeleccionadaId == null,
                    onClick = {
                        viewModel.seleccionarOportunidad(null)
                        currentNavDestination = 0
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Oportunidades") },
                    label = { Text("Oportunidades") }
                )
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
                                    Badge { Text(uiState.totalAlertasSinLeer.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alertas")
                        }
                    },
                    label = { Text("Alertas") }
                )
                NavigationBarItem(
                    selected = currentNavDestination == 2 && uiState.oportunidadSeleccionadaId == null,
                    onClick = {
                        viewModel.seleccionarOportunidad(null)
                        currentNavDestination = 2
                    },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Nueva") },
                    label = { Text("Nueva") }
                )
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
            if (selId != null) {
                OportunidadDetalleScreen(
                    oportunidadId = selId,
                    uiState = uiState,
                    rol = rol,
                    onVolver = { viewModel.seleccionarOportunidad(null) },
                    onCambiarEstado = { nuevoEstado ->
                        viewModel.actualizarEstadoOportunidad(selId, nuevoEstado)
                    },
                    onAgregarSeguimiento = { resp, tipo, res, prox, fechaProx, obs ->
                        viewModel.agregarSeguimiento(selId, usuario.ifBlank { resp }, tipo, res, prox, fechaProx, obs)
                    },
                    onRegistrarResultado = { fecha, asis, cont, inter, insc, obs, eval ->
                        viewModel.registrarResultadoActivacion(selId, fecha, asis, cont, inter, insc, obs, eval)
                    }
                )
            } else {
                when (currentNavDestination) {
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
                        onCerrarSesion = onCerrarSesion,
                        perfilBitmap = perfilBitmap,
                        onPerfilBitmapChange = { perfilBitmap = it }
                    )
                }
            }
        }
    }
}
