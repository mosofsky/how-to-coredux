package com.howto.coredux

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.howto.coredux.HowToReduxAction.Initialize
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val howToViewModel by viewModels<HowToViewModel>()

    private lateinit var videoListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRedux()

        initViewIds()

        initUIObservers()
    }

    private fun initRedux() {
        howToViewModel.dispatchAction(
            Initialize(
                listOf(
                    HowToVideo(
                        "How To Properly Wash the Dishes",
                        URL("https://www.youtube.com/watch?v=fRS3v2DId4w")
                    ),
                    HowToVideo(
                        "How To Make Bubble Tea",
                        URL("https://www.youtube.com/watch?v=3sy8-9f-198")
                    ),
                    HowToVideo(
                        "How To Make Salsa",
                        URL("https://www.youtube.com/watch?v=rozhHGDEUw0")
                    )
                )
            )
        )
    }

    private fun initViewIds() {
        videoListRecyclerView = findViewById(R.id.videoListRecyclerView)
    }

    private fun initUIObservers() {
        howToViewModel.isInitializationInProgress.observe(this, Observer {
            if (it) {
                initRecyclerView()
                howToViewModel.dispatchAction(HowToReduxAction.Initialized)
            }
        })
    }

    private fun state(): HowToReduxState {
        return howToViewModel.state.value!!
    }

    private fun initRecyclerView() {
        videoListRecyclerView.layoutManager = LinearLayoutManager(this)
        videoListRecyclerView.adapter = HowToVideoListAdapter(this, state().howToVideos!!)
    }
}