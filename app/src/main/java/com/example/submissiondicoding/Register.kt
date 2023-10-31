package com.example.submissiondicoding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.submissiondicoding.databinding.ActivityRegisterBinding
import com.example.submissiondicoding.di.Injection
import com.example.submissiondicoding.model.SignupViewModel
import com.example.submissiondicoding.model.ViewModelFactory
import com.example.submissiondicoding.preferences.UserPreference

// Ubah nama variabel binding
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        // Set visibility
        val viewsToAnimate = arrayOf(
            binding.imgLogo, binding.txtCreateAccount, binding.tvNameTitle,
            binding.edtName, binding.tvEmailTitle, binding.edtEmail,
            binding.tvPasswordTitle, binding.edtPassword, binding.btnRegister,
            binding.layoutTextRegister, binding.tvIsHaveAccount, binding.tvToLogin
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

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edtName.error = "Please input your name"
                }

                email.isEmpty() -> {
                    binding.edtEmail.error = "Please input your email"
                }

                password.isEmpty() -> {
                    binding.edtPassword.error = "Please input your password"
                }

                else -> {
                    val repository = Injection.provideRepository(this)
                    val userPreference = UserPreference.getInstance(this.dataStore)
                    val factory = ViewModelFactory(repository, userPreference)
                    val signupViewModel: SignupViewModel by viewModels { factory }
                    signupViewModel.registerAccount(name, email, password)
                }
            }
        }
        binding.tvToLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}
