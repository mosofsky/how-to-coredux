package com.howto.coredux

@Suppress("ClassName") // suppress complaints about underscores in action names
sealed class HowToReduxAction {
    // Initialize_Start is a data class because it receives a property, howToVideos
    data class Initialize_Start(val howToVideos:List<HowToVideo>) : HowToReduxAction()
    // Initialize_Finish is an object since it has no properties (note: data class not allowed sans properties)
    object Initialize_Finish : HowToReduxAction()

    data class ShowVideoFragment_Start(val howToVideo: HowToVideo) : HowToReduxAction()
    object ShowVideoFragment_Finish : HowToReduxAction()
    object HideVideoFragment_Start : HowToReduxAction()
    object HideVideoFragment_Finish : HowToReduxAction()

    object LoadVideo_Start : HowToReduxAction()
    object LoadVideo_Finish : HowToReduxAction()
}