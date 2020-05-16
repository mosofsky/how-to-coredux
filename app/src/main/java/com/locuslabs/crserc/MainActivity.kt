package com.locuslabs.crserc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    private val myViewModel by viewModels<MyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myViewModel.isShowingResult1.observe(this, Observer {
            if (it) {
                findViewById<Button>(R.id.doAsyncTasksButton).isEnabled = false
                findViewById<TextView>(R.id.result1TextView).text = state().result1.toString()
                findViewById<TextView>(R.id.result1TextView).visibility = View.VISIBLE
                Handler().postDelayed({
                    myViewModel.dispatchAction(MyReduxAction.Async1FinishedAction)
                }, DELAY_MILLISECONDS)
            } else {
                findViewById<Button>(R.id.doAsyncTasksButton).isEnabled = true
                (applicationContext as MyApplication).espressoTestIdlingResource.decrement()
            }
        })

        myViewModel.isShowingResult2.observe(this, Observer {
            findViewById<TextView>(R.id.result2TextView).text = state().result2.toString()
            findViewById<TextView>(R.id.result2TextView).visibility = View.VISIBLE
            (applicationContext as MyApplication).espressoTestIdlingResource.decrement()
        })

        findViewById<Button>(R.id.doAsyncTasksButton).setOnClickListener {
            (applicationContext as MyApplication).espressoTestIdlingResource.increment()
            (applicationContext as MyApplication).espressoTestIdlingResource.increment()

            findViewById<TextView>(R.id.result1TextView).visibility = View.GONE
            findViewById<TextView>(R.id.result2TextView).visibility = View.GONE

            myViewModel.dispatchAction(MyReduxAction.Async1StartAction)
            myViewModel.dispatchAction(MyReduxAction.Async2StartAction)
        }
    }

    private fun state(): MyReduxState {
        return myViewModel.state.value!!
    }
}