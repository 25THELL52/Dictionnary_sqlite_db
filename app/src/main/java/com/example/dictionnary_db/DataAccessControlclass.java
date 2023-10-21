package com.example.dictionnary_db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.EventLogTags;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class DataAccessControlclass extends SQLiteOpenHelper {


    private SQLiteDatabase myDataBase;
    public static String DATABASE_PATH = "";
    private final Context context;
    public boolean isword = false;

    public DataAccessControlclass(Context context) {
        super(context, "EDMTDictionary.db", null, 1);
        DATABASE_PATH = context.getDatabasePath("EDMTDictionary.db").toString();
        Log.i("path", DATABASE_PATH);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createDatabase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            Log.v("DB Exists", "db exists");

        }
        //boolean dbExist1 = checkDataBase();

        //if(!dbExist1)
        else if (!dbExist) {
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }


    //Check database already exist or not
    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            String myPath = DATABASE_PATH;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        } catch (SQLiteException e) {
        }
        return checkDB;
    }

    private void copyDataBase() throws IOException {
        String outFileName = DATABASE_PATH;
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = context.getAssets().open("EDMTDictionary.db");
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    public void db_delete() {
        File file = new File(DATABASE_PATH);
        if (file.exists()) {
            file.delete();
            System.out.println("delete database file.");
        }
    }

    //Open database
    public void openDatabase() throws SQLException {
        String myPath = DATABASE_PATH;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase() throws SQLException {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }


    public List<String> find(String word) {


        int id = 0;
        List<String> listofdefs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        String Des = "";


        Cursor cr;

        String query2 = " SELECT Description from Word WHERE Word = \"" + word + "\" COLLATE NOCASE;";
        cr = db.rawQuery(query2, null);
        if (cr.moveToFirst()) {
            Log.i("test", "cr.moveToFirst 2");
            do {

                Des = cr.getString(cr.getColumnIndex("Description"));
                listofdefs.add(Des);
            }

            while (cr.moveToNext());

        } else {
            String query3 = " SELECT Word, ID_ALPHABET from Word WHERE Word LIKE '%" + word + "%' COLLATE NOCASE;";
            Log.i("test", "cr.moveToFirst()3");

            cr = db.rawQuery(query3, null);
            if (cr.moveToFirst()) {
                listofdefs.add("DID YOU MEAN ?");

                do {

                    Des = cr.getString(cr.getColumnIndex("Word"));
                    String ID = cr.getString(cr.getColumnIndex("ID_ALPHABET"));
                    Log.i("test", "ID OF JJINN Is :" + ID);

                    listofdefs.add(Des);
                }
                while (cr.moveToNext());
                this.isword = true;
            } else listofdefs.add("WORD NOT FOUND");


        }
        cr.close();
        db.close();
        Log.i("test", listofdefs.toString());

        return listofdefs;


    }


}
