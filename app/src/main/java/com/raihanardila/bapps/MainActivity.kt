package com.raihanardila.bapps

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.raihanardila.bapps.databinding.ActivityMainBinding
import com.raihanardila.bapps.ui.home.HomeFragment
import com.raihanardila.bapps.ui.liked.LikedFragment
import com.raihanardila.bapps.ui.profile.ProfileFragment
import com.raihanardila.bapps.ui.search.SearchFragment
import com.raihanardila.bapps.ui.story.BPostFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frame_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    hideActionBar()
                    loadFragment(HomeFragment())
                    true
                }

                R.id.search -> {
                    hideActionBar()
                    loadFragment(SearchFragment())
                    true
                }

                R.id.bpost -> {
                    hideActionBar()
                    loadFragment(BPostFragment())
                    true
                }

                R.id.fav -> {
                    hideActionBar()
                    loadFragment(LikedFragment())
                    true
                }

                R.id.profile -> {
                    hideActionBar()
                    loadFragment(ProfileFragment())
                    true
                }

                else -> {
                    false
                }

            }
        }
        loadFragment(HomeFragment())
    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }

    private fun showActionBar() {
        supportActionBar?.show()
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
    }
}