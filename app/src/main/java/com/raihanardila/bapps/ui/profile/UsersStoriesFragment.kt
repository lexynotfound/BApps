package com.raihanardila.bapps.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raihanardila.bapps.R
import com.raihanardila.bapps.core.data.adapter.BFeedAdapter
import com.raihanardila.bapps.core.data.adapter.UserStoriesPagingSource
import com.raihanardila.bapps.core.data.remote.client.ApiService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class UsersStoriesFragment : Fragment() {

    private lateinit var bFeedAdapter: BFeedAdapter
    private var token: String? = null
    private val apiService: ApiService by inject()

    companion object {
        private const val ARG_TOKEN = "token"

        fun newInstance(token: String): UsersStoriesFragment {
            val fragment = UsersStoriesFragment()
            val bundle = Bundle().apply {
                putString(ARG_TOKEN, token)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = arguments?.getString(ARG_TOKEN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_users_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_user_stories)
        recyclerView.layoutManager = LinearLayoutManager(context)

        bFeedAdapter = BFeedAdapter(
            onUserClick = { story -> /* Handle user click */ },
            onStoryClick = { story -> /* Handle story click */ },
            onPhotoClick = { photoUrl -> /* Handle photo click */ },
            showBottomSheet = { photoUrl -> /* Handle show bottom sheet */ }
        )

        recyclerView.adapter = bFeedAdapter

        fetchUserStories()
    }

    private fun fetchUserStories() {
        lifecycleScope.launch {
            val pager = Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = { UserStoriesPagingSource(apiService) }
            ).flow.cachedIn(lifecycleScope)

            pager.collectLatest { pagingData ->
                bFeedAdapter.submitData(pagingData)
            }
        }
    }
}
