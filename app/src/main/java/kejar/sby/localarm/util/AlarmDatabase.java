package kejar.sby.localarm.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import kejar.sby.localarm.model.Alarm;

/**
 * Created by Irfan Septiadi Putra on 06/05/2016.
 */
public class AlarmDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "localarm.db";
    public static final int DATABASE_VERSION = 1;

    //Atribut Database Alarm
    public static final String TABEL_ALARM = "alarm";
    public static final String ALARM_ID = "id";
    public static final String ALARM_DESTINATION = "destination";
    public static final String ALARM_LATITUDE = "latitude";
    public static final String ALARM_LONGITUDE = "longitude";
    public static final String ALARM_RADIUS = "radius";
    public static final String ALARM_STATUS = "status";

    public AlarmDatabase(Context context){
        super(context,DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_ALARM = "create table "+TABEL_ALARM+" ("+ALARM_ID+" integer primary key autoincrement, " +
                ALARM_DESTINATION+" text," +
                ALARM_LATITUDE+" double," +
                ALARM_LONGITUDE+" double," +
                ALARM_RADIUS+" integer," +
                ALARM_STATUS+" boolean)";
        db.execSQL(CREATE_TABLE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABEL_ALARM);
        onCreate(db);
    }

    public boolean setAlarm(Alarm alarm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ALARM_DESTINATION,alarm.getDestination());
        cv.put(ALARM_LATITUDE,alarm.getLatitude());
        cv.put(ALARM_LONGITUDE,alarm.getLongitude());
        cv.put(ALARM_RADIUS,alarm.getRadius());
        cv.put(ALARM_STATUS,alarm.getStatus());
        try{
            db.insert(TABEL_ALARM,null,cv);
            Log.e("Set Alarm","Berhasil untuk lokasi "+alarm.getDestination());
            return true;
        }catch (Exception e){
            return false;
        }

    }

    public Alarm getAlarm(int id){
        Alarm alarm = new Alarm();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,TABEL_ALARM,null,ALARM_ID+"=?",new String[id],null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            alarm.setId(id);
            alarm.setDestination(cursor.getString(cursor.getColumnIndex(ALARM_DESTINATION)));
            alarm.setLatitude(cursor.getDouble(cursor.getColumnIndex(ALARM_LATITUDE)));
            alarm.setLongitude(cursor.getDouble(cursor.getColumnIndex(ALARM_LONGITUDE)));
            alarm.setRadius(cursor.getInt(cursor.getColumnIndex(ALARM_RADIUS)));
            alarm.setStatus(cursor.getInt(cursor.getColumnIndex(ALARM_STATUS)));
            cursor.moveToNext();
        }
        cursor.close();
        return alarm;
    }

    public ArrayList<Alarm> getListAlarm (){
        ArrayList<Alarm> list = new ArrayList<Alarm>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,TABEL_ALARM,null,null,null,null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Alarm alarm = new Alarm();
            alarm.setId(cursor.getInt(cursor.getColumnIndex(ALARM_ID)));
            alarm.setDestination(cursor.getString(cursor.getColumnIndex(ALARM_DESTINATION)));
            alarm.setLatitude(cursor.getDouble(cursor.getColumnIndex(ALARM_LATITUDE)));
            alarm.setLongitude(cursor.getDouble(cursor.getColumnIndex(ALARM_LONGITUDE)));
            alarm.setRadius(cursor.getInt(cursor.getColumnIndex(ALARM_RADIUS)));
            alarm.setStatus(cursor.getInt(cursor.getColumnIndex(ALARM_STATUS)));
            list.add(alarm);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public ArrayList<String> getListActiveAlarm (){
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+ALARM_DESTINATION+" FROM "+TABEL_ALARM+" WHERE "+ALARM_STATUS+"="+1;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String destination = cursor.getString(cursor.getColumnIndex(ALARM_DESTINATION));
            list.add(destination);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public boolean setStatus(int id,int status){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = { new Integer(status).toString() };
        String query = "UPDATE "+TABEL_ALARM+" SET "+ALARM_STATUS+"="+status+" WHERE "+ALARM_ID+"="+id;
        try{
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            cursor.close();
            Log.e("Update Status","Sukses");
            return true;
        }catch (Exception e){
            Log.e("Update Status","Sukses");
            return false;

        }
    }
}
