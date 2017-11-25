package com.tareq.admissionaid;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;


public class StoreData extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private int ID = 20000;


    private static final String DATABASE_NAME = "AllData";

    // Contacts table name
    private static final String TABLE = "Data";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "versity_name";
    private static final String KEY_TITLE = "title";
    private static final String KEY_INFO = "info";
    private static final String KEY_DATE = "date";
    private static final String KEY_LINK = "link";


    public StoreData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public StoreData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public StoreData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE + "("
                + KEY_ID + " integer primary key, "
                + KEY_NAME + " text not null, "
                + KEY_TITLE + " text not null, "
                + KEY_INFO + " text not null, "
                + KEY_DATE + " text not null, "
                + KEY_LINK + " text not null"
                + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void storeNewData(final ArrayList<String[]> upData) {


        if (upData == null)
            return;

        new AsyncTask<String, String, String>() {
            private ManagePanes managePanes = new ManagePanes();

            @Override
            protected String doInBackground(String... voids) {
                Log.d("", "");
                managePanes.setCursorToBeg();
                for (int i = upData.size() - 1; i >= 0; i--) {
                    publishProgress(upData.get(i));
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(String... strings) {
                managePanes.addPane(strings[0], strings[1], strings[2], strings[3], strings[4]);
            }
        }.execute();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                SQLiteDatabase db = StoreData.this.getWritableDatabase();

                ID -= upData.size();
                int tmpID = ID;


                Log.d("", "");
                for (String[] it : upData) {

                    ContentValues values = new ContentValues();

                    values.put(KEY_ID, tmpID);

                    tmpID++;

                    values.put(KEY_NAME, it[0]); // Contact Name
                    values.put(KEY_TITLE, it[1]);
                    values.put(KEY_INFO, it[2]);
                    values.put(KEY_DATE, it[3]);
                    values.put(KEY_LINK, it[4]);


                    // Inserting Row
                    db.insert(TABLE, null, values);

                }
                //        db.close();
                return null;
            }
        }.execute();

        // Closing database connection
    }

    public String[] getLastData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE, new String[]{KEY_ID, KEY_NAME, KEY_TITLE, KEY_INFO, KEY_DATE, KEY_LINK}, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            String[] vals = {cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)};
            ID = Integer.parseInt(cursor.getString(0));
            cursor.close();
            //   db.close();
            return vals;
        }
        cursor.close();
        //  db.close();
        return null;

    }

    public void printSaveData() {


        new AsyncTask<String, String, String>() {
            private ManagePanes managePanes = new ManagePanes();

            @Override
            protected String doInBackground(String... strings) {
                managePanes.setCursorToEnd();
                Log.d("", "");
                SQLiteDatabase db = getWritableDatabase();

                String selectQuery = "SELECT * FROM " + TABLE + " ORDER BY " + KEY_ID + " ASC";

                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.getCount() > 0) {
                    try {


                        cursor.moveToFirst();

                        do {
                            publishProgress(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));

                        } while (cursor.moveToNext());
                        //   calc--;


                    } finally {
                        //    db.close();
                        //    cursor.close();
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... strings) {
                managePanes.addPane(strings[0], strings[1], strings[2], strings[3], strings[4]);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
