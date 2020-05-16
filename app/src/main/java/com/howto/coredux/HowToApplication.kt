package com.howto.coredux

import android.app.Application
import androidx.test.espresso.idling.CountingIdlingResource

class HowToApplication : Application() {
    // this idling resource will be used by Espresso to wait for and synchronize with RetroFit Network call
    var espressoTestIdlingResource =
        CountingIdlingResource("MyCountingIdlingResource")
}