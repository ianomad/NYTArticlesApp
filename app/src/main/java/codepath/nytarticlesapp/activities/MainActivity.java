package codepath.nytarticlesapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import codepath.nytarticlesapp.R;
import codepath.nytarticlesapp.adapters.NewsRecyclerViewAdapter;
import codepath.nytarticlesapp.interfaces.DaggerInjector;
import codepath.nytarticlesapp.interfaces.NewsCallback;
import codepath.nytarticlesapp.models.NewsResponse;
import codepath.nytarticlesapp.network.NYTApiClient;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.newsRecyclerView)
    RecyclerView newsRecyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Inject
    NYTApiClient nyApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerInjector.builder().build().inject(this);
        ButterKnife.bind(this);

        initGui();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    private void initGui() {
        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle("Hello, world!");
        }

        recyclerViewLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        newsRecyclerView.setLayoutManager(recyclerViewLayoutManager);

        nyApiClient.searchNews("Asia", new NewsCallback() {
            @Override
            public void onSuccess(final NewsResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Debug", "Here we are: " + response.getResponse().getNews());
                        newsRecyclerView.setAdapter(new NewsRecyclerViewAdapter(MainActivity.this, response.getResponse().getNews()));
                    }
                });
            }

            @Override
            public void onError(Exception e, String message) {
                Log.e("Debug", "Error", e);
            }
        });
    }
}
