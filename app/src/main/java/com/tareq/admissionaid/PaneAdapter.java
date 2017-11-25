package com.tareq.admissionaid;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static com.tareq.admissionaid.MainActivity.context;

public class PaneAdapter extends RecyclerView.Adapter<PaneAdapter.MyViewHolder> {
    private int lastPosition = -1;
    private DownloadFile downloadFile;
    public static List<Pane> paneList = new ArrayList<>(), tempList = new ArrayList<>();
    public static PaneAdapter paneAdapter;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RelativeLayout container;
        public TextView title, time, vername, info;
        public ImageView ver_logo, status;

        public MyViewHolder(View view) {

            super(view);
            container = (RelativeLayout) view.findViewById(R.id.pane);
            title = (TextView) view.findViewById(R.id.title);
            time = (TextView) view.findViewById(R.id.time);
            vername = (TextView) view.findViewById(R.id.vername);
            info = (TextView) view.findViewById(R.id.info);
            ver_logo = (ImageView) view.findViewById(R.id.ver_logo);
            status = (ImageView) view.findViewById(R.id.status);
            view.setOnClickListener(this);

        }


        public void setTag(Pane pane) {
            container.setTag(pane);
        }


        @Override
        public void onClick(View view) {

            int ChkSt;

            final Pane pane = (Pane) view.getTag();

            if (pane.Downloadable) {

                File file = new File(MainActivity.myDir, pane.FileName);

                if (file.exists()) {
                    status.setBackgroundResource(R.drawable.sign);

                    ChkSt = 0;
                } else {
                    status.setBackgroundResource(R.drawable.download);
                    ChkSt = 1;
                }
            } else {
                status.setBackgroundResource(R.drawable.browse);
                ChkSt = 2;
            }


            if (ChkSt == 0) {
                Intent target = new Intent(Intent.ACTION_QUICK_VIEW);

                target.setDataAndType(Uri.fromFile(new File(MainActivity.myDir, pane.FileName)), pane.IntentName);
                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {


                }
            } else if (ChkSt == 1) {

                if (downloadFile == null) {
                    downloadFile = new DownloadFile((Pane) view.getTag());
                    downloadFile.execute();

                } else if (downloadFile.getStatus() == AsyncTask.Status.FINISHED) {
                    downloadFile = new DownloadFile((Pane) view.getTag());
                    downloadFile.execute();

                }
            } else if (ChkSt == 2) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.web.showWeb(pane.Link);
                    }
                }, 400);

            }
        }
    }


    public static void filterBy(String str) {

        paneList.clear();
        for (Pane p : tempList)
            paneList.add(new Pane(p));

        MainActivity.andfilter = str;
        ManagePanes.manageAll();

    }


    @Override
    public PaneAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pane, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Pane pane = paneList.get(position);

        holder.title.setText(pane.Title);
        holder.info.setText(pane.Info);
        holder.time.setText(pane.Time);
        holder.vername.setText(pane.VersityName);
        try {
            holder.ver_logo.setImageDrawable(MainActivity.appCompatActivity.getResources().getDrawable(MainActivity.appCompatActivity.getResources().getIdentifier("@drawable/" + pane.VersityName, null, context.getPackageName())));
        } catch (Exception e) {

        }
        if (pane.Downloadable) {

            File file = new File(MainActivity.myDir, pane.FileName);
            if (file.exists()) {
                holder.status.setBackgroundResource(R.drawable.sign);

            } else {
                holder.status.setBackgroundResource(R.drawable.download);
            }
        } else {
            holder.status.setBackgroundResource(R.drawable.browse);

        }

        holder.setTag(pane);

        setAnimation(holder.container, position);
    }


    @Override
    public int getItemCount() {
        return paneList.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.pane_anim);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
