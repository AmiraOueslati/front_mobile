import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication11.models.Product
import com.example.myapplication11.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

sealed interface ProductUiState {
    data class Success(val produits: List<Product>) : ProductUiState
    data class Error(val message: String) : ProductUiState
    object Loading : ProductUiState
    object ProductCreated : ProductUiState // État pour quand un produit est créé
}

class ProductViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState

    fun fetchProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                val produits = RetrofitClient.apiService.getAllProducts()
                _uiState.value = ProductUiState.Success(produits)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value =
                    ProductUiState.Error("Erreur lors de la récupération des produits: ${e.localizedMessage}")
            }
        }
    }

    // Fonction pour créer un produit
    fun createProduct(
        name: String, description: String, location: String,
        state: String, category: String, imageFile: File
    ) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                // Préparer les RequestBody pour les champs texte
                val nameRequestBody = RequestBody.create("text/plain".toMediaType(), name)
                val descriptionRequestBody =
                    RequestBody.create("text/plain".toMediaType(), description)
                val locationRequestBody = RequestBody.create("text/plain".toMediaType(), location)
                val stateRequestBody = RequestBody.create("text/plain".toMediaType(), state)
                val categoryRequestBody = RequestBody.create("text/plain".toMediaType(), category)

                // Préparer l'image
                val imageRequestBody = RequestBody.create("image/jpeg".toMediaType(), imageFile)
                val imagePart =
                    MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)

                // Appel à l'API pour créer le produit
                val response = RetrofitClient.apiService.createProduct(
                    nameRequestBody, descriptionRequestBody, locationRequestBody,
                    stateRequestBody, categoryRequestBody, imagePart
                )

                // Vérifier la réponse
                if (response.isSuccessful) {
                    _uiState.value = ProductUiState.ProductCreated // L'état après succès
                } else {
                    _uiState.value = ProductUiState.Error("Erreur lors de la création du produit")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ProductUiState.Error("Erreur: ${e.localizedMessage}")
            }
        }
    }
}