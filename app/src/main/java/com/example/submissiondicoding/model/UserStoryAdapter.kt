package com.example.submissiondicoding.model
import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissiondicoding.Detail
import com.example.submissiondicoding.R
import com.example.submissiondicoding.api.response.StoryItem
import com.example.submissiondicoding.databinding.StoryItemBinding


class UserStoryAdapter :
    PagingDataAdapter<StoryItem, UserStoryAdapter.UserStoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryItem> =
            object : DiffUtil.ItemCallback<StoryItem>() {
                override fun areItemsTheSame(oldUser: StoryItem, newUser: StoryItem): Boolean {
                    return oldUser.id == newUser.id
                }
                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldUser: StoryItem, newUser: StoryItem): Boolean {
                    return oldUser == newUser
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserStoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StoryItemBinding.inflate(inflater, parent, false)
        return UserStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserStoryViewHolder, position: Int) {
        val userStory = getItem(position)
        if (userStory != null) {
            holder.bind(userStory)
        } else {
            holder.showPlaceholder()
        }
        // to detail
        holder.binding.cardView.setOnClickListener {
            val intent = Intent(it.context, Detail::class.java)
            intent.putExtra(Detail.EXTRA_ID, userStory?.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    class UserStoryViewHolder(val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userStory: StoryItem) {
            binding.itemName.text = userStory.name
            Glide.with(itemView.context)
                .load(userStory.photoUrl)
                .error(R.drawable.baseline_broken_image_24)
                .centerCrop()
                .into(binding.itemPhoto)
        }
        fun showPlaceholder() {
            binding.itemName.text = "An Error Occurred"
            Glide.with(itemView.context)
                .load(R.drawable.baseline_camera_alt_24)
                .centerCrop()
                .into(binding.itemPhoto)
        }
    }
}