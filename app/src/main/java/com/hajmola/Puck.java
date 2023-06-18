/*
package com.hajmola;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.Random;

public class Puck {
    Integer frameRate = 30;
    View view;
    float x=100;
    float y=100;
    float parent_x;
    float parent_y;
    PointF moveVec=new PointF();
    FrameLayout frameLayout;

    Player player;
    Boolean playersExist = false;

    public Puck(View view, float start_x, float start_y, FrameLayout frameLayout) {
        this.view = view;
        Random random = new Random();
        int min=6;
        int max=18;
        Math.floor(Math.random()*(max-min+1)+min);
        moveVec.set((float) Math.floor(Math.random()*(max-min+1)+min),(float) Math.floor(Math.random()*(max-min+1)+min));
        x=start_x;
        y=start_y;
        this.frameLayout = frameLayout;
        parent_x = 400;
        parent_y = 200;
        View parent = (View) view.getParent();
        Log.w("DIMS",view.getWidth()+":"+parent_y);

        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                parent_x=frameLayout.getWidth();
                parent_y=frameLayout.getHeight();
            }
        });
    }

    public Puck(View view, FrameLayout frameLayout, Player player) {
        this.view = view;
        this.player = player;
        playersExist = true;
        Random random = new Random();
        int min=6;
        int max=18;
        this.frameLayout = frameLayout;
        parent_x = 400;
        parent_y = 200;
        View parent = (View) view.getParent();
        Log.w("DIMS",view.getWidth()+":"+parent_y);
        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                parent_x=frameLayout.getWidth();
                parent_y=frameLayout.getHeight();
            }
        });
    }



    Float ratio = 1f;
    Long timeToUpdate = System.currentTimeMillis();

    public void changePosition(double x, double y){

        Log.w("VALUES",x+" "+y);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = (int)x ;
        layoutParams.topMargin = (int)y;
        layoutParams.rightMargin = 0;
        layoutParams.bottomMargin = 0;
        view.setLayoutParams(layoutParams);
    }

    public void step(){
        collide();
        long current = System.currentTimeMillis();
        long delta = current-timeToUpdate;
        ratio = 1f + delta*frameRate/1000f;
        timeToUpdate=current+1000L/frameRate;
        x+=moveVec.x;
        y+=moveVec.y;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = (int)x ;
        layoutParams.topMargin = (int)y;
        layoutParams.rightMargin = 0;
        layoutParams.bottomMargin = 0;
        view.setLayoutParams(layoutParams);


        //location.offset(moveVec.x*ratio, moveVec.y*ratio);
    }

    public void collide(){

        if(playersExist){
            int x_low = player.playerX-50;

            if((player.playerX<view.getX() && view.getX()<player.playerX+player.view.getWidth())
                    && (view.getY()<player.playerY+player.view.getHeight() && view.getY()>player.playerY))
            {
                moveVec.x = moveVec.x+2*(float) player.getVelocityX();
                moveVec.y = moveVec.y+2*player.getVelocityY();

                moveVec.x = -moveVec.x;
                moveVec.y = -moveVec.y;
            }
        }


        if (view.getX()<= 0 || view.getX() >= parent_x) {
            moveVec.x = -moveVec.x;
            if (view.getX() <= 0) {
                view.setX(view.getX()-view.getX());
            } else {
                view.setX(view.getX()+parent_x - view.getX());
            }
        }
        if (view.getY() <= 0 || view.getY() >= parent_y) {
            moveVec.y = -moveVec.y;
            if (view.getY() <= 0) {
                view.setY(view.getY()-view.getY());
            } else {
                view.setY(view.getY()+parent_y- view.getY());
            }
        }
    }



}
*/
