package com.swu.dimiz.ogg.ui.feed


//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.swu.dimiz.ogg.R
//import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed

//
//class FeedAdapter(var context: Context, var feedList:ArrayList<Feed>) : RecyclerView.Adapter<FeedAdapter.ViewHolder>(){
////
////    var onItemClickListener: OnItemClickListener?=null
////    //
////    //글라이더 이미지 필요
////    //
////    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
////        private var feedIv: ImageView =itemView.findViewById(R.id.feed_iv)
////
////        fun bind(feed:Feed){
////            Glide.with(context).load(feed.imageUrl).into(feedIv)
////            feedIv.setOnClickListener {
////                if(onItemClickListener!=null)
////                    onItemClickListener?.onItemClick(feed)
////            }
////        }
////    }
////
////    interface OnItemClickListener{
////        fun onItemClick(feed:Feed)
////    }
////
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
////        var view= LayoutInflater.from(context).inflate(R.layout.item_feed,parent,false)
////        return ViewHolder(view)
////    }
////
////    override fun getItemCount(): Int {
////        return feedList.size
////    }
////
////    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
////        var feed=feedList[position]
////        holder.bind(feed)
////    }
//}
