package com.raihanardila.bapps.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.raihanardila.bapps.R
import com.raihanardila.bapps.core.data.adapter.BFeedAdapter
import com.raihanardila.bapps.core.data.local.prefrences.AuthPreferences
import com.raihanardila.bapps.core.data.model.StoriesBModel
import com.raihanardila.bapps.core.data.viewmodel.BMainViewModel
import com.raihanardila.bapps.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: BMainViewModel by viewModel()
    private lateinit var authPreferences: AuthPreferences
    lateinit var bFeedAdapter: BFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        authPreferences = AuthPreferences(requireContext())

        if (authPreferences.getToken().isNullOrEmpty()) {
            navigateToLogin()
        }

        setupAdapter()
        setupSwipeToRefresh()

        lifecycleScope.launch {
            viewModel.storiesFlow.collectLatest { pagingData ->
                bFeedAdapter.submitData(pagingData)
            }
        }

        bFeedAdapter.addLoadStateListener { loadState ->
            binding.swipeRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
            binding.progressBar.visibility = if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE
        }

        return view
    }

    private fun setupAdapter() {
        bFeedAdapter = BFeedAdapter(
            onUserClick = { story -> navigateToUserDetail(story) },
            onStoryClick = { story -> navigateToStoryDetail(story) },
            onPhotoClick = { photoUrl -> navigateToPhotoDetail(photoUrl) }
        )

        binding.rvBFeed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bFeedAdapter
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            bFeedAdapter.refresh()
        }
    }

    fun scrollToTop() {
        binding.rvBFeed.smoothScrollToPosition(0)
    }

    fun isScrolledToTop(): Boolean {
        val layoutManager = binding.rvBFeed.layoutManager as LinearLayoutManager
        return layoutManager.findFirstVisibleItemPosition() == 0 && layoutManager.findViewByPosition(0)?.top == 0
    }

    private fun navigateToUserDetail(story: StoriesBModel) {
        // Implement navigation to user detail
    }

    private fun navigateToStoryDetail(story: StoriesBModel) {
        // Implement navigation to story detail
    }

    private fun navigateToPhotoDetail(photoUrl: String) {
        // Implement navigation to photo detail
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }
}
