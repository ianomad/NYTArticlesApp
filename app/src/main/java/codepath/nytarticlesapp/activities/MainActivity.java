package codepath.nytarticlesapp.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import org.parceler.Parcels;

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
import codepath.nytarticlesapp.network.SearchRequest;
import codepath.nytarticlesapp.utils.Constants;
import codepath.nytarticlesapp.utils.EndlessRecyclerViewScrollListener;
import codepath.nytarticlesapp.utils.NewsSearchListener;
import codepath.nytarticlesapp.utils.SearchBroadCastReceiver;
import codepath.nytarticlesapp.views.StatusView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.statusView)
    StatusView statusView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.newsRecyclerView)
    RecyclerView newsRecyclerView;

    StaggeredGridLayoutManager recyclerViewLayoutManager;

    @Inject
    NYTApiClient nyApiClient;

    private NewsRecyclerViewAdapter newsAdapter;
    private SearchDialog searchDialog;
    private SearchBroadCastReceiver mMessageReceiver;
    private SearchRequest lastSearchRequest;
    private EndlessRecyclerViewScrollListener loadMoreScrollListener;

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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        NewsSearchListener searchListener = new NewsSearchListener(this, searchView);
        searchView.setOnQueryTextListener(searchListener);
        searchListener.onQueryTextSubmit(null);
        return true;
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.SEARCH_REQUEST_CREATED));
        super.onResume();
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

        loadMoreScrollListener = new EndlessRecyclerViewScrollListener(recyclerViewLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreNews(page);
            }
        };
        newsRecyclerView.addOnScrollListener(loadMoreScrollListener);

        NewsCallback newsCallback = new NewsCallback() {
            @Override
            public void before(SearchRequest searchRequest) {
                if (searchRequest.getPage() == 1) {
                    newsAdapter.clearData();
                    newsAdapter.notifyDataSetChanged();
                    loadMoreScrollListener.resetState();
                    newsRecyclerView.setVisibility(View.GONE);
                    statusView.showLoading();
                }
            }

            @Override
            public void onSuccess(NewsResponse response, SearchRequest searchRequest) {
                lastSearchRequest = searchRequest;
                updateResults(response, searchRequest);
            }

            @Override
            public void onError(Exception e, String message, SearchRequest searchRequest) {
                lastSearchRequest = searchRequest;
                updateResults(null, searchRequest);
            }
        };

        mMessageReceiver = new SearchBroadCastReceiver(this, nyApiClient, newsCallback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_advanced_search:
                showAdvancedDialog();
                break;
        }

        return true;
    }

    private void showAdvancedDialog() {
        if (null == searchDialog) {
            searchDialog = new SearchDialog();
        }

        searchDialog.show(getSupportFragmentManager(), "search");
    }

    private void updateResults(NewsResponse response, SearchRequest searchRequest) {
        boolean secondaryPage = searchRequest.getPage() != 1;
        if (null != response && null != response.getResponse() && null != response.getResponse().getNews() && response.getResponse().getNews().isEmpty()) {
            if (secondaryPage) {
                return;
            }

            newsRecyclerView.setVisibility(View.GONE);
            statusView.showMessage(getResources().getString(R.string.empty_results));
            return;
        } else if (null == response || null == response.getResponse()) {
            if (secondaryPage) {
                return;
            }

            newsRecyclerView.setVisibility(View.GONE);
            statusView.showMessage(getResources().getString(R.string.unexpected_error));
            return;
        }

        statusView.hide();
        newsRecyclerView.setVisibility(View.VISIBLE);
        if (newsAdapter.getItemCount() == 0) {
            newsAdapter.setNewsList(response.getResponse().getNews());
        } else {
            newsAdapter.addPage(response.getResponse().getNews());
        }


        newsAdapter.notifyDataSetChanged();
    }

    private void loadMoreNews(int nextPage) {
        lastSearchRequest.setPage(nextPage + 1);
        Intent searchIntent = new Intent(Constants.SEARCH_REQUEST_CREATED);
        searchIntent.putExtra("data", Parcels.wrap(lastSearchRequest));
        LocalBroadcastManager.getInstance(this).sendBroadcast(searchIntent);
    }
}
