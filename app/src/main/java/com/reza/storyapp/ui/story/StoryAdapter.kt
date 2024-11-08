package com.reza.storyapp.ui.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.reza.storyapp.data.local.StoryEntity
import com.reza.storyapp.databinding.ItemStoryBinding
import com.reza.storyapp.ui.storyDetail.DetailActivity

class StoryAdapter : PagingDataAdapter<StoryEntity, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var imgPhoto: ImageView = binding.ivItemPhoto
        private var tvName: TextView = binding.tvItemName
        private var tvDescription: TextView = binding.tvItemDescription

        fun bind(story: StoryEntity?) {
            Glide.with(this.itemView.context)
                .load(story?.photoUrl)
                .into(this.binding.ivItemPhoto)
            binding.tvItemName.text = story?.name
            binding.tvItemDescription.text = story?.description

            itemView.setOnClickListener {
                val intentDetail = Intent(itemView.context, DetailActivity::class.java)
                intentDetail.putExtra(STORY_ID_KEY, story?.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(imgPhoto, "image"),
                        Pair(tvName, "name"),
                        Pair(tvDescription, "description")
                    )

                itemView.context.startActivity(intentDetail, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        const val STORY_ID_KEY = "story_id"

        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>() {
                override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}