package com.howto.coredux

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freeletics.coredux.*
import com.freeletics.coredux.log.android.AndroidLogSink
import com.howto.coredux.HowToReduxAction.Initialize
import com.howto.coredux.HowToReduxAction.Initialized
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlin.coroutines.CoroutineContext

class HowToViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private val mutableState = MutableLiveData<HowToReduxState>()

    val state: LiveData<HowToReduxState> = mutableState

    val isInitializationInProgress = MutableLiveData<Boolean>()

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

                    is Initialize, Initialized -> {
                        maybeUpdate(
                            isInitializationInProgress,
                            s._isInitializationInProgress
                        )
                    }

                }
            }
        }
    }

    /**
     * [isXInProgress] remains null until we're ready to set it to true to trigger a UI event
     *
     * Once non-null, always propagate changes to [_isXInProgress] to [isXInProgress]
     */
    private fun maybeUpdate(isXInProgress: MutableLiveData<Boolean>, _isXInProgress: Boolean) {
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
        sideEffects = listOf(updateUISideEffect),
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
            is Initialize -> {
                state.copy(
                    howToVideos = action.howToVideos,
                    _isInitializationInProgress = true
                )
            }

            Initialized -> {
                state.copy(_isInitializationInProgress = false)
            }
        }
    }
}