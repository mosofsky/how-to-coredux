package com.howto.coredux

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.howto.coredux.HowToReduxAction.Initialize_Start
import java.net.URL

class MainActivity : AppCompatActivity() {
    val howToViewModel by viewModels<HowToViewModel>()

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
            Initialize_Start(
                listOf(
                    HowToVideo(
                        "How To Properly Wash the Dishes",
                        "https://www.youtube.com/embed/fRS3v2DId4w"
                    ),
                    HowToVideo(
                        "How To Make Bubble Tea",
                        "https://www.youtube.com/embed/3sy8-9f-198"
                    ),
                    HowToVideo(
                        "How To Make Salsa",
                        "https://www.youtube.com/embed/rozhHGDEUw0"
                    )
                )
            )
        )
    }

    private fun initViewIds() {
        videoListRecyclerView = findViewById(R.id.videoListRecyclerView)
    }

    private fun initUIObservers() {
        howToViewModel.isInitializeInProgress.observe(this, Observer {
            if (it) {
                initRecyclerView()
                howToViewModel.dispatchAction(HowToReduxAction.Initialize_Finish)
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