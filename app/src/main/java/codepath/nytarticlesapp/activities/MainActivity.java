package codepath.nytarticlesapp.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import codepath.nytarticlesapp.R;
import codepath.nytarticlesapp.adapters.NewsRecyclerViewAdapter;
import codepath.nytarticlesapp.dialogs.SearchDialog;
import codepath.nytarticlesapp.interfaces.DaggerInjector;
import codepath.nytarticlesapp.interfaces.NewsCallback;
import codepath.nytarticlesapp.models.NewsResponse;
import codepath.nytarticlesapp.network.NYTApiClient;
import codepath.nytarticlesapp.utils.NewsSearchListener;
import codepath.nytarticlesapp.views.StatusView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.statusView)
    StatusView statusView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.newsRecyclerView)
    RecyclerView newsRecyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Inject
    NYTApiClient nyApiClient;
    private NewsRecyclerViewAdapter newsAdapter;
    private NewsSearchListener searchListener;
    private SearchDialog searchDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerInjector.builder().build().inject(this);
        ButterKnife.bind(this);

        statusView.show();

        initGui();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        setupSearchView(searchView);
        searchListener.onQueryTextSubmit(null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_advanced_search:
                if (null == searchDialog) {
                    searchDialog = new SearchDialog();
                }

                searchDialog.show(getSupportFragmentManager(), "search");
                break;
        }

        return true;
    }

    private void setupSearchView(SearchView searchView) {
        searchListener = new NewsSearchListener(this, nyApiClient, new NewsCallback() {
            @Override
            public void before() {
                newsRecyclerView.setVisibility(View.GONE);
                statusView.showLoading();
            }

            @Override
            public void onSuccess(NewsResponse response) {
                updateResults(response);
            }

            @Override
            public void onError(Exception e, String message) {
                updateResults(null);
            }
        });

        searchView.setOnQueryTextListener(searchListener);
    }

    private void initGui() {
        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle(R.string.app_title);
        }

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        recyclerViewLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        newsRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        newsAdapter = new NewsRecyclerViewAdapter(MainActivity.this, null);
        newsRecyclerView.setAdapter(newsAdapter);
    }

    private void updateResults(NewsResponse response) {
        if (null != response && null != response.getResponse() && null != response.getResponse().getNews()
                && response.getResponse().getNews().isEmpty()) {
            newsRecyclerView.setVisibility(View.GONE);
            statusView.showMessage(getResources().getString(R.string.empty_results));
            return;
        } else if (null == response || null == response.getResponse()) {
            newsRecyclerView.setVisibility(View.GONE);
            statusView.showMessage(getResources().getString(R.string.unexpected_error));
            return;
        }

        statusView.hide();
        newsRecyclerView.setVisibility(View.VISIBLE);

        newsAdapter.setNewsList(response.getResponse().getNews());
        newsAdapter.notifyDataSetChanged();
    }
}
