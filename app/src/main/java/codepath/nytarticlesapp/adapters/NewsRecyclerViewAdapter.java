/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.adapters;

import android.content.Context;
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

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsItemViewHolder> {

    private List<News> newsList;
    private Context context;

    public NewsRecyclerViewAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;

        DaggerInjector.builder().build().inject(this);
    }

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_view_item, parent, false);
        return new NewsItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsItemViewHolder vh, int position) {
        News news = newsList.get(position);

        Headline headline = news.getHeadline();

        vh.title.setText(null != headline ? headline.getMain() : "Missing");
        List<Multimedia> multimedia = news.getMultimedia();

        if (!multimedia.isEmpty()) {
            Glide.with(context).load("http://nytimes.com/" + multimedia.get(0).getUrl()).into(vh.imageView);
        } else {
            Glide.with(context).load("http://beilersautorepair.mechanicnet.com/mobile/img/placeholder.png").into(vh.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return null != newsList ? newsList.size() : 0;
    }
}
