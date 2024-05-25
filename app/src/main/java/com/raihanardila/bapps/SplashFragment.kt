package com.raihanardila.bapps

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.raihanardila.bapps.core.data.local.prefrences.AuthPreferences
import com.raihanardila.bapps.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start the transition
        binding.main.transitionToEnd()

        // Check if the user is authenticated
        val authPreferences = AuthPreferences(requireContext())
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) { // Check if the fragment is still added to its activity
                if (authPreferences.getToken().isNullOrEmpty()) {
                    navigateToLogin()
                } else {
                    navigateToHome()
                }
            }
        }, 2000) // Adjust the delay as needed
    }

    private fun navigateToLogin() {
        findNavController().navigate(
            R.id.action_splashFragment_to_loginFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
        )
    }

    private fun navigateToHome() {
        findNavController().navigate(
            R.id.action_splashFragment_to_homeFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
