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

        // 여기 이미지 형식은 파이어베이스에 맞춰서 바까줘야함
//        val imgUri = it.toUri().buildUpon().scheme("https").build()
//
//        Glide.with(image.context)
//            .load(imgUri)
//            .apply(
//                RequestOptions()
//                .placeholder(R.drawable.feed_animation_loading)
//                .error(R.drawable.myenv_image_empty))
//            .into(image)

        Glide.with(image.context).load(imageUrl).into(image)
        Timber.i(imageUrl)
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Feed>?) {
    val adapter = recyclerView.adapter as FeedGridAdapter
    adapter.submitList(data)
}