/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.modules;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import codepath.nytarticlesapp.NYApp;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module(includes = {GlideAppModule.class})
public class AppModule {

    @Provides
    @Singleton
    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    @Provides
    @Singleton
    @Named("NY_API_KEY")
    public String getNyAPIKey() {
        return "5cdab788b9004bd389035b5cee814ccf";
    }

    @Provides
    @Singleton
    public Gson getGson() {
        return new Gson();
    }
}
