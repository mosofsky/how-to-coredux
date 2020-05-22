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
                    ),
                    HowToVideo(
                        "How To Restore a Rusty Knife",
                        "https://www.youtube.com/embed/9Kt5vlhV7Q0"
                    ),
                    HowToVideo(
                        "How To Apply a Screen Protector Perfectly",
                        "https://www.youtube.com/embed/cGeMs2PyGPo"
                    ),
                    HowToVideo(
                        "How To Make McDonald's Chicken McNuggets",
                        "https://www.youtube.com/embed/fBymZ4X_Gps"
                    ),
                    HowToVideo(
                        "How To Make Pulled Pork",
                        "https://www.youtube.com/embed/-2nnIx6LRPw"
                    ),
                    HowToVideo(
                        "How To Properly Wash Your Hands",
                        "https://www.youtube.com/embed/ngpWXg5PnH8"
                    ),
                    HowToVideo(
                        "How To Make the Perfect Pizza",
                        "https://www.youtube.com/embed/OisvDHvmKuM"
                    ),
                    HowToVideo(
                        "How To Cook BBQ Ribs",
                        "https://www.youtube.com/embed/bialmhIFMLA"
                    ),
                    HowToVideo(
                        "How To Make an Oreo Cheesecake",
                        "https://www.youtube.com/embed/AlE5ornrwGc"
                    ),
                    HowToVideo(
                        "How To Correctly Pack a Suitcase",
                        "https://www.youtube.com/embed/rApkSx8DEwI"
                    ),
                    HowToVideo(
                        "How To Make Spring Rolls",
                        "https://www.youtube.com/embed/wRaEVSeYo1s"
                    ),
                    HowToVideo(
                        "How To Edit a Video",
                        "https://www.youtube.com/embed/vvCkmcoEolQ"
                    ),
                    HowToVideo(
                        "How To Make Chicken Teriyaki",
                        "https://www.youtube.com/embed/pMfV9g0P_kM"
                    ),
                    HowToVideo(
                        "How To Make Crème Brûlée",
                        "https://www.youtube.com/embed/eZS6zqHD5eU"
                    ),
                    HowToVideo(
                        "How To Make a Century Egg",
                        "https://www.youtube.com/embed/kIup5IorCrQ"
                    ),
                    HowToVideo(
                        "How To Properly Decorate a Christmas Tree",
                        "https://www.youtube.com/embed/ZSVE2bv6u94"
                    ),
                    HowToVideo(
                        "How To Make an ASMR Video",
                        "https://www.youtube.com/embed/w6bUbguIvNo"
                    ),
                    HowToVideo(
                        "How To Make a Hair Lasagna",
                        "https://www.youtube.com/embed/EN_HzPZVfNE"
                    ),
                    HowToVideo(
                        "How To Plant a Tree",
                        "https://www.youtube.com/embed/MqH8WtR5hWs"
                    ),
                    HowToVideo(
                        "How To Make Gnocchi",
                        "https://www.youtube.com/embed/nHxNO7tXk-A"
                    ),
                    HowToVideo(
                        "How To Make a Giant Pizza",
                        "https://www.youtube.com/embed/mIs3zMOYbCk"
                    ),
                    HowToVideo(
                        "How To Make Almond Milk",
                        "https://www.youtube.com/embed/8xAgsOaW1JM"
                    ),
                    HowToVideo(
                        "How To Make a Bath Bomb",
                        "https://www.youtube.com/embed/Qt_UHDeeW_g"
                    ),
                    HowToVideo(
                        "How To Make Homemade Pasta",
                        "https://www.youtube.com/embed/StMQKihKoP0"
                    ),
                    HowToVideo(
                        "How To Make the Perfect Burger",
                        "https://www.youtube.com/embed/L6yX6Oxy_J8"
                    ),
                    HowToVideo(
                        "How To Ride a Motorcycle",
                        "https://www.youtube.com/embed/UhtemZSUNqU"
                    ),
                    HowToVideo(
                        "How To Repair a Chipped or Cracked Windshield",
                        "https://www.youtube.com/embed/d1tUmnmv5pc"
                    ),
                    HowToVideo(
                        "How To Perfectly Cook Roast Chicken",
                        "https://www.youtube.com/embed/IBlNuZzwG4o"
                    ),
                    HowToVideo(
                        "How To Make a Vegan Pizza",
                        "https://www.youtube.com/embed/1qGyPdoeveQ"
                    ),
                    HowToVideo(
                        "How To Make a Wedding Cake",
                        "https://www.youtube.com/embed/6CWORWQf-BU"
                    ),
                    HowToVideo(
                        "How To Make The Perfect Roast",
                        "https://www.youtube.com/embed/q4OItmKWFKw"
                    ),
                    HowToVideo(
                        "How To Make a Chicken Kiev",
                        "https://www.youtube.com/embed/3_JsSvHezAU"
                    ),
                    HowToVideo(
                        "How To Make Vegan Ice cream",
                        "https://www.youtube.com/embed/guNk3Pr_Iws"
                    ),
                    HowToVideo(
                        "How To Make Eggs Benedict",
                        "https://www.youtube.com/embed/Oryq_TdAyNI"
                    ),
                    HowToVideo(
                        "How To Make Homemade Eggnog",
                        "https://www.youtube.com/embed/P_s1ZXG_4VA"
                    ),
                    HowToVideo(
                        "How To Make a Giant Candy Cane",
                        "https://www.youtube.com/embed/G0vjsLhey2Q"
                    ),
                    HowToVideo(
                        "How To Make Banana Bread",
                        "https://www.youtube.com/embed/WlreNuiJ5KE"
                    ),
                    HowToVideo(
                        "How To Make Crab Curry",
                        "https://www.youtube.com/embed/hfaUAnI8RAs"
                    ),
                    HowToVideo(
                        "How To Install Red Dead Redemption 2 On PC",
                        "https://www.youtube.com/embed/XyduVYH3Zrw"
                    ),
                    HowToVideo(
                        "How To Build a Gaming PC",
                        "https://www.youtube.com/embed/UCs9X3BwdCE"
                    ),
                    HowToVideo(
                        "How To Make a Vegan Lasagna",
                        "https://www.youtube.com/embed/_xrgvJ7LNhk"
                    ),
                    HowToVideo(
                        "How To Make a Bulletproof iPhone Case",
                        "https://www.youtube.com/embed/ZN0BjN3wGEU"
                    ),
                    HowToVideo(
                        "How To Make a Chocolate Mud Cake",
                        "https://www.youtube.com/embed/MbsLn9CI24o"
                    ),
                    HowToVideo(
                        "How To Make Ramen",
                        "https://www.youtube.com/embed/B8y3SSmz4sg"
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