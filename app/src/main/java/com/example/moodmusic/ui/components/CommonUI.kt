package com.example.moodmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodmusic.ui.theme.*

@Composable
fun LogoOnda() {
    Box(
        modifier = Modifier.size(width = 200.dp, height = 90.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "♩", fontSize = 22.sp, color = ColorMorado)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Mood&Music",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Cursive,
                    color = ColorAzul
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "♪", fontSize = 18.sp, color = ColorMorado)
            }
        }
    }
}

@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        placeholder = { Text(text = placeholder, color = ColorSubtexto, fontSize = 14.sp) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = ColorMorado,
            unfocusedBorderColor    = ColorBorde,
            focusedContainerColor   = ColorCampo,
            unfocusedContainerColor = ColorCampo
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CampoContrasena(
    valor: String,
    onValorChange: (String) -> Unit,
    mostrar: Boolean,
    onToggleMostrar: () -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        placeholder = { Text("Ingrese la contraseña", color = ColorSubtexto, fontSize = 14.sp) },
        singleLine = true,
        visualTransformation = if (mostrar) VisualTransformation.None
        else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleMostrar) {
                Icon(
                    imageVector = if (mostrar) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    tint = ColorSubtexto
                )
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = ColorMorado,
            unfocusedBorderColor    = ColorBorde,
            focusedContainerColor   = ColorCampo,
            unfocusedContainerColor = ColorCampo
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun BotonGradiente(
    texto: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(ColorAzul, ColorMorado)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(
                text = texto,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorBotonGris,
            contentColor   = ColorTexto
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = texto,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
