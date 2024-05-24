package com.raihanardila.bapps.ui.images

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.raihanardila.bapps.R
import com.raihanardila.bapps.databinding.FragmentImageDetailBinding

class ImageDetailFragment : Fragment() {
    private var _binding: FragmentImageDetailBinding? = null
    private val binding get() = _binding!!

    private var isSystemUiVisible = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val photoUrl = arguments?.getString("photoUrl")
            photoUrl?.let {
                Glide.with(this@ImageDetailFragment)
                    .load(it)
                    .into(imgMain)
            }

            imgMain.setOnClickListener {
                toggleSystemUiVisibility()
            }

            btnClose.setOnClickListener {
                findNavController().navigate(R.id.action_imageDetailFragment_to_homeFragment)
            }
        }
    }

    private fun toggleSystemUiVisibility() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(requireView())
        windowInsetsController?.let {
            if (isSystemUiVisible) {
                it.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                it.show(WindowInsetsCompat.Type.systemBars())
            }
            isSystemUiVisible = !isSystemUiVisible
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
