package com.example.moodmusic.ui.avatar

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.moodmusic.R
import com.example.moodmusic.data.model.UsuarioEntity
import com.example.moodmusic.ui.main.*
import com.example.moodmusic.ui.registro_estado.EstadoAnimoActivity
import com.example.moodmusic.ui.theme.MoodMusicTheme
import com.example.moodmusic.ui.theme.*
import com.example.moodmusic.viewmodel.UsuarioViewModel

class SeleccionAvatarActivity : ComponentActivity() {

    private val viewModel: UsuarioViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(UsuarioViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MoodMusicTheme {
                PantallaSeleccionAvatar(
                    onVolver = { finish() },
                    onContinuar = { avatarSeleccionado ->
                        viewModel.actualizarAvatar(avatarSeleccionado)
                        
                        if (intent.getBooleanExtra("desdePerfil", false)) {
                            finish()
                        } else {
                            val intent = Intent(this@SeleccionAvatarActivity, EstadoAnimoActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaSeleccionAvatar(
    onVolver: () -> Unit,
    onContinuar: (Int) -> Unit
) {
    var avatarSeleccionado by remember { mutableStateOf<Int?>(null) }

    val listaAvatares = listOf(
        R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4,
        R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7, R.drawable.avatar8
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp))
                    .background(ColorCampo, RoundedCornerShape(12.dp))
                    .border(1.dp, ColorMorado.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable { onVolver() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Volver",
                    tint = ColorMorado,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Elige tu avatar",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(20.dp))
                .background(ColorCampo, RoundedCornerShape(20.dp))
                .border(
                    2.dp,
                    Brush.linearGradient(listOf(ColorAzul, ColorMorado)),
                    RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(200.dp)
            ) {
                itemsIndexed(listaAvatares) { index, avatar ->
                    ItemAvatar(
                        imagen = avatar,
                        seleccionado = avatarSeleccionado == index,
                        onClick = { avatarSeleccionado = index }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                avatarSeleccionado?.let { onContinuar(it) }
            },
            enabled = avatarSeleccionado != null,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(52.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorCampo,
                contentColor = ColorTexto,
                disabledContainerColor = ColorBotonGris.copy(alpha = 0.5f),
                disabledContentColor = ColorSubtexto.copy(alpha = 0.5f)
            ),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(ColorAzul, ColorMorado))
            )
        ) {
            Text(
                text = "Continuar",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ItemAvatar(
    imagen: Int,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(ColorCampo, RoundedCornerShape(16.dp))
            .border(
                width = if (seleccionado) 3.dp else 1.dp,
                brush = if (seleccionado)
                    Brush.linearGradient(listOf(ColorAzul, ColorMorado))
                else Brush.linearGradient(listOf(ColorBorde, ColorBorde)),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imagen),
            contentDescription = "Avatar",
            modifier = Modifier.size(50.dp)
        )
    }
}
