package com.swu.dimiz.ogg.ui.feed.myfeed

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import timber.log.Timber

@BindingAdapter("imageUrl")
fun bindImage(image: ImageView, imageUrl: String?) {
    imageUrl?.let {

        Glide.with(image.context)
            .load(imageUrl)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.feed_animation_loading)
                .error(R.drawable.myenv_image_empty))
            .into(image)
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Feed>?) {
    val adapter = recyclerView.adapter as FeedGridAdapter
    adapter.submitList(data)
}