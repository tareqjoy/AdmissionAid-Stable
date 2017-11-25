package com.tareq.admissionaid;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class DownloadJSON extends AsyncTask<Void, String, ArrayList<String[]>> {

    private final String urlStr = "https://www.dropbox.com/s/oo18yf42bb8ojg1/Updates.txt?dl=1";
    private String[] lastData;
    private  ManagePanes managePanes;

    @Override
    protected void onPreExecute() {
        managePanes=new ManagePanes();

    }

    @Override
    protected ArrayList<String[]> doInBackground(Void... voids) {

        Log.d("","");
        ArrayList<String[]> arrData = new ArrayList<String[]>();
        try {

            managePanes.setCursorToBeg();
            lastData = MainActivity.storeData.getLastData();


            URL url = new URL(urlStr);  //string converted to URL type
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();  //as HTTPS
            connection.connect();   //connecting to the server

            InputStream stream = connection.getInputStream();  //downloading the file

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));  //store the read data from server line by line to memory

            StringBuffer StrBfr = new StringBuffer();  //store the value as String
            String line = "";

            while ((line = reader.readLine()) != null)  //reading the file line by line
            {
                StrBfr.append(line + "\n");   //adding the line to Stringbuffer
            }

            String jsonStr = StrBfr.toString();   //convert Stringbuffer to pure String

            JSONArray JArr = new JSONArray(jsonStr);  // [] indicates a jason object


            for (int i = 0; i < JArr.length(); i++) {
                JSONObject IndividualUpdts = JArr.getJSONObject(i);
                String name = IndividualUpdts.getString("name");
                String title = IndividualUpdts.getString("title");
                String link = IndividualUpdts.getString("link");
                String info = IndividualUpdts.getString("info");
                String time = IndividualUpdts.getString("time");
                if (lastData != null) {
                    if (name.equals(lastData[0]) && title.equals(lastData[1]) && link.equals(lastData[4])) {
                        return arrData;
                    }
                }

                arrData.add(new String[]{name, title, info, time, link});

                //  MainActivity.storeData.addNewData(name,title,info,time,link);
               // publishProgress(name, title, info, time, link);
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

      //  return null;
        return arrData;
    }

    @Override
    protected void onProgressUpdate(String... strings) {

      //  managePanes.addPane(strings[0], strings[1], strings[2], strings[3], strings[4]);
    }

    @Override
    protected void onPostExecute(ArrayList<String[]> result) {

        MainActivity.swipeRefreshLayout.setRefreshing(false);
        MainActivity.storeData.storeNewData(result);

    }
}
