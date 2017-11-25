package com.tareq.admissionaid;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tareq.admissionaid.PaneAdapter.paneAdapter;
import static com.tareq.admissionaid.PaneAdapter.paneList;
import static com.tareq.admissionaid.PaneAdapter.tempList;

public class MainActivity extends AppCompatActivity {

    public static Toolbar toolbar;
    public static TextView header;
    public static DrawerLayout drawer;
    private NavigationView navbardrawer;
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static AppCompatActivity appCompatActivity;
    public static String filter = " all ", andfilter = "";
    public static StoreData storeData;
    public static RecyclerView MainLayout;
    public static RelativeLayout OuterLayout;
    public static Context context;
    public static Web web;
    public static Menu toolbarMenu;
    public final static File myDir = new File(Environment.getExternalStorageDirectory() + "/AdmissionAid");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        context = this;
        appCompatActivity = this;
        storeData = new StoreData(this);


        MainActivity.this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);


        InitializeMainLayout();

        OuterLayout = (RelativeLayout) findViewById(R.id.outer_layout);

        toolbar = findViewById(R.id.my_toolbar);
        header = findViewById(R.id.header);

        //  scrollView = findViewById(R.id.scrollview);

        swipeRefreshLayout = findViewById(R.id.refresh_layout);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navbardrawer = (NavigationView) findViewById(R.id.nav_view);

        web = new Web(MainActivity.this);

        InitializeNavDrawer();
        InitializeToolbar();
        //    InitializeScrollView();
        InitializeRefreshView();

        swipeRefreshLayout.setRefreshing(true);
        new DownloadJSON().execute();
        storeData.printSaveData();
        //    paneAdapter.notifyDataSetChanged();

    }


    private void InitializeToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setTag(0);
        toolbar.setNavigationIcon(R.drawable.menu);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (toolbar.getTag().equals(0))
                    drawer.openDrawer(Gravity.START);
                else if (toolbar.getTag().equals(1)) {
                    web.deInitializeToolbar();
                }

            }
        });
    }

    private void InitializeMainLayout() {
        MainLayout = findViewById(R.id.MainLayout);
        paneAdapter = new PaneAdapter();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        MainLayout.setLayoutManager(layoutManager);
        MainLayout.setItemAnimator(new DefaultItemAnimator());
        MainLayout.setAdapter(paneAdapter);
    }

    private void InitializeNavDrawer() {

        navbardrawer.bringToFront();
        navbardrawer.getMenu().getItem(0).setChecked(true);
        header.setText("All");

        navbardrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                navbardrawer.setCheckedItem(item.getItemId());
                header.setText(item.getTitle());
                drawer.closeDrawer(Gravity.LEFT);


                paneList.clear();
                if (tempList.size() > 0)
                    for (Pane p : tempList)
                        paneList.add(new Pane(p));

                switch (item.getItemId()) {
                    case R.id.nav_all: {
                        filter = " all ";
                        break;
                    }
                    case R.id.nav_eng: {
                        filter = " buet kuet ruet cuet butex duet ";
                        break;
                    }
                    case R.id.nav_gen: {
                        filter = " bru bu bup cou cu du iu jkkniu jnu ju ku nu ru ";
                        break;
                    }
                    case R.id.nav_agri: {
                        filter = " bau bsmrau cvasu sbau stau ";
                        break;
                    }
                    case R.id.nav_scn: {
                        filter = " bsmrstu hstu just mbstu nstu pstu pust rmstu sust ";
                        break;
                    }
                    case R.id.nav_cus: {

                        break;
                    }
                    case R.id.nav_abt: {

                        break;
                    }
                    case R.id.nav_settings: {

                        break;
                    }
                }
                ManagePanes.manageAll();


                return false;
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        toolbarMenu = menu;
        MenuItem item = toolbarMenu.findItem(R.id.tool_sear);
        final SearchView searchView = (android.support.v7.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    andfilter = "";
                    PaneAdapter.filterBy(newText);
                } else {
                    PaneAdapter.filterBy(newText);
                }
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                andfilter = "";
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return false;
    }

    private void InitializeRefreshView() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new DownloadJSON().execute();
            }
        });

    }


}
