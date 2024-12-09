import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication11.models.LoginRequest
import com.example.myapplication11.models.LoginResponse
import com.example.myapplication11.models.User
import com.example.myapplication11.network.RetrofitClient
import com.example.myapplication11.network.RetrofitClient.apiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel : ViewModel() {

    // État pour l'inscription
    private val _signUpResponse = MutableStateFlow<Response<Unit>?>(null)
    val signUpResponse: StateFlow<Response<Unit>?> = _signUpResponse

    // État pour la connexion
    private val _loginResponse = MutableStateFlow<Response<LoginResponse>?>(null)
    val loginResponse: StateFlow<Response<LoginResponse>?> = _loginResponse

    // État pour les erreurs
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Gestion centralisée des erreurs
    private fun handleError(exception: Exception, message: String) {
        exception.printStackTrace()
        _errorMessage.value = "$message: ${exception.localizedMessage}"
    }

    fun signUp(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.signUp(user)
                if (!response.isSuccessful) {
                    Log.e("SignUp", "Error: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("SignUp", "Exception occurred: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }




    // Connexion d'un utilisateur
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = LoginRequest(username, password)
                val response = RetrofitClient.apiService.login(request)
                _loginResponse.value = response

                if (!response.isSuccessful) {
                    _errorMessage.value = "Erreur lors de la connexion: ${response.message()}"
                } else {
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                handleError(e, "Erreur de connexion")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
