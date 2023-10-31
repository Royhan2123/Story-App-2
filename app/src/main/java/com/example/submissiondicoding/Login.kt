package com.example.submissiondicoding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.submissiondicoding.databinding.ActivityLoginBinding
import com.example.submissiondicoding.di.Injection
import com.example.submissiondicoding.model.LoginViewModel
import com.example.submissiondicoding.model.ViewModelFactory
import com.example.submissiondicoding.preferences.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupView()
        checkField()
        playAnimation()
        binding.txtRegister.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        // Set visibility
        val viewsToAnimate = arrayOf(
            binding.imgLogo, binding.txtloginAccount, binding.tvEmailTitle,
            binding.edtEmail, binding.txtPassword, binding.edtPassword,
            binding.btnLogin, binding.layoutTextRegister, binding.tvIsHaventAccount, binding.txtRegister
        )

        for (view in viewsToAnimate) {
            view.visibility = View.VISIBLE
            view.alpha = 0f
        }

        // Create animator set
        val animatorSet = AnimatorSet()
        ObjectAnimator.ofFloat(binding.imgLogo, View.TRANSLATION_X, -50f, 50f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val duration = 500L

        // Play animations sequentially
        val animators = viewsToAnimate.map {
            ObjectAnimator.ofFloat(it, View.ALPHA, 1f).setDuration(duration)
        }
        animatorSet.playSequentially(*animators.toTypedArray())

        // Start animation
        animatorSet.start()
    }

    private fun setupView() {
        // Konfigurasi tampilan fullscreen
        @Suppress("DEPRECATION")
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

    private fun checkField() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edtEmail.error = getString(R.string.enter_email)
                }
                password.isEmpty() -> {
                    binding.edtPassword.error = getString(R.string.enter_password)
                }
                else -> {
                    val repository = Injection.provideRepository(this)
                    val userPreference = UserPreference.getInstance(this.dataStore)
                    val factory = ViewModelFactory(repository, userPreference)

                    val loginViewModel: LoginViewModel by viewModels { factory }

                    loginViewModel.loginAccount(email, password)
                    loginViewModel.loginResult.observe(this) { result ->
                        when (result) {
                            is com.example.submissiondicoding.api.Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                                is com.example.submissiondicoding.api.Result.Success -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                Toast.makeText(this@Login,R.string.succes, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Login, MainActivity::class.java)
                                startActivity(intent)
                            }
                            is com.example.submissiondicoding.api.Result.Error -> {
                                Toast.makeText(this@Login,R.string.failed, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}
