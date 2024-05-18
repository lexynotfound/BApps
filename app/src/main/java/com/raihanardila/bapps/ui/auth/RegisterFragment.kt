package com.raihanardila.bapps.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.raihanardila.bapps.R
import com.raihanardila.bapps.core.data.viewmodel.AuthViewModel
import com.raihanardila.bapps.databinding.FragmentRegisterBinding
import com.raihanardila.bapps.ui.customeview.EmailCustomeView
import com.raihanardila.bapps.ui.customeview.NameCustomeView
import com.raihanardila.bapps.ui.customeview.PasswordCustomeView
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var nameCustomView: NameCustomeView
    private lateinit var emailCustomView: EmailCustomeView
    private lateinit var passwordCustomView: PasswordCustomeView
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textLogin: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        nameCustomView = view.findViewById(R.id.name_custom_view)
        emailCustomView = view.findViewById(R.id.email_custom_view)
        passwordCustomView = view.findViewById(R.id.password_custom_view)
        registerButton = view.findViewById(R.id.register_button)
        progressBar = view.findViewById(R.id.progress_bar)
        textLogin = view.findViewById(R.id.login_text)

        registerButton.setOnClickListener {
            val name = nameCustomView.findViewById<TextInputEditText>(R.id.username_input).text.toString()
            val email = emailCustomView.findViewById<TextInputEditText>(R.id.username_input).text.toString()
            val password = passwordCustomView.findViewById<TextInputEditText>(R.id.password_input).text.toString()

            if (validateFields(name, email, password)) {
                authViewModel.registerUser(name, email, password)
            }
        }

        authViewModel.registerSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                navigateToLogin()
            }
        })

        authViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        textLogin.setOnClickListener {
            navigateToLogin()
        }

        return view
    }

    private fun validateFields(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            nameCustomView.findViewById<TextInputEditText>(R.id.username_input).error = getString(R.string.error_name_required)
            isValid = false
        }

        if (email.isEmpty()) {
            emailCustomView.findViewById<TextInputEditText>(R.id.username_input).error = getString(R.string.error_email_required)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailCustomView.findViewById<TextInputEditText>(R.id.username_input).error = getString(R.string.invalid_email_error)
            isValid = false
        }

        if (password.isEmpty()) {
            passwordCustomView.findViewById<TextInputEditText>(R.id.password_input).error = getString(R.string.error_password_required)
            isValid = false
        }

        return isValid
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }
}
