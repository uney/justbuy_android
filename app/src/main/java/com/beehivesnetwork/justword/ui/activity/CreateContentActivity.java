package com.beehivesnetwork.justword.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ViewSwitcher;

import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.autocomplete.SchoolListAdapter;
import com.beehivesnetwork.justword.R;
import com.beehivesnetwork.justword.Utils;
import com.beehivesnetwork.justword.ui.adapter.PhotoFiltersAdapter;
import com.beehivesnetwork.justword.ui.view.RevealBackgroundView;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Miroslaw Stanek on 08.02.15.
 */
public class CreateContentActivity extends BaseActivity implements RevealBackgroundView.OnStateChangeListener{
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final int STATE_TAKE_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;

    @Bind(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;

    @Bind(R.id.card_view)
    CardView card_view;

    @Bind(R.id.hash_tag)
    MultiAutoCompleteTextView hashTag;

    private boolean pendingIntro;
    private int currentState;
    private SchoolListAdapter schoolListAdapter;

    private File photoPath;

    public static void startCameraFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, CreateContentActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_content);
        updateStatusBarColor();
        updateState(STATE_TAKE_PHOTO);
        setupRevealBackground(savedInstanceState);
        schoolListAdapter = new SchoolListAdapter(this, R.layout.view_autocomplete_item);
        hashTag.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        hashTag.setAdapter(schoolListAdapter);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff111111);
        }
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setFillPaintColor(getResources().getColor(R.color.style_color_primary));
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            card_view.setVisibility(View.VISIBLE);
            startIntroAnimation();
        } else {
            card_view.setVisibility(View.INVISIBLE);
        }
    }

    private void startIntroAnimation() {
        card_view.setTranslationY(card_view.getHeight());
        card_view.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
    }




    @Override
    public void onBackPressed() {
        if (currentState == STATE_SETUP_PHOTO) {
            updateState(STATE_TAKE_PHOTO);
        } else {
            super.onBackPressed();
        }
    }

    private void updateState(int state) {
        currentState = state;
//        if (currentState == STATE_TAKE_PHOTO) {
//            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_right);
//            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_right);
//            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_left);
//            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_left);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ivTakenPhoto.setVisibility(View.GONE);
//                }
//            }, 400);
//        } else if (currentState == STATE_SETUP_PHOTO) {
//            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_left);
//            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_left);
//            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_right);
//            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_right);
//            ivTakenPhoto.setVisibility(View.VISIBLE);
//        }
    }
}
