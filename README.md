# how-to-coredux
How to implement CoRedux for Android

[CoRedux](https://github.com/freeletics/CoRedux) is a stateless data store for Android modeled after the [Redux design pattern popular in JavaScript](https://redux.js.org/).  This repository demonstrates one way to implement CoRedux.

## What's difficult to implement with CoRedux

Although CoRedux provides a Redux data store, it is hard to use with a user interface because it lacks anything equivalent to [React Redux](https://react-redux.js.org/).  React Redux seamlessly propagates state changes to the user interface (UI).  It does this by smartly re-rendering the specifics parts of a UI that depend on specific slices of state.  Thus it automatically manages the relationship between the UI and the state.  Google doesn't currently recommend anything like React Redux for Android development.

Google's [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) recommends that we keep our data in a ViewModel and update the user interface via LiveData.  So in the how-to-coredux project we've attempted to propagate the changes in the ViewModel's CoRedux data store to the UI via LiveData.

Another approach would be to replace Android's UI layer with something more like React.  See the section below on [Further Areas of Exploration] for some libraries that may make this approach possible.  But for the purposes of the how-to-coredux project we attempted to stick as much as possible to the Google's suggested architecture.

## How to propagate CoRedux state changes to the UI

The [CoRedux](https://github.com/freeletics/CoRedux) repository provides good instructions on how to set up CoRedux.  To propagate state changes to the UI, the how-to-coredux project implements a [CoRedux side effect](https://github.com/freeletics/CoRedux#side-effects):

```kotlin
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
```

CoRedux calls `updateUISideEffect` each time it reduces the state in response to an action.  `updateUISideEffect` essentially determines which piece of state affects which piece of UI.  It tries to translate each CoRedux state change to a LiveData update via a function `maybeUpdateLiveData`:

```kotlin
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
```

`maybeUpdateLiveData` checks if the value of each piece of state differs from its corresponding LiveData value.  If it detects a discrepancy, then `maybeUpdateLiveData` synchronizes the LiveData to its corresponding state variable.  `maybeUpdateLiveData` makes magic happen because the UI updates each time the LiveData updates.

To update the UI, the how-to-coredux project subscribes to LiveData changes in the `initUIObservers` of each Fragment:

```kotlin
private fun initUIObservers() {
    howToViewModel.isInitializeInProgress.observe(this, Observer {
        if (it) {
            initRecyclerView()
            howToViewModel.dispatchAction(Initialize_Finish)
        }
    })
}
```

Each `Observer` responds when the LiveData value changes from `false` to `true`.  When it completes its response, it takes responsibility for setting the LiveData value from `true` back to `false`.  To do that, it dispatches an action to signify that the original action is complete.  Thus in how-to-coredux each action that starts a UI update has a corresponding action to signify the UI update completed.  This allows for asynchronous UI handling which will be explained in the following example.   

### Example: how to load a video asynchronously

how-to-coredux shows how to load a video asynchronously so that the UI could show and hide a loading indicator when loading begins and ends, respectively.  When a user clicks the name of the video from the list on the main page, it pops open an Android [`Fragment`](https://developer.android.com/reference/androidx/fragment/app/Fragment)).  When that Fragment opens it dispatches the action `LoadVideo_Start` as follows:

```kotlin
howToViewModel.dispatchAction(LoadVideo_Start)
```

Our reducer updates the state variable `_isShowVideoFragmentInProgress` to `true` so that our state reflects that the asynchronous task is in-progress:

```kotlin
LoadVideo_Start -> {
    state.copy(_isLoadVideoInProgress = true)
}
``` 

Later when the asynchronous event finishes we will set `_isLoadVideoInProgress` back to `false` to signify that it's no longer in progress.  More on that later.  First we'll explain how the CoRedux side effect causes the UI to be updated.

The CoRedux side effect in our project is called `updateUISideEffect` and it propagates the `_isLoadVideoInProgress` state change to its corresponding LiveData, `isLoadVideoInProgress`:

```kotlin
LoadVideo_Start, LoadVideo_Finish -> {
    maybeUpdateLiveData(
        isLoadVideoInProgress,
        s._isLoadVideoInProgress
    )
}
``` 

> Note that this same piece of code not only propagates the value `true` but also will propagate `false` later in the flow of execution, as will be explained below.

We've adopted a naming convention of `_isXInProgress` for the CoRedux state variable and `isXInProgress` for its corresponding LiveData.  

To begin the actual loading, our `Fragment` registers an `Observer` of the `LiveData`: 

```kotlin
howToViewModel.isLoadVideoInProgress.observe(viewLifecycleOwner, Observer {
    if (it) {
        maybeLoadVideoAsynchronously()
    }
})
```

This could perform any asynchronous code; in our case it loads a video as follows:

```kotlin
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
```

That's a lot of detail, where the essential bit is the `loadData()` call that kicks off the asynchronous loading.  Here we could also display an animated loading indicator (we will elaborate on that below).

Now that we've got the loading taken care of, the last part is to detect when it finishes.  Most asynchronous functions offer some mechanism for you, the programmer, to know when they're done.  Thankfully that mechanism is available for the `loadData()` function:

```kotlin
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
```

The essential bit is the `override fun onPageFinished()` which simply dispatches `LoadVideo_Finish`.

We have already described how one action got processed and now we have another.  The first action we described was the `LoadVideo_Start` action which began the asynchronous processing.  Now we'll cover its corresponding action, `LoadVideo_Finish` which signifies the end of the asynchronous action.

```kotlin
LoadVideo_Finish -> {
    state.copy(_isLoadVideoInProgress = false)
}
```

Earlier you saw the following code snippet from our CoRedux side effect `updateUISideEffect`:

```kotlin
LoadVideo_Start, LoadVideo_Finish -> {
    maybeUpdateLiveData(
        isLoadVideoInProgress,
        s._isLoadVideoInProgress
    )
}
``` 

Just as for the case of `LoadVideo_Start`, we now handle `LoadVideo_Finish` by propagating the `false` value of `_isLoadVideoInProgress` to its corresponding LiveData, `isLoadVideoInProgress`.

#### Example: how to show a loading indicator

That's it for our implementation but a more user-friendly implementation would show and hide a loading indicator.  To show and hide that indicator, you can make use of the `Observer`:

```kotlin
howToViewModel.isLoadVideoInProgress.observe(viewLifecycleOwner, Observer {
    if (it) {
        // SHOW loading indicator
        maybeLoadVideoAsynchronously()
    } else {
        // HIDE loading indicator
    }
})
```

## How to test asynchronous code

Asynchronous code confounds those who wish to test their code.  Asynchronous code that alters a user interface compounds the problem for test automation enthusiasts.  Fortunately [Android's Espresso](https://developer.android.com/training/testing/espresso) framework offers a solution in form of [Espresso idling resource counting](https://developer.android.com/training/testing/espresso/idling-resource).  And idling resource counter is a bit misnamed because it actually counts resources that ARE NOT idle.  Suffice it to say, when its count decrements down to 0, that means all resources are idle.  And when all resources are idle, the Espresso test framework knows it can proceed with the next test instruction.

Applying idling resource counting to code in the how-to-coredux project was as easy as adding another side effect:

```kotlin
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
```

Each of the `*_Start` actions results in an `increment()` of the idling resource counter and likewise each `*_Finish` action results in a `decrement()`.  Again, note the "idling resource counter" is actually counts how many asynchronous calls are in progress, meaning it counts the number of resources that are NOT idle.  If you can overlook that poor choice of names, the `espressoTestIdlingResourceSideEffect` is a very simple mechanism to the challenging problem of testing asynchronous UI code.

## Further Areas of Exploration

Others have been exploring the idea of updating a UI automatically on state changes.  For example,
[Anvil](https://github.com/anvil-ui/anvil) is a "library for creating reactive user interfaces".  The article [Writing a Todo app with Redux on Android](https://medium.com/@trikita/writing-a-todo-app-with-redux-on-android-5de31cfbdb4f) shows how to subscribe the UI to changes in state by calling:

    `store.subscribe(Anvil::render);` 
    
It would be interesting to see how Anvil works with CoRedux.
 