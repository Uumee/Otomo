package jp.live2d.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fuyuk on 16/2/15.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    static final String DB = "sqlite_schedule.db";
    static final int DV_VERSION = 1;
    static final String CREATE_TABLE = "create table mytable( _id integer primary key autoincrement, time text not null, title text not null, memo text not null );";
    static final String DROP_TABLE = "drop table mytable";

    public MySQLiteOpenHelper(Context c){
        super(c, DB, null, DV_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void addSchedule(SQLiteDatabase db, int year, int month, int day, int hour, int minute, String title, String memo){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time",""+year+"/"+month+"/"+day+"　"+String.format("%02d",hour)+":"+String.format("%02d",minute));
        values.put("title",""+title);
        values.put("memo",""+memo);
        db.insert("mytable", null, values);

        db.close();
    }

}