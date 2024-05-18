package com.raihanardila.bapps.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.raihanardila.bapps.MainActivity
import com.raihanardila.bapps.R
import com.raihanardila.bapps.core.data.local.prefrences.AuthPreferences
import com.raihanardila.bapps.core.data.viewmodel.AuthViewModel
import com.raihanardila.bapps.ui.customeview.EmailCustomeView
import com.raihanardila.bapps.ui.customeview.PasswordCustomeView
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModel()

    private lateinit var emailCustomView: EmailCustomeView
    private lateinit var passwordCustomView: PasswordCustomeView
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        authPreferences = AuthPreferences(this)

        emailCustomView = findViewById(R.id.email_custom_view)
        passwordCustomView = findViewById(R.id.password_custom_view)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.create_account_button)
        progressBar = findViewById(R.id.progress_bar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loginButton.setOnClickListener {
            val email = emailCustomView.findViewById<TextInputEditText>(R.id.username_input).text.toString()
            val password = passwordCustomView.findViewById<TextInputEditText>(R.id.password_input).text.toString()
            authViewModel.loginUser(email, password)
        }

        authViewModel.loginSuccess.observe(this, Observer { success ->
            if (success) {
                val token = authViewModel.authToken.value
                if (token != null) {
                    Log.d("Token", "Token save: $token")
                    saveAuthToken(token)
                    navigateToHome()
                }
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            } else {
                val token = authViewModel.authToken.value
                Log.e("Token", "Failed Token save: $token")
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        })

        authViewModel.isLoading.observe(this, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        registerButton.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun saveAuthToken(token: String) {
        authPreferences.saveToken(token)
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
