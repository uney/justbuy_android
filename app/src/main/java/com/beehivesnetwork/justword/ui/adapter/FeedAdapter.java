package com.beehivesnetwork.justword.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.beehivesnetwork.justword.R;
import com.beehivesnetwork.justword.object.Novel;
import com.beehivesnetwork.justword.ui.activity.MainActivity;
import com.beehivesnetwork.justword.ui.utils.CircleTransformation;
import com.beehivesnetwork.justword.ui.utils.CommonUtils;
import com.beehivesnetwork.justword.ui.view.LoadingFeedItemView;
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

    private final List<Novel> feedItems = new ArrayList<>();
    private int viewType = VIEW_TYPE_DEFAULT;

    private static Context context;
    private OnFeedItemClickListener onFeedItemClickListener;

    private boolean showLoadingView = false;

    public FeedAdapter(Context context) {
        this.context = context;
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
        cellFeedViewHolder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onCommentsClick(view, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onMoreClick(v, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                feedItems.get(adapterPosition).addLike();
                notifyItemChanged(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).showLikedSnackbar("Liked!", false);
                }
            }
        });
        cellFeedViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                feedItems.get(adapterPosition).addLike();
                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).showLikedSnackbar("Liked!", false);
                }
            }
        });
        cellFeedViewHolder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onProfileClick(view);
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
        feedItems.addAll(Arrays.asList(
                new Novel("","","asdf","asd","","","","",1,10,"","",""),
                new Novel("","","asdf","asdf","","","","",1,20,"","",""),
                new Novel("","","A","http://indiabright.com/wp-content/uploads/2015/11/profile_picture_by_kyo_tux-d4hrimy.png","","","","",1,50,"","",""),
                new Novel("","","asdf","asdf","","","","",1,40,"","",""),
                new Novel("","","asdf","asf","","","","",1,50,"","",""),
                new Novel("","","asdf","asdf","","","","",1,10,"","",""),
                new Novel("","","asdf","asdf","","","","",1,10,"","","")
        ));
        if (animated) {
            notifyItemRangeInserted(0, feedItems.size());
            initialPosition = feedItems.size();
        } else {
            notifyDataSetChanged();
        }
    }

    public void refreshItems(boolean animated) {
        feedItems.clear();
        feedItems.addAll(Arrays.asList(
                new Novel("","","Jack","http://indiabright.com/wp-content/uploads/2015/11/profile_picture_by_kyo_tux-d4hrimy.png","","","","",1,10,"","",""),
                new Novel("","","Will","asfd","","","","",1,20,"","",""),
                new Novel("","","Vi","http://indiabright.com/wp-content/uploads/2015/11/profile_picture_by_kyo_tux-d4hrimy.png","","","","",1,30,"","",""),
                new Novel("","","asfd","asdf","","","","",1,40,"","",""),
                new Novel("","","A","http://indiabright.com/wp-content/uploads/2015/11/profile_picture_by_kyo_tux-d4hrimy.png","","","","",1,50,"","",""),
                new Novel("","","asdf","asdf","","","","",1,10,"","",""),
                new Novel("","","asdf","asdf","","","","",1,10,"","","")
        ));
        if (animated) {
            notifyItemRangeInserted(initialPosition, feedItems.size());
            initialPosition = feedItems.size();
        } else {
            notifyDataSetChanged();
        }
    }

    public void insert(boolean animated) {
        feedItems.addAll(Arrays.asList(
                new Novel("","","Jack","http://indiabright.com/wp-content/uploads/2015/11/profile_picture_by_kyo_tux-d4hrimy.png","","","","",1,10,"","",""),
                new Novel("","","Will","asfd","","","","",1,20,"","",""),
                new Novel("","","Vi","http://indiabright.com/wp-content/uploads/2015/11/profile_picture_by_kyo_tux-d4hrimy.png","","","","",1,30,"","",""),
                new Novel("","","asfd","asdf","","","","",1,40,"","",""),
                new Novel("","","A","http://indiabright.com/wp-content/uploads/2015/11/profile_picture_by_kyo_tux-d4hrimy.png","","","","",1,50,"","",""),
                new Novel("","","asdf","asdf","","","","",1,10,"","",""),
                new Novel("","","asdf","asdf","","","","",1,10,"","","")
        ));
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
        @Nullable @Bind(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @Nullable @Bind(R.id.ivFeedBottom)
        ImageView ivFeedBottom;
        @Nullable @Bind(R.id.btnComments)
        ImageButton btnComments;
        @Nullable @Bind(R.id.btnLike)
        ImageButton btnLike;
        @Nullable @Bind(R.id.btnMore)
        ImageButton btnMore;
        @Nullable @Bind(R.id.vBgLike)
        View vBgLike;
        @Nullable @Bind(R.id.ivLike)
        ImageView ivLike;
        @Nullable @Bind(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @Nullable @Bind(R.id.ivUserProfile)
        RelativeLayout ivUserProfile;
        @Nullable @Bind(R.id.user_name)
        TextView user_name;
        @Nullable @Bind(R.id.profile_pic)
        ImageView profile_pic;
        @Nullable @Bind(R.id.vImageRoot)
        FrameLayout vImageRoot;
        @Nullable @Bind(R.id.bottom_progress_bar)
        ProgressBar bottomProgressBar;

        Novel novel;
        int type = FeedAdapter.VIEW_TYPE_DEFAULT;

        public CellFeedViewHolder(View view, int type) {
            super(view);
            ButterKnife.bind(this, view);
            this.type = type;
        }

        public void bindView(Novel novel) {
            if(type==FeedAdapter.VIEW_TYPE_DEFAULT){
                CommonUtils.LogI("","VIEW_TYPE : default");
                this.novel = novel;
                int adapterPosition = getAdapterPosition();
                ivFeedCenter.setImageResource(adapterPosition % 2 == 0 ? R.drawable.img_feed_center_1 : R.drawable.img_feed_center_2);
                ivFeedBottom.setImageResource(adapterPosition % 2 == 0 ? R.drawable.img_feed_bottom_1 : R.drawable.img_feed_bottom_2);
                btnLike.setImageResource(novel.getLiked()=="1" ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
                tsLikesCounter.setCurrentText(vImageRoot.getResources().getQuantityString(
                        R.plurals.likes_count, novel.getLike(), novel.getLike()
                ));
                Picasso.with(context)
                        .load(novel.getAuthor_pic())
                        .placeholder(R.drawable.img_circle_placeholder)
                        .resize(40, 40)
                        .centerCrop()
                        .transform(new CircleTransformation())
                        .into(profile_pic);
                user_name.setText(novel.getAuthor_name());
            }
            else{
                CommonUtils.LogI("", "VIEW_TYPE : else");
            }
        }

        public Novel getFeedItem() {
            return novel;
        }
    }


    public static class LoadingCellFeedViewHolder extends CellFeedViewHolder {

        LoadingFeedItemView loadingFeedItemView;

        public LoadingCellFeedViewHolder(LoadingFeedItemView view) {
            super(view, VIEW_TYPE_LOADER);
            this.loadingFeedItemView = view;
        }

        @Override
        public void bindView(Novel novel) {
            super.bindView(novel);
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
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);

        void onProfileClick(View v);
    }
}
