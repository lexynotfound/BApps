package com.raihanardila.bapps.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.raihanardila.bapps.R
import com.raihanardila.bapps.core.data.local.prefrences.AuthPreferences

class SettingsFragment : Fragment() {

    private lateinit var authPreferences: AuthPreferences
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authPreferences = AuthPreferences(requireContext())
        progressBar = view.findViewById(R.id.progressBar)

        val ivLogout = view.findViewById<ImageView>(R.id.icon_logout)
        val tvLogout = view.findViewById<TextView>(R.id.text_logout)

        val logoutListener = View.OnClickListener {
            progressBar.visibility = View.VISIBLE
            authPreferences.clearToken()
            progressBar.visibility = View.GONE
            findNavController().navigate(
                R.id.action_settingsFragment_to_loginFragment, null,
                NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
            )
        }

        ivLogout.setOnClickListener(logoutListener)
        tvLogout.setOnClickListener(logoutListener)

        // Handle back navigation
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // If the user is not authenticated, block the back navigation
                    if (authPreferences.getToken().isNullOrEmpty()) {
                        findNavController().navigate(R.id.loginFragment)
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        // Check if the user is authenticated
        if (authPreferences.getToken().isNullOrEmpty()) {
            // Navigate to the login screen if not authenticated
            findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
        }
    }
}
