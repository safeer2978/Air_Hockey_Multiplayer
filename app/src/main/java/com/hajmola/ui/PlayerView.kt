package com.hajmola.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class PlayerView(context: Context, attributionSource: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attributionSource){

    var dx = 0f
    var dy = 0f
    var transmitter: ((Float, Float) -> Unit)? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                dx = x - event.rawX
                dy = y - event.rawY
            }

            MotionEvent.ACTION_MOVE->{
                x = event.rawX + dx
                y = event.rawY + dy
                transmitter?.invoke(x,y)
            }}
        return true
    }
}