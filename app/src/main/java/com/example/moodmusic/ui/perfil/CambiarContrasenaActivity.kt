package com.example.moodmusic.ui.perfil

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodmusic.ui.main.*
import com.example.moodmusic.ui.theme.MoodMusicTheme
import com.example.moodmusic.ui.theme.*
import com.example.moodmusic.viewmodel.UsuarioViewModel

class CambiarContrasenaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodMusicTheme {
                PantallaCambiarContrasena(onVolver = { finish() })
            }
        }
    }
}

@Composable
fun PantallaCambiarContrasena(
    viewModel: UsuarioViewModel = viewModel(),
    onVolver: () -> Unit
) {
    var passActual by remember { mutableStateOf("") }
    var nuevaPass by remember { mutableStateOf("") }
    var confirmarPass by remember { mutableStateOf("") }
    
    var mostrarPassActual by remember { mutableStateOf(false) }
    var mostrarNuevaPass by remember { mutableStateOf(false) }
    var mostrarConfirmarPass by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val usuario = viewModel.usuarioActual

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        Text(
            text = "Cambiar\ncontraseña:",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(60.dp))

        CajaTextoPasswordDiseno(
            valor = passActual,
            label = "Contraseña anterior",
            mostrar = mostrarPassActual,
            onToggle = { mostrarPassActual = !mostrarPassActual }
        ) { passActual = it }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CajaTextoPasswordDiseno(
            valor = nuevaPass,
            label = "Contraseña nueva",
            mostrar = mostrarNuevaPass,
            onToggle = { mostrarNuevaPass = !mostrarNuevaPass }
        ) { nuevaPass = it }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CajaTextoPasswordDiseno(
            valor = confirmarPass,
            label = "Confirmar contraseña",
            mostrar = mostrarConfirmarPass,
            onToggle = { mostrarConfirmarPass = !mostrarConfirmarPass }
        ) { confirmarPass = it }

        Spacer(modifier = Modifier.height(60.dp))

        // Botón Guardar (Gradiente)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(ColorCampo)
                .border(
                    BorderStroke(1.5.dp, Brush.linearGradient(listOf(ColorAzul, ColorMorado))),
                    RoundedCornerShape(16.dp)
                )
                .clickable {
                    if (passActual != usuario?.contrasena) {
                        Toast.makeText(context, "La contraseña anterior es incorrecta", Toast.LENGTH_SHORT).show()
                    } else if (nuevaPass.isEmpty()) {
                        Toast.makeText(context, "La nueva contraseña no puede estar vacía", Toast.LENGTH_SHORT).show()
                    } else if (nuevaPass != confirmarPass) {
                        Toast.makeText(context, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.actualizarContrasena(nuevaPass)
                        Toast.makeText(context, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show()
                        onVolver()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Guardar", color = ColorTexto, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Cancelar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .background(ColorCampo, RoundedCornerShape(16.dp))
                .border(1.dp, ColorBorde, RoundedCornerShape(16.dp))
                .clickable { onVolver() },
            contentAlignment = Alignment.Center
        ) {
            Text("Cancelar", color = ColorTexto, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CajaTextoPasswordDiseno(
    valor: String,
    label: String,
    mostrar: Boolean,
    onToggle: () -> Unit,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .background(ColorCampo, RoundedCornerShape(14.dp))
            .border(1.dp, ColorBorde, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (valor.isEmpty()) {
            Text(label, color = ColorSubtexto, fontSize = 14.sp)
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = valor,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                visualTransformation = if (mostrar) VisualTransformation.None else PasswordVisualTransformation(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = ColorTexto,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            
            IconButton(onClick = onToggle, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = if (mostrar) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    tint = ColorSubtexto,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
