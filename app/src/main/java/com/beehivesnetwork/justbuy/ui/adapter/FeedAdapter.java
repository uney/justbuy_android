package com.beehivesnetwork.justbuy.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.beehivesnetwork.justbuy.R;
import com.beehivesnetwork.justbuy.object.Ads;
import com.beehivesnetwork.justbuy.ui.activity.MainActivity;
import com.beehivesnetwork.justbuy.ui.utils.CommonUtils;
import com.beehivesnetwork.justbuy.ui.view.LoadingFeedItemView;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.squareup.picasso.Picasso;

/**
 * Created by froger_mcs on 05.11.14.
 */
public class FeedAdapter extends UltimateViewAdapter<RecyclerView.ViewHolder> {
    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";

    public int initialPosition = 0;
    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;
    public static final int VIEW_TYPE_FOOTER = 3;

    private ArrayList<Ads> feedItems = new ArrayList<>();
    private int viewType = VIEW_TYPE_DEFAULT;

    private static Context context;
    private OnFeedItemClickListener onFeedItemClickListener;

    private boolean showLoadingView = false;

    public FeedAdapter(Context context, ArrayList<Ads> feedItems) {
        this.context = context;
        this.feedItems = feedItems;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new CellFeedViewHolder(view, 0);
//        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
            CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view, VIEW_TYPE_DEFAULT);
            setupClickableViews(view, cellFeedViewHolder);
            return cellFeedViewHolder;
        } else if (viewType == VIEW_TYPE_LOADER) {
            LoadingFeedItemView view = new LoadingFeedItemView(context);
            view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            return new LoadingCellFeedViewHolder(view);
        }else if (viewType == VIEW_TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_bottom_progressbar, parent, false);
            CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view, VIEW_TYPE_FOOTER);
            setupClickableViews(view, cellFeedViewHolder);
            return cellFeedViewHolder;
        }
        return null;
    }


    private void setupClickableViews(final View view, final CellFeedViewHolder cellFeedViewHolder) {

        cellFeedViewHolder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                feedItems.get(adapterPosition);
//                if (context instanceof MainActivity) {
//                    Intent i = new Intent(context, BuyActivity.class);
//                    i.putExtra("product", feedItems.get(adapterPosition));
//                    context.startActivity(i);
//                }
            }
        });
        cellFeedViewHolder.btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).showBuyDialog(feedItems.get(adapterPosition));
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((CellFeedViewHolder) viewHolder).bindView(feedItems.get(position));

        if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem((LoadingCellFeedViewHolder) viewHolder);
        }
    }

    private void bindLoadingFeedItem(final LoadingCellFeedViewHolder holder) {
        holder.loadingFeedItemView.setOnLoadingFinishedListener(new LoadingFeedItemView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                showLoadingView = false;
                notifyItemChanged(0);
            }
        });
        holder.loadingFeedItemView.startLoading();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        }
        else if(feedItems.size()-1==position){
            return VIEW_TYPE_FOOTER;
        }
        else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public long generateHeaderId(int i) {
        return 0;
    }

    public void updateItems(boolean animated) {
        feedItems.clear();
        initialPosition =0;
        initialPosition = feedItems.size();
        notifyDataSetChanged();
    }

    public void refreshItems(boolean animated, ArrayList<Ads> list) {
        feedItems.clear();
        feedItems.addAll(list);
        initialPosition = feedItems.size();
        notifyItemRangeInserted(0, feedItems.size());
    }

    public void insert(boolean animated, ArrayList<Ads> list) {
        feedItems.addAll(list);
        if (animated) {
            notifyItemRangeInserted(initialPosition, feedItems.size());
            initialPosition = feedItems.size();

        } else {
            notifyDataSetChanged();
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @Nullable @Bind(R.id.price)
        TextView price;
        @Nullable @Bind(R.id.date)
        TextView date;
        @Nullable @Bind(R.id.name)
        TextView name;
        @Nullable @Bind(R.id.pic)
        ImageView pic;
        @Nullable @Bind(R.id.description)
        TextView description;
        @Nullable @Bind(R.id.btn_buy)
        ImageView btn_buy;
        @Nullable @Bind(R.id.counter)
        TextSwitcher counter;
        @Nullable @Bind(R.id.locations)
        TextView locations;
        @Nullable @Bind(R.id.card_view)
        android.support.v7.widget.CardView card_view;
        @Nullable @Bind(R.id.buy_animation)
        ImageView buy_animation;
        @Nullable @Bind(R.id.bg_buy_animation)
        View bg_buy_animation;
        @Nullable @Bind(R.id.vImageRoot)
        FrameLayout vImageRoot;

        Ads ads;
        int type = FeedAdapter.VIEW_TYPE_DEFAULT;

        public CellFeedViewHolder(View view, int type) {
            super(view);
            ButterKnife.bind(this, view);
            this.type = type;
        }

        public void bindView(Ads ads) {
            if(type==FeedAdapter.VIEW_TYPE_DEFAULT){
                CommonUtils.LogI("","VIEW_TYPE : default");
                this.ads = ads;
                int adapterPosition = getAdapterPosition();
//                ivFeedCenter.setImageResource(adapterPosition % 2 == 0 ? R.drawable.img_feed_center_1 : R.drawable.img_feed_center_2);
//                description.setText(ads.getDescription());
                description.setText(ads.getDescription());
                if(ads.getCountDown()/1000<=0){
                    ads.setCountDown(0);
                }else{
                    counter.setCurrentText("" + ads.getCountDown()/1000);
                }
                long countDown = ads.getCountDown();
                ads.setCountDown(countDown-1000);
                if(ads.getCountDown()/1000<=0){
                    ads.setCountDown(0);
                }
//                btnLike.setImageResource(novel.getLiked()=="1" ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
                counter.setText(""+ads.getCountDown()/1000);
                Picasso.with(context)
                        .load(ads.getPic())
                        .placeholder(R.drawable.img_circle_placeholder)
                        .resize(CommonUtils.getScreenWidth(context), CommonUtils.getScreenWidth(context))
                        .centerCrop()
                        .into(pic);
                name.setText(ads.getName());
                locations.setText(ads.getLocations());
                date.setVisibility(View.GONE);
                price.setText("$"+ads.getPrice());
            }
            else{
                CommonUtils.LogI("", "VIEW_TYPE : else");
            }
        }

        public Ads getFeedItem() {
            return ads;
        }
    }


    public static class LoadingCellFeedViewHolder extends CellFeedViewHolder {

        LoadingFeedItemView loadingFeedItemView;

        public LoadingCellFeedViewHolder(LoadingFeedItemView view) {
            super(view, VIEW_TYPE_LOADER);
            this.loadingFeedItemView = view;
        }

        @Override
        public void bindView(Ads ads) {
            super.bindView(ads);
        }
    }

    public static class FeedItem {
        public int likesCount;
        public boolean isLiked;

        public FeedItem(int likesCount, boolean isLiked) {
            this.likesCount = likesCount;
            this.isLiked = isLiked;
        }
    }

    public interface OnFeedItemClickListener {
        void onItemClick(View v, int position);
    }
}
