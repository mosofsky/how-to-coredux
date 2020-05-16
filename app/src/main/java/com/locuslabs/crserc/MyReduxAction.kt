package com.locuslabs.crserc

sealed class MyReduxAction {
    object Async1StartAction : MyReduxAction()
    object Async1FinishedAction : MyReduxAction()
    object Async2StartAction : MyReduxAction()
    object Async2FinishedAction : MyReduxAction()
}