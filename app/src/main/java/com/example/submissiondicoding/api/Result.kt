package com.example.submissiondicoding.api
sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val err: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}