package com.raihanardila.bapps.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.raihanardila.bapps.ui.customeview.download.DownloadBottomSheetFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BMainViewModel by viewModel()
    private lateinit var authPreferences: AuthPreferences
    lateinit var bFeedAdapter: BFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authPreferences = AuthPreferences(requireContext())
        checkAuthentication()

        setupAdapter()
        setupSwipeToRefresh()
        observeViewModel() // Ensure this is called after setting up the adapter and other initializations

        // Handle back navigation to exit the app
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity() // Exit the app
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkAuthentication() {
        if (authPreferences.getToken().isNullOrEmpty()) {
            navigateToLogin()
        }
    }

    private fun setupAdapter() {
        bFeedAdapter = BFeedAdapter(
            onUserClick = { story -> navigateToUserDetail(story) },
            onStoryClick = { story -> navigateToStoryDetail(story) },
            onPhotoClick = { photoUrl -> navigateToPhotoDetail(photoUrl) },
            showBottomSheet = { photoUrl -> showDownloadBottomSheet(photoUrl) },
            onMapsClick = { story -> navigateToMapsDetail(story) }
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

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.storiesFlow.collectLatest { pagingData ->
                bFeedAdapter.submitData(pagingData)
            }
        }
        lifecycleScope.launch {
            bFeedAdapter.loadStateFlow.collectLatest { loadState ->
                if (_binding != null) { // Ensure binding is not null
                    binding.swipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading
                    binding.progressBar.visibility =
                        if (loadState.refresh is LoadState.Loading) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun navigateToUserDetail(story: StoriesBModel) {
        // Implement navigation to user detail
    }

    private fun navigateToStoryDetail(story: StoriesBModel) {
        val bundle = Bundle().apply {
            putString("name", story.name)
            putString("id", story.id)
            putString("description", story.description)
            putString("photoUrl", story.photoUrl)
            putString("createdAt", story.createdAt)
            putDouble("lat", story.lat)
            putDouble("lon", story.lon)
        }
        findNavController().navigate(R.id.action_homeFragment_to_detailBPostFragment, bundle)
    }

    private fun navigateToPhotoDetail(photoUrl: String) {
        val bundle = Bundle().apply {
            putString("photoUrl", photoUrl)
        }
        findNavController().navigate(R.id.action_homeFragment_to_imageDetailFragment, bundle)
    }

    private fun navigateToMapsDetail(story: StoriesBModel) {
        val bundle = Bundle().apply {
            putString("name", story.name)
            putString("id", story.id)
            putString("description", story.description)
            putString("photoUrl", story.photoUrl)
            putString("createdAt", story.createdAt)
            putDouble("lat", story.lat)
            putDouble("lon", story.lon)
        }
        findNavController().navigate(R.id.action_homeFragment_to_mapsFragment, bundle)
    }

    private fun showDownloadBottomSheet(photoUrl: String) {
        val downloadBottomSheet = DownloadBottomSheetFragment()
        downloadBottomSheet.setPhotoUrl(photoUrl)
        downloadBottomSheet.show(childFragmentManager, downloadBottomSheet.tag)
    }

    fun scrollToTop() {
        binding.rvBFeed.smoothScrollToPosition(0)
    }

    fun isScrolledToTop(): Boolean {
        val layoutManager = binding.rvBFeed.layoutManager as LinearLayoutManager
        return layoutManager.findFirstVisibleItemPosition() == 0 && layoutManager.findViewByPosition(
            0
        )?.top == 0
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }
}
