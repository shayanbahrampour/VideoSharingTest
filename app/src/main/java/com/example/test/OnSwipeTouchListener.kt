package com.example.test

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener


open class OnSwipeTouchListener(context: Context?) : OnTouchListener {
    private val gestureDetector: GestureDetector
    open fun onSwipeLeft() {}
    fun onSwipeRight() {}
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
             val SWIPE_DISTANCE_THRESHOLD = 100
             val SWIPE_VELOCITY_THRESHOLD = 100

            val distanceX = e2.x - e1.x
            val distanceY = e2.y - e1.y
            if (Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceY > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false
        }
    }
    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }
}