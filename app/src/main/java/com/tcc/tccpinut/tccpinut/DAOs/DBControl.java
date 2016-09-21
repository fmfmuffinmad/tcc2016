package com.tcc.tccpinut.tccpinut.DAOs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by muffinmad on 17/09/2016.
 */
public class DBControl extends SQLiteOpenHelper {

    public static final int DBVERSION = 1;
    public static final String DBNAME = "PinutDB";

    public DBControl(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql =
                        //tabela PINUTS
                        "CREATE TABLE PINUTS (" +
                        "LOCALID INTEGER PRIMARY KEY," +
                        "PINID INTEGER," +
                        "OWNERID INTEGER," +
                        "EXPIREON INTEGER," +
                        "PRIVACY INTEGER," +
                        "CREATEDON INTEGER," +
                        "LATITUDE DOUBLE," +
                        "LONGITUDE DOUBLE," +
                        "IMAGEPATH TEXT," +
                        "AUDIOPATH TEXT," +
                        "TITLE TEXT," +
                        "PTEXT TEXT); " +
                        //tabela Amigos
                        "CREATE TABLE AMIGOS (" +
                        "ID INTEGER PRIMARY KEY," +
                        "USERNAME STRING," +
                        "PHONE STRING);";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS PINUTS;" +
                "DROP TABLE IF EXISTS AMIGOS";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
//        switch (i){
//            case 1:
//                sql = "ALTER TABLE ALUNOS ADD COLUMN CAMINHOFOTO TEXT";
//                sqLiteDatabase.execSQL(sql);
//        }
    }
}
