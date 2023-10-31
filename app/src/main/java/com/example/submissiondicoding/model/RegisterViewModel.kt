package com.example.submissiondicoding.model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissiondicoding.api.StoryRepository
import kotlinx.coroutines.launch


class SignupViewModel(private val repository: StoryRepository) : ViewModel() {

    fun registerAccount(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.registerAccount(name, email, password)
        }
    }
}