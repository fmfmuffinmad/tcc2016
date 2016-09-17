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

    public DBControl(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DBNAME, factory, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        private int pinid;
//        private int ownerid;
//        private Date expireOn;
//        private int privacy;
//        private Date createdOn;
//        private LatLng location;
        String sql = "CREATE TABLE PINUTS (" +
                "PINID INTEGER PRIMARY KEY," +
                "OWNERID INTEGER NOT NULL," +
                "EXPIREON INTEGER," +
                "PRIVACY INTEGER," +
                "CREATEDON INTEGER," +
                "LATITUDE REAL," +
                "LONGITUDE REAL); ";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = null;
//        switch (i){
//            case 1:
//                sql = "ALTER TABLE ALUNOS ADD COLUMN CAMINHOFOTO TEXT";
//                sqLiteDatabase.execSQL(sql);
//        }
    }
}
