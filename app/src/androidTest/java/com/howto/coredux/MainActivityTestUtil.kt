package com.howto.coredux

import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tapper

/**
 * Perform a click at a coordinate in the coordinate system of the view, for example:
 *
 * onView(withId(R.id.llMapView)).perform(clickXY(Tap.DOUBLE,703.65375f, 395.02158f))
 *
 * <a href="https://stackoverflow.com/a/22798043/2848676">stackoverflow</a>
 */
fun clickXY(tapper: Tapper, x: Float, y: Float): GeneralClickAction {
    return GeneralClickAction(
        tapper,
        CoordinatesProvider { view ->
            val screenPos = IntArray(2)
            view.getLocationOnScreen(screenPos)

            val screenX = screenPos[0] + x
            val screenY = screenPos[1] + y

            floatArrayOf(screenX, screenY)
        },
        Press.FINGER,
        0,
        0,
        null
    )
}