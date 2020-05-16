package com.howto.coredux

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HowToVideoListAdapter(val mainActivity: MainActivity, private val howToVideos: List<HowToVideo>) : RecyclerView.Adapter<HowToVideoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HowToVideoViewHolder {
        return HowToVideoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.video_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return howToVideos.size
    }

    override fun onBindViewHolder(howToVideoViewHolder: HowToVideoViewHolder, position: Int) {
        val howToVideo = howToVideos[position]
        howToVideoViewHolder.videoNameTextView.text = howToVideo.name
    }
}

class HowToVideoViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val videoNameTextView: TextView = view.findViewById(R.id.videoNameTextView)
}