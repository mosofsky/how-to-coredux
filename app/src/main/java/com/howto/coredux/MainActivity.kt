package com.howto.coredux

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.howto.coredux.HowToReduxAction.*

class MainActivity : AppCompatActivity() {
    val howToViewModel by viewModels<HowToViewModel>()

    private lateinit var rootViewGroup: ViewGroup
    private lateinit var videoListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRedux()

        initViewIds()

        initUIObservers()

        initEventListeners()
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
        rootViewGroup = findViewById(R.id.rootViewGroup)
        videoListRecyclerView = findViewById(R.id.videoListRecyclerView)
    }

    private fun initUIObservers() {
        howToViewModel.isInitializeInProgress.observe(this, Observer {
            if (it) {
                initRecyclerView()
                howToViewModel.dispatchAction(Initialize_Finish)
            }
        })
    }

    private fun initEventListeners() {
        rootViewGroup.setOnClickListener {
            howToViewModel.dispatchAction(HideVideoFragment_Start)
        }
    }

    private fun state(): HowToReduxState {
        return howToViewModel.state.value!!
    }

    private fun initRecyclerView() {
        videoListRecyclerView.layoutManager = LinearLayoutManager(this)
        videoListRecyclerView.adapter = HowToVideoListAdapter(this, state().howToVideos!!)
    }
}