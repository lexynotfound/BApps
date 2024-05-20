package com.raihanardila.bapps.core.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raihanardila.bapps.core.data.model.StoriesBModel
import com.raihanardila.bapps.databinding.ListBBinding
import com.raihanardila.bapps.utils.DateUtils

class BFeedAdapter(
    private val onUserClick: (StoriesBModel) -> Unit,
    private val onStoryClick: (StoriesBModel) -> Unit,
    private val onPhotoClick: (String) -> Unit,
    private val showBottomSheet: (String) -> Unit
) : PagingDataAdapter<StoriesBModel, BFeedAdapter.BFeedViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BFeedViewHolder {
        val binding = ListBBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BFeedViewHolder(binding, onUserClick, onStoryClick, onPhotoClick, showBottomSheet)
    }

    override fun onBindViewHolder(holder: BFeedViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    class BFeedViewHolder(
        private val binding: ListBBinding,
        private val onUserClick: (StoriesBModel) -> Unit,
        private val onStoryClick: (StoriesBModel) -> Unit,
        private val onPhotoClick: (String) -> Unit,
        private val showBottomSheet: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: StoriesBModel) {
            binding.apply {
                name.text = story.name
                val currentTime = System.currentTimeMillis()
                val timeDiffer = DateUtils.formatTimeDifference(DateUtils.parseIso8601(story.createdAt), currentTime)
                date.text = timeDiffer
                postContent.text = story.description

                Glide.with(binding.ImageB.context).load(story.photoUrl).into(binding.ImageB)

                root.setOnClickListener { onStoryClick(story) }
                profile.setOnClickListener { onUserClick(story) }
                ImageB.setOnClickListener { onPhotoClick(story.photoUrl) }

                ImageB.setOnLongClickListener {
                    showBottomSheet(story.photoUrl)
                    true
                }

                cardView.setOnLongClickListener {
                    showBottomSheet(story.photoUrl)
                    true
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoriesBModel>() {
            override fun areItemsTheSame(oldItem: StoriesBModel, newItem: StoriesBModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoriesBModel, newItem: StoriesBModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
