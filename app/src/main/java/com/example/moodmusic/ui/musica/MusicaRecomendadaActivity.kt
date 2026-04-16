package com.example.moodmusic.ui.musica

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodmusic.data.model.Cancion
import com.example.moodmusic.ui.main.*
import com.example.moodmusic.ui.perfil.PerfilActivity
import com.example.moodmusic.ui.registro_estado.EstadoAnimoActivity
import com.example.moodmusic.ui.theme.MoodMusicTheme

class MusicaRecomendadaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodMusicTheme {
                PantallaMusicaRecomendada(
                    onVolver = {
                        val intent = Intent(this, PerfilActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaMusicaRecomendada(onVolver: () -> Unit) {
    val canciones = listOf(
        Cancion("As It Was", "Harry Styles"),
        Cancion("Blinding Lights", "The Weeknd"),
        Cancion("Flowers", "Miley Cyrus"),
        Cancion("Kill Bill", "SZA"),
        Cancion("Anti-Hero", "Taylor Swift"),
        Cancion("Creepin'", "Metro Boomin, The Weeknd, 21 Savage"),
        Cancion("Die For You", "The Weeknd & Ariana Grande"),
        Cancion("Calm Down", "Rema & Selena Gomez"),
        Cancion("Unholy", "Sam Smith & Kim Petras"),
        Cancion("I'm Good (Blue)", "David Guetta & Bebe Rexha")
    )

    // Degradado de fondo similar al de la imagen
    val fondoGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE0F7FA), // Azul muy claro
            Color(0xFFF3E5F5), // Morado muy claro
            Color(0xFFFFFFFF)  // Blanco
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Botón volver estilizado
            IconButton(
                onClick = onVolver,
                modifier = Modifier
                    .size(45.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = ColorAzul,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Música para ti",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorTexto,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Usamos weight(1f) para que la lista ocupe el espacio disponible
            // y el scroll funcione perfectamente dentro de la columna.
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(canciones) { cancion ->
                    ItemCancion(cancion)
                }
            }
        }
    }
}

@Composable
fun ItemCancion(cancion: Cancion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder imagen (Gris como en la imagen)
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFAAB8C2))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = cancion.nombre,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTexto
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cancion.artista,
                    fontSize = 14.sp,
                    color = ColorSubtexto,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMusicaRecomendada() {
    MoodMusicTheme {
        PantallaMusicaRecomendada({})
    }
}
