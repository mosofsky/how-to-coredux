package com.howto.coredux

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.howto.coredux.HowToReduxAction.*

class PlayVideoFragment : Fragment() {
    private val howToViewModel by activityViewModels<HowToViewModel>()

    private lateinit var playVideoWebView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.play_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewIds()

        initUIObservers()

        configureWebView()
    }

    private fun initViewIds() {
        playVideoWebView = requireView().findViewById(R.id.playVideoWebView)
    }

    private fun configureWebView() {
        playVideoWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (null != url && url != ABOUT_URL) {
                    howToViewModel.dispatchAction(LoadVideo_Finish)
                }
            }
        }
        val ws: WebSettings = playVideoWebView.settings

        @SuppressLint("SetJavaScriptEnabled") // suppressed because this is just a demo app
        ws.javaScriptEnabled = true
    }

    private fun initUIObservers() {
        howToViewModel.isShowVideoFragmentInProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                requireView().visibility = View.VISIBLE
                howToViewModel.dispatchAction(LoadVideo_Start)
                howToViewModel.dispatchAction(ShowVideoFragment_Finish)
            }
        })

        howToViewModel.isHideVideoFragmentInProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                playVideoWebView.loadUrl(ABOUT_URL)
                requireView().visibility = View.GONE
                howToViewModel.dispatchAction(HideVideoFragment_Finish)
            }
        })

        howToViewModel.isLoadVideoInProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                maybeLoadVideoAsynchronously()
            }
        })
    }

    private fun maybeLoadVideoAsynchronously() {
        val howToVideoShown = howToViewModel.state.value!!.howToVideoShown!!

        val videoStr =
            "<html><body>${howToVideoShown.name}<br><iframe width=\"380\" height=\"300\" src=\"${howToVideoShown.url}\" frameborder=\"0\" allowfullscreen></iframe></body></html>"

        if (null == playVideoWebView.url || !playVideoWebView.url.endsWith(videoStr)) {
            playVideoWebView.loadData(videoStr, "text/html", "utf-8")
        } else {
            howToViewModel.dispatchAction(LoadVideo_Finish)
        }
    }
}