import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddImageScreen(
    viewModel: ProductViewModel, // Pass the ViewModel as a parameter
    onSubmit: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // For error and loading state handling
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Clothes", "Education", "Humanity")

    val context = LocalContext.current
    val activity = context as Activity
    val permissionGranted = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    // Request permission if not granted
    if (!permissionGranted) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1001 // Permission request code
        )
    }

    // Image picker launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
            uri?.let {
                Log.d("AddImageScreen", "Image URI: $it")
                // Process image file here if needed
                val imageFile = uriToFile(it, context)
                imageFile?.let { file ->
                    Log.d("AddImageScreen", "File path: ${file.absolutePath}")
                }
            }
        }
    )

    // Function to convert Uri to a File
    fun uriToFile(uri: Uri, context: android.content.Context): File? {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        inputStream?.let {
            // Create a temporary file
            val tempFile = File(context.cacheDir, "temp_image.jpg")
            try {
                FileOutputStream(tempFile).use { outputStream ->
                    it.copyTo(outputStream)
                }
                return tempFile
            } catch (e: IOException) {
                Log.e("AddImageScreen", "Error saving image", e)
                errorMessage = "Error saving image: ${e.localizedMessage}"
            } finally {
                it.close()
            }
        }
        return null
    }

    val imagePart = selectedImageUri?.let {
        val imageFile = uriToFile(it, context)
        imageFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaType())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Handling loading and error states
        if (isLoading) {
            CircularProgressIndicator()
        }
        errorMessage?.let {
            Text(text = it, color = Color.Red)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Donate Now!", color = Color(0xFF1976D2), modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Your donation will help us change the lives of those in need.", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input fields
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
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
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
                modifier = Modifier.fillMaxWidth().height(200.dp),
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

        Spacer(modifier = Modifier.height(16.dp))

        // Submit product
        Button(
            onClick = {
                if (name.isNotBlank() && description.isNotBlank() && location.isNotBlank() && state.isNotBlank() && selectedCategory.isNotBlank() && selectedImageUri != null) {
                    // Appeler la fonction createProduct dans le ViewModel pour enregistrer le produit
                    val imageFile = uriToFile(selectedImageUri!!, context)
                    if (imageFile != null) {
                        viewModel.createProduct(name, description, location, state, selectedCategory, imageFile)
                    } else {
                        errorMessage = "Failed to process the image"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Product")
        }

        // Handle UI state changes
        when (val uiState = viewModel.uiState.collectAsState().value) {
            is ProductUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is ProductUiState.ProductCreated -> {
                Toast.makeText(LocalContext.current, "Product Created", Toast.LENGTH_SHORT).show()
                onSubmit() // You can call onSubmit() if necessary
            }
            is ProductUiState.Error -> {
                Toast.makeText(LocalContext.current, uiState.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
}

fun uriToFile(uri: Uri, context: Context): File? {
    val contentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(uri)

    // Check if the input stream is valid
    inputStream?.let {
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        try {
            FileOutputStream(tempFile).use { outputStream -> it.copyTo(outputStream) }
            return tempFile
        } catch (e: IOException) {
            Log.e("AddImageScreen", "Error saving image: ${e.localizedMessage}")
            return null
        } finally {
            it.close()
        }
    }
    return null
}