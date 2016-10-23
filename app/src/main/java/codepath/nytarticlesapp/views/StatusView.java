/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import codepath.nytarticlesapp.R;

public class StatusView extends RelativeLayout {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.statusTextView)
    TextView statusTextView;

    public StatusView(Context context) {
        super(context);
        initView(context);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_status, this, true);

        ButterKnife.bind(this);
    }

    public void showLoading() {
        show();
        progressBar.setVisibility(VISIBLE);
        statusTextView.setText(R.string.loading);
    }

    public void showMessage(String status) {
        show();
        progressBar.setVisibility(INVISIBLE);
        statusTextView.setText(status);
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(View.GONE);
    }
}
