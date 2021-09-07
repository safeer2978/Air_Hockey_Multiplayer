package com.airhockey.strikrr;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class HomeFragment extends Fragment implements View.OnClickListener {

    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        view.findViewById(R.id.home_play_now).setOnClickListener(this);
        view.findViewById(R.id.home_leaderboards).setOnClickListener(this);
        view.findViewById(R.id.home_mute).setOnClickListener(this);
        view.findViewById(R.id.home_profile).setOnClickListener(this);

        Animation fadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.home_logo_slide_up);
        ImageView logo = view.findViewById(R.id.home_logo);
        logo.setAnimation(fadeIn);
        fadeIn.setDuration(800);
        Animation fadeInPlayNow = AnimationUtils.loadAnimation(getContext(),R.anim.home_logo_slide_up);
        Animation fadeInLeaderBoards = AnimationUtils.loadAnimation(getContext(),R.anim.home_logo_slide_up);
        fadeInPlayNow.setDuration(600);
        fadeInLeaderBoards.setDuration(600);
        view.findViewById(R.id.home_play_now).setAnimation(fadeInPlayNow);
        view.findViewById(R.id.home_leaderboards).setAnimation(fadeInLeaderBoards);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_play_now:
                navController.navigate(R.id.action_homeFragment_to_searchingPlayersFragment);
                break;
            case R.id.home_leaderboards:
                navController.navigate(R.id.action_homeFragment_to_leaderBoardsFragment);
                break;
            case R.id.home_mute:
                break;
            case R.id.home_profile:
                navController.navigate(R.id.action_homeFragment_to_profileFragment);
                break;
        }
    }
}