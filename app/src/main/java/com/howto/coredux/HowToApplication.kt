package com.howto.coredux

import android.app.Application
import androidx.test.espresso.idling.CountingIdlingResource

class HowToApplication : Application() {
    var espressoTestIdlingResource =
        CountingIdlingResource("MyCountingIdlingResource")
}