package com.howto.coredux

data class HowToReduxState(
    val howToVideos:List<HowToVideo>? = null,
    val howToVideoShown:HowToVideo? = null,

    val _isInitializeInProgress: Boolean = IS_X_IN_PROGRESS_INIT,
    val _isShowVideoFragmentInProgress: Boolean = IS_X_IN_PROGRESS_INIT,
    val _isHideVideoFragmentInProgress: Boolean = IS_X_IN_PROGRESS_INIT,
    val _isLoadVideoInProgress: Boolean = IS_X_IN_PROGRESS_INIT
)