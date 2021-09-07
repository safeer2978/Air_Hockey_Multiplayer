package com.airhockey.strikrr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout = findViewById(R.id.main_frame);

        Button button1 = findViewById(R.id.button1);
        Puck puck1 = new Puck(button1,0,600,frameLayout);

        Button button2 = findViewById(R.id.button2);
        Puck puck2 = new Puck(button2,500,600,frameLayout);

        Button button3 = findViewById(R.id.button3);
        Puck puck3 = new Puck(button3,0,400,frameLayout);

        Button button4 = findViewById(R.id.button4);
        Puck puck4 = new Puck(button4,400,0,frameLayout);

        ViewGroup viewGroup = (ViewGroup) frameLayout;
        Button button = new Button(this);
        button.setWidth(30);
        button.setHeight(30);
        button.setBackground(getDrawable(R.drawable.circle));
        button.setText("");
        viewGroup.addView(button,30,30);
        Puck puck = new Puck(button, 700,0,frameLayout);

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                puck.step();
                puck1.step();
                puck2.step();
                puck3.step();
                puck4.step();
                handler.post(this);
            }
        });
    }
}