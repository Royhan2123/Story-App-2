package com.example.submissiondicoding.model
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissiondicoding.api.StoryRepository
import kotlinx.coroutines.launch
import java.io.File
import com.example.submissiondicoding.api.Result


class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private var _uploadResult = MutableLiveData<Result<Unit>>()
    val uploadResult: LiveData<Result<Unit>> get() = _uploadResult
    fun getStoryPaging(token: String) = storyRepository.getStoryPaging(token)

    fun getStoryDetail(token: String, id: String) = storyRepository.getDetailStory(token, id)

    fun uploadStory(token: String, description: String, photo: File) {
        viewModelScope.launch {
            Result.Loading
            try {
                val result = storyRepository.uploadStory(token, description, photo)
                _uploadResult.value = Result.Success(result)

                // biar refresh
                storyRepository.getStoryPaging(token)

            } catch (e: Exception) {
                _uploadResult.value = Result.Error(e.message ?: "Failed to upload story")
            }
        }
    }

    fun getMapStory(token: String) = storyRepository.getMapStory(token)



}