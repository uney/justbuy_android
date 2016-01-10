package com.beehivesnetwork.justbuy.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.beehivesnetwork.justbuy.MyApp;
import com.beehivesnetwork.justbuy.R;
import com.beehivesnetwork.justbuy.Utils;
import com.beehivesnetwork.justbuy.object.Ads;
import com.beehivesnetwork.justbuy.ui.adapter.FeedAdapter;
import com.beehivesnetwork.justbuy.ui.adapter.FeedItemAnimator;
import com.beehivesnetwork.justbuy.ui.utils.AppConstant;
import com.beehivesnetwork.justbuy.ui.utils.CommonUtils;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.uiUtils.ScrollSmoothLineaerLayoutManager;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements FeedAdapter.OnFeedItemClickListener{
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;
    final Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            feedAdapter.notifyDataSetChanged();
            timerHandler.postDelayed(this, 1000); //run every minute
        }
    };
    @Bind(R.id.rvFeed)
    UltimateRecyclerView rvFeed;
//    @Bind(R.id.btnCreate)
//    FloatingActionButton fabCreate;
    @Bind(R.id.content)
    CoordinatorLayout clContent;
    @Bind(R.id.loading_progress)
    LinearLayout loading_progress;

    private FeedAdapter feedAdapter;
    private final ArrayList<Ads> feedItems = new ArrayList<>();
    private boolean pendingIntroAnimation;
    private int page = 0;
    Handler getDataHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                ArrayList<Ads> arrayList = new ArrayList<>();
                arrayList = (ArrayList)msg.getData().getSerializable("result");
                CommonUtils.LogI("", "getDataHandler: " + arrayList.size());
                feedAdapter.insert(true, arrayList);
                adsArrayList.addAll(arrayList);
                feedAdapter.notifyDataSetChanged();
                if(loading_progress!=null){
                    loading_progress.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Handler refreshHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                ArrayList<Ads> arrayList = new ArrayList<>();
                arrayList = (ArrayList)msg.getData().getSerializable("result");
                CommonUtils.LogI("","getDataHandler: "+arrayList.size());
                feedAdapter.refreshItems(true, arrayList);
                adsArrayList.addAll(arrayList);
                feedAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    ArrayList<Ads> adsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFeed();

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        } else {
            feedAdapter.updateItems(false);
        }
        rvFeed.enableLoadmore();
        rvFeed.enableDefaultSwipeRefresh(true);
        rvFeed.setDefaultSwipeToRefreshColorScheme(R.color.main_color);
        feedAdapter.setCustomLoadMoreView(LayoutInflater.from(this)
                .inflate(R.layout.view_bottom_progressbar, null));
        rvFeed.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                Animation animShow;
                animShow = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_from_bottom);
                loading_progress.startAnimation(animShow);
                loading_progress.setVisibility(View.VISIBLE);
                MyApp.getMyApp().getApiHandler().getApiResult(AppConstant.API_URL + "&page=" + page, getDataHandler);
                page = page+1;
            }
        });

        rvFeed.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                MyApp.getMyApp().getApiHandler().getApiResult(AppConstant.API_URL+ "&page=" + page, refreshHandler);
            }
        });
        MyApp.getMyApp().getApiHandler().getApiResult(AppConstant.API_URL+ "&page=" + page, refreshHandler);

    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new ScrollSmoothLineaerLayoutManager(this, LinearLayoutManager.VERTICAL, false, 300) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);

        feedAdapter = new FeedAdapter(this, feedItems);
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setAdapter(feedAdapter);
        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
        rvFeed.setItemAnimator(new FeedItemAnimator());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            showFeedLoadingItemDelayed();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rvFeed.getLayoutManager().scrollToPosition(0);
                feedAdapter.showLoadingView();
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);
//        getMenuItem().getActionView().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();

    }

    private void startContentAnimation() {
        feedAdapter.updateItems(true);
    }

    public void showBuyDialog(final Ads ads) {
        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Action Now")
                .customView(R.layout.view_dialog, wrapInScrollView)
                .positiveText("Buy on eBay")
                .negativeText("Cancel")
                .neutralText("Call Seller")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                        String url = "http://www.ebay.com";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                        dialog.dismiss();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ads.getPhone()));
                        startActivity(intent);
                    }
                })
                .show();
        View view = dialog.getCustomView();
        TextView price = (TextView)view.findViewById(R.id.price);
        TextView discount = (TextView)view.findViewById(R.id.discount);
        TextView title = (TextView)view.findViewById(R.id.name);
        TextView phone = (TextView)view.findViewById(R.id.number);
        price.setText("Price: SGD "+ads.getPrice());
        discount.setText("("+ads.getCountDown()/1000+" cheaper on eBay)");
        title.setText("Product: "+ads.getName());
        phone.setText("Contact: "+ads.getPhone());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Toast.makeText(this, ""+item.getItemId(), Toast.LENGTH_LONG).show();

        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(this, ""+item.getItemId(), Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(View v, int position) {

    }

    @Override
    protected void onPause() {
        timerHandler.removeCallbacks(timerRunnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        timerHandler.postDelayed(timerRunnable, 500);
        super.onResume();
    }
}