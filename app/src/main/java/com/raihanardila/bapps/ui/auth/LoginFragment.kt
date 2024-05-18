package com.raihanardila.bapps.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.raihanardila.bapps.R
import com.raihanardila.bapps.core.data.local.prefrences.AuthPreferences
import com.raihanardila.bapps.core.data.viewmodel.AuthViewModel
import com.raihanardila.bapps.databinding.FragmentLoginBinding
import com.raihanardila.bapps.ui.customeview.EmailCustomeView
import com.raihanardila.bapps.ui.customeview.PasswordCustomeView
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var emailCustomView: EmailCustomeView
    private lateinit var passwordCustomView: PasswordCustomeView
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        emailCustomView = view.findViewById(R.id.email_custom_view)
        passwordCustomView = view.findViewById(R.id.password_custom_view)
        loginButton = view.findViewById(R.id.login_button)
        registerButton = view.findViewById(R.id.create_account_button)
        progressBar = view.findViewById(R.id.progress_bar)

        loginButton.setOnClickListener {
            val email =
                emailCustomView.findViewById<TextInputEditText>(R.id.username_input).text.toString()
            val password =
                passwordCustomView.findViewById<TextInputEditText>(R.id.password_input).text.toString()
            authViewModel.loginUser(email, password)
        }

        authViewModel.loginSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                val token = authViewModel.authToken.value
                if (token != null) {
                    Log.d("Authenthication", "Token: $token")
                    val authPreferences = AuthPreferences(requireContext())
                    authPreferences.saveToken(token)
                    navigateToHome()
                }
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        })


        authViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        registerButton.setOnClickListener {
            navigateToRegister()
        }

        return view
    }

//    private fun saveAuthToken(token: String) {
//        authPreferences.saveToken(token)
//    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }
}
