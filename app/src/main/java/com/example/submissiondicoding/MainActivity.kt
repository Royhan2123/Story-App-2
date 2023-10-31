package com.example.submissiondicoding
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissiondicoding.databinding.ActivityMainBinding
import com.example.submissiondicoding.di.Injection
import com.example.submissiondicoding.model.LoadingStateAdapter
import com.example.submissiondicoding.model.LoginViewModel
import com.example.submissiondicoding.model.LogoutViewModel
import com.example.submissiondicoding.model.MainViewModel
import com.example.submissiondicoding.model.UserStoryAdapter
import com.example.submissiondicoding.model.ViewModelFactory
import com.example.submissiondicoding.preferences.UserPreference
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: LogoutViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var storyViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        loginState()
        setupAction()
        setupAdapterAndList()
    }

    private fun loginState() {
        loginViewModel.readLoginState().observe(this) { isLogin ->
            if (isLogin) {
                supportActionBar?.title = "Story"
            } else {
                startActivity(Intent(this@MainActivity, Login::class.java))
                finish()
            }
        }
    }

    private fun setupViewModel() {
        val userPref = UserPreference.getInstance(dataStore)
        val repository = Injection.provideRepository(this)
        val viewModelFactory = ViewModelFactory(repository, userPref)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[LogoutViewModel::class.java]
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        storyViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        setupAdapterAndList()
    }

    private fun setupAction() {
        binding.imgLogout.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.logout()
                startActivity(Intent(this@MainActivity, Login::class.java))
                finish()
            }
        }

        binding.imgMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.addStory.setOnClickListener {
            val intent = Intent(this, AddStory::class.java)
            startActivity(intent)
        }
    }

    private fun setupAdapterAndList() {
        val userStoryAdapter = UserStoryAdapter()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recycleView.layoutManager = layoutManager
        binding.recycleView.adapter = userStoryAdapter

        loginViewModel.readToken().observe(this) { token ->
            binding.recycleView.adapter = userStoryAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    userStoryAdapter.retry()
                })

            storyViewModel.getStoryPaging(token).observe(this){ pagingData ->
                userStoryAdapter.submitData(lifecycle, pagingData)
            }
        }
    }
}