package com.tareq.admissionaid;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Web extends RelativeLayout implements Animation.AnimationListener {
    private RelativeLayout backMain;
    private Context context;
    private WebView webView;
    private ImageView backButton, forwardButton, refreshButton, crossButton;
    private Animation anim;
    private MenuItem item;
    private String prevHeaderText;
    private ProgressBar webProgress;

    public Web(Context context) {
        super(context);
        this.context = context;

        Initialize();
    }

    public void Initialize() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        inflater.inflate(R.layout.web, this);
        backButton = findViewById(R.id.backButton);
        forwardButton = findViewById(R.id.forwardButton);
        refreshButton = findViewById(R.id.refreshButton);
        crossButton = findViewById(R.id.crossButton);
        backMain = findViewById(R.id.webBackLayout);
        webView = findViewById(R.id.weview);
        webProgress = findViewById(R.id.webProgress);


        setUpWebView();
        setUpTools();


    }

    private void setUpTools()
    {
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                webView.goBack();

            }
        });

        forwardButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                webView.goForward();


            }
        });

        refreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl(webView.getUrl());
            }
        });

        crossButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.stopLoading();
            }
        });
    }

    private void setUpWebView() {

        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setSupportZoom(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient());


        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                webProgress.setProgress(progress);


            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                crossButton.setVisibility(View.GONE);
                webProgress.setVisibility(GONE);
                refreshButton.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                crossButton.setVisibility(View.VISIBLE);
                webProgress.setVisibility(VISIBLE);
                refreshButton.setVisibility(View.GONE);
                view.loadUrl(url);
                return true;
            }
        });
    }

    public void showWeb(String Url) {



        crossButton.setVisibility(View.VISIBLE);
        refreshButton.setVisibility(View.GONE);
        webProgress.setVisibility(VISIBLE);

        prevHeaderText = (String) MainActivity.header.getText();

        StringBuilder webHeaderGen = new StringBuilder();
        for (int i = 7; i < Url.length(); i++) {
            if (Url.charAt(i) == '/')
                break;
            webHeaderGen.append(Url.charAt(i));
        }

        MainActivity.header.setText(webHeaderGen);


        webView.loadUrl(Url);

        this.setVisibility(INVISIBLE);
        MainActivity.OuterLayout.addView(this);

        anim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slide0_to_100);
        anim.setAnimationListener(this);
        backMain.startAnimation(anim);


        MainActivity.MainLayout.startAnimation(anim);

        this.setVisibility(VISIBLE);
        InitializeToolbar();
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }


    @Override
    public void onAnimationEnd(Animation animation) {


    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void InitializeToolbar() {
        MainActivity.MainLayout.setVisibility(GONE);
        MainActivity.toolbar.setNavigationIcon(R.drawable.back_ico_white);
        MainActivity.toolbar.setTag(1);
        item = MainActivity.toolbarMenu.findItem(R.id.tool_sear);
        item.setVisible(false);
        item = MainActivity.toolbarMenu.findItem(R.id.tool_open_in_browser);
        item.setVisible(true);

        MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void deInitializeToolbar() {
        this.setVisibility(GONE);
        MainActivity.MainLayout.setVisibility(VISIBLE);
        anim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slide100_0);
        anim.setAnimationListener(this);
        backMain.startAnimation(anim);


        MainActivity.toolbar.setNavigationIcon(R.drawable.menu);
        MainActivity.toolbar.setTag(0);
        item = MainActivity.toolbarMenu.findItem(R.id.tool_sear);
        item.setVisible(true);
        item = MainActivity.toolbarMenu.findItem(R.id.tool_open_in_browser);
        item.setVisible(false);

        MainActivity.MainLayout.startAnimation(anim);

        MainActivity.OuterLayout.removeView(this);

        MainActivity.header.setText(prevHeaderText);

        MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        webView.loadUrl("about:blank");
        webProgress.setProgress(0);


    }
}
