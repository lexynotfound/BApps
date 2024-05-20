package com.raihanardila.bapps.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.raihanardila.bapps.R
import com.raihanardila.bapps.core.data.adapter.ProfilePagerAdapter
import com.raihanardila.bapps.core.data.local.prefrences.AuthPreferences

class ProfileFragment : Fragment() {

    private lateinit var authPreferences: AuthPreferences
    private lateinit var profilePagerAdapter: ProfilePagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authPreferences = AuthPreferences(requireContext())
        val token = authPreferences.getToken()
        if (token.isNullOrEmpty()) {
            // Handle no token case
            return
        }

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabsLayout)

        profilePagerAdapter = ProfilePagerAdapter(requireContext(), token)
        viewPager.adapter = profilePagerAdapter
        TabLayoutMediator(tabLayout, viewPager, profilePagerAdapter).attach()

        // Display logged-in user's name
        val userName = "User Name" // Replace with actual logic to fetch the user name
        view.findViewById<TextView>(R.id.usernameTextView).text = userName

        // Handle settings icon click to navigate to SettingsFragment
        view.findViewById<ImageView>(R.id.imageView).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
    }
}
