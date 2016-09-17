package com.tcc.tccpinut.tccpinut.DAOs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by muffinmad on 17/09/2016.
 */
public class PinutDAO extends DBControl{

    public PinutDAO(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }



}


