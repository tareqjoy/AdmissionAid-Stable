package com.tareq.admissionaid;


import android.os.Handler;
import android.widget.RelativeLayout;

import static com.tareq.admissionaid.PaneAdapter.paneAdapter;
import static com.tareq.admissionaid.PaneAdapter.paneList;
import static com.tareq.admissionaid.PaneAdapter.tempList;
import static com.tareq.admissionaid.MainActivity.filter;
import static com.tareq.admissionaid.MainActivity.andfilter;

public class ManagePanes {
    private RelativeLayout.LayoutParams layoutParams;
    private int CursorState = 0;  //


    public void addPane(String VersityName, String Title, String Info, String Time, String Link) {
        if (CursorState == 0) {
            addPaneUsual(VersityName, Title, Info, Time, Link);
        } else {
            addPaneNotUsual(VersityName, Title, Info, Time, Link);
        }
    }

    private void addPaneUsual(String VersityName, String Title, String Info, String Time, String Link) {
        Pane pane = new Pane(VersityName, Title, Info, Time, Link);
        paneList.add(pane);
        tempList.add(pane);
        paneAdapter.notifyDataSetChanged();
    }

    private void addPaneNotUsual(String VersityName, String Title, String Info, String Time, String Link) {
        Pane pane = new Pane(VersityName, Title, Info, Time, Link);
        if (MainActivity.filter.contains(" " + VersityName.toLowerCase() + " ") || MainActivity.filter.equals(" all ")) {
            paneList.add(0, pane);
            paneAdapter.notifyDataSetChanged();
        }
        tempList.add(0, pane);
    }


    public void setCursorToBeg() {
        CursorState = 1;
    }

    public void setCursorToEnd() {
        CursorState = 0;
    }

    public static void manageAll() {

        

        for (int k = 0; k < paneList.size(); ) {
            Pane p = paneList.get(k);
            if (!filter.contains(" " + p.VersityName.toLowerCase() + " ") && !filter.equals(" all ") || !(p.VersityName.toLowerCase().contains(andfilter) || p.Title.toLowerCase().contains(andfilter) || p.Info.toLowerCase().contains(andfilter))) {
                paneList.remove(k);
                paneAdapter.notifyItemRemoved(k);

            } else {
                k++;
            }

        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                paneAdapter.notifyDataSetChanged();
            }
        }, 500);


    }


}
