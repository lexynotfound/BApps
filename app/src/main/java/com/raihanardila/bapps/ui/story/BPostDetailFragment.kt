package com.raihanardila.bapps.ui.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.raihanardila.bapps.core.data.model.StoriesBModel
import com.raihanardila.bapps.databinding.FragmentBPostDetailBinding
import com.raihanardila.bapps.utils.DateUtils
import kotlin.math.abs

class BPostDetailFragment : Fragment() {

    private var _binding: FragmentBPostDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collapsingToolbarLayout = binding.collapsingToolbarLayout
        appBarLayout = binding.appBarLayout

        val story = getStoryFromBundle()
        populateData(story)

        setupCollapsingToolbar()
        setupBackNavigation()
    }

    private fun getStoryFromBundle(): StoriesBModel {
        val name = arguments?.getString("name").orEmpty()
        val id = arguments?.getString("id").orEmpty()
        val description = arguments?.getString("description").orEmpty()
        val photoUrl = arguments?.getString("photoUrl").orEmpty()
        val createdAt = arguments?.getString("createdAt").orEmpty()
        val lat = arguments?.getDouble("lat") ?: 0.0
        val lon = arguments?.getDouble("lon") ?: 0.0

        return StoriesBModel(
            name = name,
            id = id,
            description = description,
            photoUrl = photoUrl,
            createdAt = createdAt,
            lat = lat,
            lon = lon
        )
    }

    private fun populateData(story: StoriesBModel) {
        binding.name.text = story.name

        val createdAtTime = DateUtils.parseIso8601(story.createdAt)
        val currentTime = System.currentTimeMillis()
        val timeDifference = DateUtils.formatTimeDifference(createdAtTime, currentTime)
        binding.date.text = timeDifference

        binding.postContent.text = story.description

        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ImageB)

        if (story.lat != 0.0 && story.lon != 0.0) {
            val location = "Lat: ${story.lat}, Lon: ${story.lon}"
            binding.location.text = location
            binding.location.visibility = View.VISIBLE
        }
    }

    private fun setupCollapsingToolbar() {
        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                // Collapsed
                collapsingToolbarLayout.title = binding.name.text.toString()
                binding.tvTitle.visibility = View.GONE
            } else if (verticalOffset == 0) {
                // Expanded
                collapsingToolbarLayout.title = ""
                binding.tvTitle.visibility = View.VISIBLE
            } else {
                // Somewhere in between
                collapsingToolbarLayout.title = ""
                binding.tvTitle.visibility = View.GONE
            }
        }
    }

    private fun setupBackNavigation() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
