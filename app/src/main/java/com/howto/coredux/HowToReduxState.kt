package com.howto.coredux

data class HowToReduxState(
    val howToVideos:List<HowToVideo>? = null,

    val _isInitializationInProgress: Boolean = IS_X_IN_PROGRESS_INIT
)