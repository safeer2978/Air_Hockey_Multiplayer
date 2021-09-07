package com.airhockey.strikrr;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;


public class SearchingPlayersFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_searching_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView timeElapsed = view.findViewById(R.id.searching_time_elapsed);
        timeElapsed.setText("waiting");

        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        long start = Calendar.getInstance().getTimeInMillis();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Date date = new Date(Calendar.getInstance().getTimeInMillis()-start-1800000);
                timeElapsed.setText("Time Elapsed: "+format.format(date));
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}