package com.app.sagliklikal.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.app.sagliklikal.R
import com.app.sagliklikal.databinding.ActivityMainBinding

// Uygulamanın ana ekranını yöneten MainActivity sınıfı
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NavHostFragment'in alınması ve navController'in atanması
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_main_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // BottomNavigationView ile navController'in bağlanması
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
