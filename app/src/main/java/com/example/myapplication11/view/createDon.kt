

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication11.models.Product
import com.example.myapplication11.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddImageScreen(onSubmit: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Pour la gestion des erreurs et du chargement
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Clothes", "Education", "Humanity")

    // Vérification des permissions pour accéder au stockage externe
    val context = LocalContext.current
    val activity = context as Activity
    val permissionGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    if (!permissionGranted) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1001 // Code de requête pour la permission
        )
    }

    // Launcher pour sélectionner une image
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> selectedImageUri = uri }
    )

    // Extraction des extras de l'intent
    val bundle = activity.intent.extras
    if (bundle != null) {
        val value = bundle.getString("key") // Remplacez "key" par la clé de votre extra
        value?.let {
            name = it // Utilisez cette valeur pour pré-remplir le champ
        }
    } else {
        // Gestion de l'absence de l'extra dans l'intent
        Log.d("AddImageScreen", "No extras found in intent.")
    }

    // Fonction pour ajouter un produit
    fun addProduct() {
        // Créer un objet produit avec les données saisies
        val newProduct = Product(
            name = name,
            description = description,
            location = location,
            state = state,
            category = selectedCategory,
            imageUrl = selectedImageUri.toString() // Vous pouvez gérer l'image selon vos besoins
        )

        // Utiliser Retrofit pour appeler l'API et ajouter le produit
        isLoading = true
        errorMessage = null

        // Effectuer l'appel API pour créer le produit
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.createProduct(newProduct)
                if (response.isSuccessful) {
                    // Succès
                    onSubmit()
                } else {
                    // Erreur de la requête API
                    errorMessage = "Failed to add product: ${response.message()}"
                }
            } catch (e: Exception) {
                // Gestion des exceptions
                errorMessage = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val customTypography = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp // Taille de police du premier texte
        )

        val smallerTypography = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp // Taille de police plus petite pour le deuxième texte
        )

        // Affichage des messages d'erreur ou de chargement
        if (isLoading) {
            CircularProgressIndicator()
        }
        errorMessage?.let {
            Text(text = it, color = Color.Red)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Centre les textes horizontalement
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Donate Now!",
                style = customTypography,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(bottom = 8.dp) // Espacement entre les deux textes
            )
            Text(
                text = "Your donation will help us change the lives of those in need.",
                style = smallerTypography, // Utilisation du style plus petit
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(20.dp)) // Espace entre les sections

        // Champs de saisie
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text("State") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                label = { Text("Category") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Image")
        }

        selectedImageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { selectedImageUri = null },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Remove Image")
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espace entre les boutons

        Button(
            onClick = { addProduct() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Product")
        }
    }
}