package cl.duoc.primer_mvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cl.duoc.primer_mvvm.`01_formulario`.RegistroScreen
import cl.duoc.primer_mvvm.`02_login`.InicioScreen
import cl.duoc.primer_mvvm.`02_login`.LoginScreen
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
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (isLoggedIn) {
            InicioScreen(
                usuario = usuarioActual,
                onCerrarSesion = {
                    isLoggedIn = false
                    usuarioActual = ""
                    selectedTab = 0
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Iniciar sesión") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Registrarse") }
                    )
                }

                when (selectedTab) {
                    0 -> LoginScreen(
                        onLoginExitoso = { usuario ->
                            usuarioActual = usuario
                            isLoggedIn = true
                        },
                        onIrARegistro = { selectedTab = 1 }
                    )
                    1 -> RegistroScreen(
                        onIrALogin = { selectedTab = 0 },
                        onRegistroExitoso = { usuario ->
                            usuarioActual = usuario
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}
