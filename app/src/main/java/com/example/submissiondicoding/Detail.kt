package com.example.submissiondicoding

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.submissiondicoding.api.Result
import com.example.submissiondicoding.databinding.ActivityDetailBinding
import com.example.submissiondicoding.di.Injection
import com.example.submissiondicoding.model.LoginViewModel
import com.example.submissiondicoding.model.MainViewModel
import com.example.submissiondicoding.model.ViewModelFactory
import com.example.submissiondicoding.preferences.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Suppress("DEPRECATION")
class Detail : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var storyViewModel: MainViewModel

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()

        val id = intent.getStringExtra(EXTRA_ID)
        if (!id.isNullOrEmpty()) {
            setupEvent(id)
        } else {
            Toast.makeText(this@Detail, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupView() {
        hideSystemUI()
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        val userPref = UserPreference.getInstance(dataStore)
        val repository = Injection.provideRepository(this)
        val viewModelFactory = ViewModelFactory(repository, userPref)
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        storyViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private fun setupEvent(id: String) {
        loginViewModel.readToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                storyViewModel.getStoryDetail(token, id).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val storyData = result.data

                            Glide.with(this)
                                .load(storyData.photoUrl)
                                .error(R.drawable.baseline_broken_image_24)
                                .centerCrop()
                                .into(binding.imgDetail)
                            binding.txtName.text = storyData.name
                            binding.txtDesc.text = storyData.description
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@Detail,
                                "Terjadi Kesalahan: ${result.err}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@Detail, "Token is empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
