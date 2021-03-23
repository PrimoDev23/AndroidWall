package com.example.androidwall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.androidwall.databinding.ActivityMainBinding
import com.example.androidwall.fragments.LogsFragment
import com.example.androidwall.fragments.RulesFragment
import com.example.androidwall.utils.Logger

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.context = applicationContext

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navController = (supportFragmentManager.findFragmentById(R.id.nav_frag) as NavHostFragment).navController

        binding.bottomNavigationView.setupWithNavController(navController)

        val config = AppBarConfiguration(setOf(
            R.id.fragment_home,
            R.id.fragment_logs
        ))

        setupActionBarWithNavController(navController, config)
    }
}