package com.raihanardila.bapps.core.data.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.raihanardila.bapps.R
import com.raihanardila.bapps.ui.profile.UsersStoriesFragment

class ProfilePagerAdapter(
    private val context: Context,
    private val token: String
) : FragmentStateAdapter(context as FragmentActivity), TabLayoutMediator.TabConfigurationStrategy {

    private val TAB_TITLES = arrayOf(
        context.getString(R.string.tab_stories)
    )

    override fun getItemCount(): Int = TAB_TITLES.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UsersStoriesFragment.newInstance(token)
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = TAB_TITLES[position]
    }
}
