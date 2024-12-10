package com.example.myapplication11.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication11.R

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Titre principal
        Text(
            text = "Welcome Donater 😊",

            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF3E4A59) // Couleur personnalisée pour un look moderne
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Sous-titre
        Text(
            text = "Congratulations, you've joined the Geevers community",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF4A4A4A)
            ),
            modifier = Modifier.padding(horizontal = 20.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Image
        Image(
            painter = painterResource(id = R.drawable.hand),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 10.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Texte en gras au centre
        Text(
            text = "To get started, we need some information !",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                color = Color(0xFF3E4A59)
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Bouton avec couleur personnalisée
        Button(
            onClick = {  navController.navigate("homeScreen") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6D55C)),// Couleur jaune personnalisée
                    modifier = Modifier
                    .size(width = 300.dp, height = 60.dp)
        ) {
            Text(
                text = "Let's get started!",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            )
        }
    }
}
