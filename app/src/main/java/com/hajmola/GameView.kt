package com.hajmola

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent

class BallView(context: Context, attributionSource: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attributionSource){

    var dx = 0f
    var dy = 0f

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

    var transmitter: ((Float, Float) -> Unit)? = null
    var receiver: ((Float, Float) -> Unit)? = null

}

class GameView(context: Context, attributionSource: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attributionSource){
    val somehtin = B2dFun()
    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(somehtin.stepInWorld(),0f,0f,null)
        postInvalidate()
    }
}