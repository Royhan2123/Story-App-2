package com.example.submissiondicoding.model
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submissiondicoding.api.StoryRepository
import com.example.submissiondicoding.api.response.LoginResponse
import com.example.submissiondicoding.preferences.UserPreference
import kotlinx.coroutines.launch
import com.example.submissiondicoding.api.Result
class LoginViewModel(private val repository: StoryRepository, private val userPreference: UserPreference) : ViewModel() {
    private var _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    fun readToken(): LiveData<String> {
        return userPreference.getToken().asLiveData()
    }

    fun readLoginState(): LiveData<Boolean> {
        return userPreference.readState().asLiveData()
    }
    fun loginAccount(email: String, password: String) {
        viewModelScope.launch {
            Result.Loading
            try {
                val result = repository.loginAccount(email, password)
                _loginResult.value = Result.Success(result)
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "Failed to upload story")
            }
        }
    }


}