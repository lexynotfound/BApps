package com.raihanardila.bapps

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.raihanardila.bapps.core.data.local.prefrences.AuthPreferences
import com.raihanardila.bapps.databinding.ActivityMainBinding
import com.raihanardila.bapps.ui.home.HomeFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var authPreferences: AuthPreferences
    private var lastClickTime = 0L
    private val doubleClickTimeDelta = 300L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frame_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authPreferences = AuthPreferences(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frame_container) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        // Listener to manage visibility of BottomNavigationView
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.notificationFragment,
                R.id.profileFragment -> showBottomNav()

                else -> hideBottomNav()
            }
        }

        setupBottomNavClickListener()

        // Navigate to splash screen initially
        if (savedInstanceState == null) {
            navController.navigate(R.id.splashFragment)
        }
    }

    private fun setupBottomNavClickListener() {
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            if (navController.currentDestination?.id != menuItem.itemId) {
                handleBottomNavClick(menuItem.itemId)
            }
            true
        }

        binding.bottomNav.setOnItemReselectedListener { menuItem ->
            handleBottomNavClick(menuItem.itemId)
        }
    }

    private fun handleBottomNavClick(itemId: Int) {
        val currentTime = System.currentTimeMillis()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frame_container) as NavHostFragment
        val fragment = navHostFragment.childFragmentManager.fragments.firstOrNull()

        if (itemId == R.id.homeFragment) {
            if (fragment is HomeFragment) {
                if (currentTime - lastClickTime < doubleClickTimeDelta) {
                    fragment.scrollToTop()
                    fragment.viewLifecycleOwner.lifecycleScope.launch {
                        fragment.bFeedAdapter.refresh()
                    }
                } else {
                    if (fragment.isScrolledToTop()) {
                        fragment.viewLifecycleOwner.lifecycleScope.launch {
                            fragment.bFeedAdapter.refresh()
                        }
                    } else {
                        fragment.scrollToTop()
                    }
                }
            } else {
                navController.navigate(R.id.homeFragment)
            }
            lastClickTime = currentTime
        } else {
            // Ensure other navigation actions are not intercepted
            if (navController.currentDestination?.id != itemId) {
                navController.navigate(itemId)
            }
        }
    }

    private fun showBottomNav() {
        binding.bottomNav.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.bottomNav.visibility = View.GONE
    }

    override fun onBackPressed() {
        // Exit the application directly from HomeFragment or LoginFragment
        if (navController.currentDestination?.id == R.id.homeFragment ||
            navController.currentDestination?.id == R.id.loginFragment
        ) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }
}
