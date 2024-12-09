import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication11.models.Product
import com.example.myapplication11.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface ProductUiState {
    data class Success(val produits: List<Product>) : ProductUiState
    data class Error(val message: String) : ProductUiState
    object Loading : ProductUiState
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
                _uiState.value = ProductUiState.Error("Erreur lors de la récupération des produits: ${e.localizedMessage}")
            }
        }
    }
}
