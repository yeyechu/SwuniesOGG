package com.swu.dimiz.ogg.ui.env.badges.badgeadapters

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.ui.env.myenv.InventoryAdapter

@BindingAdapter("badgeTitle")
fun TextView.setTitle(item: Badges?) {
    item?.let{
        text = item.title
    }
}

@BindingAdapter("badgeDetailImage")
fun detailImageSet(imageView: ImageView, data: Badges?) {
    data?.let {
        when(data.getDate) {
            null -> imageView.setImageBitmap(data.image.apply {
                imageView.setColorFilter(Color.parseColor("#F56A6A6A"))
                imageView.setBackgroundResource(R.drawable.badgelist_shape_badge_background_sealed)
            })
            else -> imageView.setImageBitmap(data.image)
        }
    }
}

@BindingAdapter("badgeImage")
fun ImageView.setImage(item: Badges?) {
    item?.let {
        when(item.getDate) {
            null -> setImageBitmap(item.image.apply {
                setColorFilter(Color.parseColor("#F56A6A6A"))
                setBackgroundResource(R.drawable.badgelist_shape_badge_background_sealed)
            })
            else -> setImageBitmap(item.image)
        }
    }
}

@BindingAdapter("badgeId", "getDate")
fun ImageView.getBadge(id: Int?, bool: Boolean) {
    id?.let {
        if(bool) {
            when(id) {
                40001 -> setImageResource(R.drawable.badge_40001)
                40002 -> setImageResource(R.drawable.badge_40002)
                40003 -> setImageResource(R.drawable.badge_40003)
                40004 -> setImageResource(R.drawable.badge_40004)
                40005 -> setImageResource(R.drawable.badge_40005)
                40006 -> setImageResource(R.drawable.badge_40006)
                40007 -> setImageResource(R.drawable.badge_40007)
                40008 -> setImageResource(R.drawable.badge_40008)
                40009 -> setImageResource(R.drawable.badge_40009)
                40010 -> setImageResource(R.drawable.badge_40010)
                40011 -> setImageResource(R.drawable.badge_40011)
                40012 -> setImageResource(R.drawable.badge_40012)
                40013 -> setImageResource(R.drawable.badge_40013)
                40014 -> setImageResource(R.drawable.badge_40014)
                40015 -> setImageResource(R.drawable.badge_40015)
                40016 -> setImageResource(R.drawable.badge_40016)
                40017 -> setImageResource(R.drawable.badge_40017)
                40018 -> setImageResource(R.drawable.badge_40018)
                40019 -> setImageResource(R.drawable.badge_40019)
                40020 -> setImageResource(R.drawable.badge_40020)
                40021 -> setImageResource(R.drawable.badge_40021)
                40022 -> setImageResource(R.drawable.badge_40022)
                40023 -> setImageResource(R.drawable.badge_40023)
                40024 -> setImageResource(R.drawable.badge_40024)
                40025 -> setImageResource(R.drawable.badge_40025)
                40026 -> setImageResource(R.drawable.badge_40026)
                40027 -> setImageResource(R.drawable.badge_40027)
                40028 -> setImageResource(R.drawable.badge_40028)
                40029 -> setImageResource(R.drawable.badge_40029)
                40030 -> setImageResource(R.drawable.badge_40030)
                40031 -> setImageResource(R.drawable.badge_40031)
            }
        } else {
            setImageResource(R.drawable.badge_40000)
        }
    }
}
//@BindingAdapter("badgeListData")
//fun bindRecyclerBadge(recyclerView: RecyclerView, data: List<Badges>?) {
//    val adapter = recyclerView.adapter as BadgeAdapter
//    adapter.submitList(data)
//}

//@BindingAdapter("badgeListData")
//fun bindRecyclerBadge(recyclerView: RecyclerView, data: List<Badges>?) {
//    val adapter = recyclerView.adapter as BadgeListAdapter
//    adapter.submitList(data)
//}

@BindingAdapter("badgeInventory")
fun bindRecyclerInventory(recyclerView: RecyclerView, data: List<Badges>?) {
    val adapter = recyclerView.adapter as InventoryAdapter
    adapter.submitList(data)
}
