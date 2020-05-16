package com.locuslabs.crserc

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freeletics.coredux.*
import com.freeletics.coredux.log.android.AndroidLogSink
import com.freeletics.coredux.log.common.LoggerLogSink
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlin.coroutines.CoroutineContext

class MyViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private val mutableState = MutableLiveData(MyReduxState())

    private val updateUISideEffect = object : SideEffect<MyReduxState, MyReduxAction> {
        override val name: String = "my updateUISideEffect"

        override fun CoroutineScope.start(
            input: ReceiveChannel<MyReduxAction>,
            stateAccessor: StateAccessor<MyReduxState>,
            output: SendChannel<MyReduxAction>,
            logger: SideEffectLogger
        ): Job = viewModelScope.launch(context = CoroutineName(name)) {
            for (action in input) {
                when (action) {
                    MyReduxAction.Async1StartAction, MyReduxAction.Async1FinishedAction -> {
                        isShowingResult1.value = isShowingResult1.value?.let { !it } ?: true
                    }
                    MyReduxAction.Async2FinishedAction -> {
                        isShowingResult2.value = isShowingResult2.value?.let { !it } ?: true
                    }
                }
            }
        }
    }

    private val performAsyncTaskSideEffect =
        CancellableSideEffect<MyReduxState, MyReduxAction>("my performAsyncTaskSideEffect") { _, action, _, handler ->
            when (action) {
                MyReduxAction.Async2StartAction -> handler { _, output ->
                    launch {
                        delay(DELAY_MILLISECONDS * 2)
                        output.send(MyReduxAction.Async2FinishedAction)
                    }
                }
                else -> null
            }
        }

    // Alternative implementation of performAsyncTaskSideEffect as a SimpleSideEffect
//    private val performAsyncTaskSideEffect =
//        SimpleSideEffect<MyReduxState, MyReduxAction>("my performAsyncTaskSideEffect") { _, action, _, handler ->
//            when (action) {
//                MyReduxAction.Async2StartAction -> handler {
//                    delay(DELAY_MILLISECONDS * 2)
//                    MyReduxAction.Async2FinishedAction
//                }
//                else -> null
//            }
//        }

    val isShowingResult1 = MutableLiveData<Boolean>()
    val isShowingResult2 = MutableLiveData<Boolean>()

    private val loggers = setOf(AndroidLogSink())

    private val reduxStore = this.createStore(
        name = "MyRedux Store",
        initialState = MyReduxState(),
        logSinks = loggers.toList(),
        sideEffects = listOf(
            updateUISideEffect,
            performAsyncTaskSideEffect
        ),
        reducer = ::reducer
    ).also {
        it.subscribeToChangedStateUpdates { newState: MyReduxState ->
            mutableState.value = newState
        }
    }

    val dispatchAction: (MyReduxAction) -> Unit = reduxStore::dispatch

    val state: LiveData<MyReduxState> = mutableState

    private fun reducer(state: MyReduxState, action: MyReduxAction): MyReduxState {
        Log.d(t, "reduce $action")

        return when (action) {
            MyReduxAction.Async1StartAction -> {
                state.copy(
                    result1 = listOf("showUIAsync")
                )
            }

            MyReduxAction.Async1FinishedAction -> {
                state
            }

            MyReduxAction.Async2StartAction -> {
                state.copy(
                    result2 = listOf("startBackendAsync")
                )
            }

            MyReduxAction.Async2FinishedAction -> {
                state.copy(
                    result2 = state.result2 + listOf("endBackendAsync")
                )
            }
        }
    }
}