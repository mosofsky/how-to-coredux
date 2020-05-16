package com.howto.coredux

sealed class HowToReduxAction {
    // Initialize is a data class because it receives a property, howToVideos
    data class Initialize(val howToVideos:List<HowToVideo>) : HowToReduxAction()
    // Initialized is an object since it has no properties (note: data class not allowed sans properties)
    object Initialized : HowToReduxAction()
}