package com.howto.coredux

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freeletics.coredux.*
import com.freeletics.coredux.log.android.AndroidLogSink
import com.howto.coredux.HowToReduxAction.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlin.coroutines.CoroutineContext

class HowToViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private val mutableState = MutableLiveData<HowToReduxState>()

    val state: LiveData<HowToReduxState> = mutableState

    val isInitializeInProgress = MutableLiveData<Boolean>()
    val isShowVideoFragmentInProgress = MutableLiveData<Boolean>()
    val isHideVideoFragmentInProgress = MutableLiveData<Boolean>()
    val isLoadVideoInProgress = MutableLiveData<Boolean>()

    private val loggers = setOf(AndroidLogSink())

    private val updateUISideEffect = object : SideEffect<HowToReduxState, HowToReduxAction> {
        override val name: String = "updateUISideEffect"

        override fun CoroutineScope.start(
            input: ReceiveChannel<HowToReduxAction>,
            stateAccessor: StateAccessor<HowToReduxState>,
            output: SendChannel<HowToReduxAction>,
            logger: SideEffectLogger
        ): Job = launch(context = CoroutineName(name)) {
            for (action in input) {
                val s = stateAccessor()

                when (action) {

                    is Initialize_Start, Initialize_Finish -> {
                        maybeUpdateLiveData(
                            isInitializeInProgress,
                            s._isInitializeInProgress
                        )
                    }

                    is ShowVideoFragment_Start, ShowVideoFragment_Finish -> {
                        maybeUpdateLiveData(
                            isShowVideoFragmentInProgress,
                            s._isShowVideoFragmentInProgress
                        )
                    }

                    HideVideoFragment_Start, HideVideoFragment_Finish -> {
                        maybeUpdateLiveData(
                            isHideVideoFragmentInProgress,
                            s._isHideVideoFragmentInProgress
                        )
                    }

                    LoadVideo_Start, LoadVideo_Finish -> {
                        maybeUpdateLiveData(
                            isLoadVideoInProgress,
                            s._isLoadVideoInProgress
                        )
                    }
                }
            }
        }
    }

    private val espressoTestIdlingResourceSideEffect =
        object : SideEffect<HowToReduxState, HowToReduxAction> {
            override val name: String = "updateUISideEffect"

            override fun CoroutineScope.start(
                input: ReceiveChannel<HowToReduxAction>,
                stateAccessor: StateAccessor<HowToReduxState>,
                output: SendChannel<HowToReduxAction>,
                logger: SideEffectLogger
            ): Job = launch(context = CoroutineName(name)) {
                for (action in input) {
                    val espressoTestIdlingResource =
                        (application as HowToApplication).espressoTestIdlingResource

                    when (action) {

                        is Initialize_Start, is ShowVideoFragment_Start, LoadVideo_Start, HideVideoFragment_Start -> {
                            espressoTestIdlingResource.increment()
                        }

                        Initialize_Finish, ShowVideoFragment_Finish, LoadVideo_Finish, HideVideoFragment_Finish -> {
                            espressoTestIdlingResource.decrement()
                        }
                    }
                }
            }
        }

    /**
     * [isXInProgress] remains null until we're ready to set it to true to trigger a UI event
     *
     * Once non-null, always propagate the [_isXInProgress] changes to [isXInProgress]
     */
    private fun maybeUpdateLiveData(
        isXInProgress: MutableLiveData<Boolean>,
        _isXInProgress: Boolean
    ) {
        if (null == isXInProgress.value) {
            if (_isXInProgress) {
                isXInProgress.value = _isXInProgress
            }
        } else {
            if (_isXInProgress != isXInProgress.value) {
                isXInProgress.value = _isXInProgress
            }
        }
    }

    private val reduxStore = this.createStore(
        name = "MyRedux Store",
        initialState = HowToReduxState(),
        logSinks = loggers.toList(),
        sideEffects = listOf(updateUISideEffect, espressoTestIdlingResourceSideEffect),
        reducer = ::reducer
    ).also {
        it.subscribeToChangedStateUpdates { newState: HowToReduxState ->
            mutableState.value = newState
        }
    }

    val dispatchAction: (HowToReduxAction) -> Unit = reduxStore::dispatch

    private fun reducer(state: HowToReduxState, action: HowToReduxAction): HowToReduxState {
        Log.d(t, "reduce $action")

        return when (action) {
            is Initialize_Start -> {
                state.copy(
                    howToVideos = action.howToVideos,
                    _isInitializeInProgress = true
                )
            }

            Initialize_Finish -> {
                state.copy(_isInitializeInProgress = false)
            }

            is ShowVideoFragment_Start -> {
                state.copy(
                    howToVideoShown = action.howToVideo,
                    _isShowVideoFragmentInProgress = true
                )
            }

            ShowVideoFragment_Finish -> {
                state.copy(_isShowVideoFragmentInProgress = false)
            }

            HideVideoFragment_Start -> {
                state.copy(
                    howToVideoShown = null,
                    _isHideVideoFragmentInProgress = true
                )
            }

            HideVideoFragment_Finish -> {
                state.copy(_isHideVideoFragmentInProgress = false)
            }

            LoadVideo_Start -> {
                state.copy(_isLoadVideoInProgress = true)
            }

            LoadVideo_Finish -> {
                state.copy(_isLoadVideoInProgress = false)
            }
        }
    }
}