package com.example.myapplication11.view

import AddImageScreen
import ProductViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication11.R
import com.example.myapplication11.data.UserStore
import com.example.myapplication11.models.User
import com.example.myapplication11.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "loginScreen") {
        composable("loginScreen") { LoginScreen(navController) }
        composable("signUpScreen") { SignUpScreen(navController) }
        composable("homeScreen") { HomeScreen(navController) }
        composable("welcomeScreen") { WelcomeScreen(navController) }

        // Passer le viewModel à AddImageScreen sans Hilt
        composable("addImageScreen") {
            // Récupérer le viewModel avec viewModel() sans Hilt
            val viewModel: ProductViewModel = viewModel() // Utiliser viewModel() au lieu de hiltViewModel()

            AddImageScreen(
                viewModel = viewModel, // Passer le viewModel au composable
                onSubmit = {
                    // Gérer la soumission ici (par exemple, retourner à la page d'accueil après soumission)
                    navController.navigate("homeScreen") {
                        // Optionnel : fermer les autres écrans ou revenir en arrière si nécessaire
                        popUpTo("addImageScreen") { inclusive = true }
                    }
                }
            )
        }
    }
}



@Composable
fun LoginScreen(navController: NavController) {
    val passwordVisibility = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val store = UserStore(context)
    val tokenUser = store.getAccessToken.collectAsState(initial = "")
    val password = store.getPassword.collectAsState(initial = "")
    val userName = remember { mutableStateOf(TextFieldValue(tokenUser.value)) }
    val userPassword = remember { mutableStateOf(TextFieldValue(password.value)) }
    val rememberMeChecked = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    LaunchedEffect(tokenUser.value) {
        userName.value = TextFieldValue(tokenUser.value)
        userPassword.value = TextFieldValue(password.value)
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.donnation),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 10.dp)
        )
        Text(
            text = "Welcome to Donation App",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = userName.value,
            onValueChange = { userName.value = it },
            label = { Text(text = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = userPassword.value,
            onValueChange = { userPassword.value = it },
            label = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                    Icon(
                        imageVector = if (passwordVisibility.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Checkbox(
                checked = rememberMeChecked.value,
                onCheckedChange = { rememberMeChecked.value = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6200EE))
            )
            Text(text = "Remember Me", modifier = Modifier.padding(start = 8.dp))
        }

        // Affichage du message d'erreur s'il y en a
        if (errorMessage.value.isNotEmpty()) {
            Text(
                text = errorMessage.value,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }

        Button(
            onClick = {
                val email = userName.value.text
                val password = userPassword.value.text

                // Envoi des identifiants au backend via Retrofit
                val credentials = mapOf("email" to email, "password" to password)
                RetrofitClient.apiService.authenticate(credentials)
                    .enqueue(object : retrofit2.Callback<User> {
                        override fun onResponse(
                            call: retrofit2.Call<User>,
                            response: retrofit2.Response<User>
                        ) {
                            if (response.isSuccessful) {
                                // Si l'authentification réussit
                                errorMessage.value = ""

                                // Enregistrer les données si "Se souvenir de moi" est activé
                                if (rememberMeChecked.value) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        store.saveToken(email)
                                        store.savePassword(password)
                                    }
                                }

                                // Navigation vers HomeScreen après une authentification réussie
                                navController.navigate("welcomeScreen")
                            } else {
                                // Si l'authentification échoue
                                errorMessage.value = when (response.code()) {
                                    404 -> "Tu n'as pas de compte. Veuillez vous inscrire."
                                    else -> "Email ou mot de passe invalide"
                                }
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                            errorMessage.value = "Erreur réseau : ${t.message}"
                        }
                    })
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {
            Text(text = "Login", fontSize = 18.sp)
        }

        // Texte pour le mot de passe oublié
        Text(
            text = "Forgot Password?",
            fontSize = 14.sp,
            color = Color(0xFF6200EE),
            modifier = Modifier
                .padding(top = 20.dp)
                .clickable { /* Ajouter la fonctionnalité */ }
        )

        // Texte pour s'inscrire
        Text(
            text = "Don't have an account? Sign Up",
            fontSize = 14.sp,
            color = Color(0xFF6200EE),
            modifier = Modifier
                .padding(top = 10.dp)
                .clickable {
                    navController.navigate("signUpScreen")
                }
        )
    }
}