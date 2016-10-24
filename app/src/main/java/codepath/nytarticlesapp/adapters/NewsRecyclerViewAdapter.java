/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.adapters;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import codepath.nytarticlesapp.R;
import codepath.nytarticlesapp.interfaces.DaggerInjector;
import codepath.nytarticlesapp.models.Headline;
import codepath.nytarticlesapp.models.Multimedia;
import codepath.nytarticlesapp.models.News;
import codepath.nytarticlesapp.utils.Constants;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsItemViewHolder> {

    private static final int WITH_IMAGE = 0;
    private static final int WO_IMAGE = 1;

    private List<News> newsList;
    private Activity activity;

    public NewsRecyclerViewAdapter(Activity activity, List<News> newsList) {
        this.activity = activity;
        this.newsList = newsList;

        DaggerInjector.builder().build().inject(this);
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public void addPage(List<News> newsList) {
        this.newsList.addAll(newsList);
    }

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        if (viewType == WITH_IMAGE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_view_item, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_view_item_without_image, parent, false);
        }

        return new NewsItemViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return null == newsList.get(position).getBestImage() ? WO_IMAGE : WITH_IMAGE;
    }

    @Override
    public void onBindViewHolder(final NewsItemViewHolder vh, int position) {
        final News news = newsList.get(position);

        Headline headline = news.getHeadline();

        vh.title.setText(null != headline ? headline.getMain() : "Missing");
        Multimedia newsBestImage = news.getBestImage();

        if (null != newsBestImage && null != vh.imageView) {
            Glide.with(activity).load("http://nytimes.com/" + newsBestImage.getUrl()).placeholder(R.drawable.image_placeholder).into(vh.imageView);
        }

        if(position % 2 == 0) {
            vh.divider.setVisibility(View.GONE);
        } else {
            vh.divider.setVisibility(View.VISIBLE);
        }

        vh.rootView.setOnClickListener(v -> {
            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.share_icon);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, news.getWebUrl());

            PendingIntent pendingIntent = PendingIntent.getActivity(activity, Constants.SHARE_NEWS_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
            builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(activity, Uri.parse(news.getWebUrl()));
        });
    }

    @Override
    public int getItemCount() {
        return null != newsList ? newsList.size() : 0;
    }

    public void clearData() {
        if (null != newsList) {
            newsList.clear();
        }
    }
}
