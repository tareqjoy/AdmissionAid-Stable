package com.tareq.admissionaid;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.Manifest;

import static com.tareq.admissionaid.MainActivity.myDir;

public class DownloadFile extends AsyncTask<Void, Integer, String> {
    private NotificationCompat.Builder mBuilder;
    private Pane pane;
    private Context context;
    private String fl_name;
    private static int mId = 0;
    private NotificationManager mNotifyManager;
    public int Cprogress = 0;
    int d;

    public DownloadFile(Pane pane) {
        this.pane = pane;
        context = MainActivity.context;
        d = context.getResources().getIdentifier(pane.VersityName, "drawable", context.getPackageName());
        mId++;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for ( ; ; ) {
                if (MainActivity.appCompatActivity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                   break;
                } else {
                    ActivityCompat.requestPermissions(MainActivity.appCompatActivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

            }
        }



        initNotification();
        setStartedNotification();
        Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show();
        myDir.mkdir();
    }

    @Override
    protected String doInBackground(Void... Url) {
        try {

            URL url = new URL(pane.Link);
            URLConnection connection = url.openConnection();
            connection.connect();
            int fileLength = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream());
            File file = new File(myDir, pane.FileName);
            fl_name = pane.FileName;
            if (file.exists()) file.delete();
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            return "f";
        }
        return "";
    }

    private void initNotification() {
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        int incr = progress[0].intValue();
        Cprogress = incr;
        if (incr == 0)
            setProgressNotification();
        updateProgressNotification(incr);
    }

    @Override
    protected void onPostExecute(String str) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onPostExecute(str);
        if (str.equals("f")) {
            final Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mBuilder.setSmallIcon(d).setContentTitle(pane.Title).setContentText("Failed to download");
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
            mBuilder.setAutoCancel(true);
            mNotifyManager.notify(mId, mBuilder.build());
        } else {
            setCompletedNotification();
        }
    }

    private void setCompletedNotification() {
        mBuilder.setSmallIcon(d).setContentTitle(pane.Title).setContentText("Completed");

        Intent target = new Intent(Intent.ACTION_QUICK_VIEW);
        String inte = "";

        File file = new File(myDir, fl_name);
        target.setDataAndType(Uri.fromFile(file), inte);
        Intent intent = Intent.createChooser(target, "Open File");


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.setAction(Intent.ACTION_MAIN);
        intent2.addCategory(Intent.CATEGORY_LAUNCHER);
        stackBuilder.addNextIntent(intent2);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        mBuilder.setAutoCancel(true);
        mNotifyManager.notify(mId, mBuilder.build());
    }

    private void setProgressNotification() {
        mBuilder.setContentTitle(pane.Title).setContentText("Downloading...").setSmallIcon(d);
    }

    private void setStartedNotification() {
        mBuilder.setSmallIcon(d).setContentTitle(pane.Title).setContentText("Connecting....");
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        mNotifyManager.notify(mId, mBuilder.build());
    }


    private void updateProgressNotification(int incr) {
        mBuilder.setProgress(100, incr, false);
        Cprogress = incr;
        mNotifyManager.notify(mId, mBuilder.build());
    }

}
