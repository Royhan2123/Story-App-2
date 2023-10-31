package com.example.submissiondicoding.model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissiondicoding.preferences.UserPreference
import kotlinx.coroutines.launch

class LogoutViewModel(private val pref: UserPreference) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            pref.discardToken()
            pref.logout()
        }
    }

}