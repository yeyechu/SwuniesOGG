package com.swu.dimiz.ogg.ui.myact.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.ItemImageBinding

class PagerImageAdapter
    : RecyclerView.Adapter<PagerImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(private var binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.imageCardnews.setImageResource(images[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        holder.bind(position)
    }

    override fun getItemCount(): Int = images.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private val images = listOf(
        R.drawable.myact_image_cardnew_1,
        R.drawable.myact_image_cardnew_2,
        R.drawable.myact_image_cardnew_3,
        R.drawable.myact_image_cardnew_4
    )
}