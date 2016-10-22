/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.interfaces;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Adapter;

import javax.inject.Singleton;

import codepath.nytarticlesapp.activities.MainActivity;
import codepath.nytarticlesapp.adapters.NewsRecyclerViewAdapter;
import codepath.nytarticlesapp.modules.AppModule;
import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface Injector {
    void inject(MainActivity mainActivity);

    void inject(Context context);

    void inject(NewsRecyclerViewAdapter adapter);
}
