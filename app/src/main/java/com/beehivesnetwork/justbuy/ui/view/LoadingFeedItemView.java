package com.beehivesnetwork.justbuy.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.beehivesnetwork.justbuy.R;

/**
 * Created by Miroslaw Stanek on 09.12.2015.
 */
public class LoadingFeedItemView extends FrameLayout {

//    @Bind(R.id.vSendingProgress)
//    SendingProgressView vSendingProgress;
    @Bind(R.id.vProgressBg)
    View vProgressBg;

    private OnLoadingFinishedListener onLoadingFinishedListener;

    public LoadingFeedItemView(Context context) {
        super(context);
        init();
    }

    public LoadingFeedItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingFeedItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingFeedItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_feed, this, true);
        ButterKnife.bind(this);
    }

    public void startLoading() {
//        vSendingProgress.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                vSendingProgress.getViewTreeObserver().removeOnPreDrawListener(this);
//                vSendingProgress.simulateProgress();
//                return true;
//            }
//        });
//        vSendingProgress.setOnLoadingFinishedListener(new SendingProgressView.OnLoadingFinishedListener() {
//            @Override
//            public void onLoadingFinished() {
//                vSendingProgress.animate().scaleY(0).scaleX(0).setDuration(200).setStartDelay(100);
//                vProgressBg.animate().alpha(0.f).setDuration(200).setStartDelay(100)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                vSendingProgress.setScaleX(1);
//                                vSendingProgress.setScaleY(1);
//                                vProgressBg.setAlpha(1);
//                                if (onLoadingFinishedListener != null) {
//                                    onLoadingFinishedListener.onLoadingFinished();
//                                    onLoadingFinishedListener = null;
//                                }
//                            }
//                        }).start();
//            }
//        });
    }

    public void setOnLoadingFinishedListener(OnLoadingFinishedListener onLoadingFinishedListener) {
        this.onLoadingFinishedListener = onLoadingFinishedListener;
    }

    public interface OnLoadingFinishedListener {
        void onLoadingFinished();
    }
}
