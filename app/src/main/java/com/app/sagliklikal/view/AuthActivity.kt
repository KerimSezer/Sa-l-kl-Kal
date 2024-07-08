package com.app.sagliklikal.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.app.sagliklikal.R
import com.app.sagliklikal.databinding.ActivityAuthBinding
import com.app.sagliklikal.viewmodel.AuthViewModel

// Oturum açma ve kayıt işlemlerini yöneten AuthActivity sınıfı
class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityAuthBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kenarları kenara etkinleştirme işlevi çağrılıyor
        enableEdgeToEdge()

        // AuthViewModel'in ViewModelProvider aracılığıyla alınması
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // NavHostFragment'in alınması ve navController'in atanması
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    // Desteklenen gezinme işlevi: Geri tuşuna basıldığında stack içinde yukarı doğru gezinme işlemi
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()

        // Kullanıcı oturum açmışsa MainActivity'e yönlendirme
        if (authViewModel.currentUser() != null) {
            val intent = Intent(this@AuthActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
