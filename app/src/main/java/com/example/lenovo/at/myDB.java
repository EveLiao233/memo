package com.example.lenovo.at;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/12/13.
 */
public class myDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "myDB.db";
    private static final String TABLE_NAME = "Memo";
    private static final int DB_VERSION = 1;
    private static int CATEGORY_THIRD = 2;

    public myDB(Context context, String thing, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, thing, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "create table if not exists " + TABLE_NAME +
                "( [thing] TEXT NOT NULL PRIMARY KEY, " +
                "  [process] INTEGER NOT NULL," +
                "  [start_time] TEXT NOT NULL," +
                "  [end_time] TEXT NOT NULL," +
                "  [category] INTEGER NOT NULL," +
                "  [icon] INTEGER NOT NULL," +
                "  [remarks] TEXT NOT NULL";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //向数据库中添加数据
    public boolean insertOneData(Affair affair) {
        SQLiteDatabase db = getWritableDatabase();
        if (db.query(TABLE_NAME, new String[] {"thing"}, "thing=" + '"' + affair.getThing() +'"',
                null, null, null, null, null).getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put("thing", affair.getThing());
            cv.put("process", affair.getProcess());
            cv.put("start_time", affair.getStart_time());
            cv.put("end_time", affair.getEnd_time());
            cv.put("category", affair.getCategory());
            cv.put("icon", affair.getIcon());
            cv.put("remarks", affair.getRemarks());
            db.insert(TABLE_NAME, null, cv);
            db.close();
            return true;
        } else {
            return false;
        }
    }

    //删除数据库中的数据
    public long deleteOneData(Affair affair) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, "thing=" + '"' + affair.getThing() +'"', null);
    }

    //更新数据库中的数据
    public long updateOneData(Affair affair) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("start_time", affair.getStart_time());
        cv.put("end_time", affair.getEnd_time());
        cv.put("process", affair.getProcess());
        cv.put("icon", affair.getIcon());
        cv.put("remarks", affair.getRemarks());
        long a = db.update(TABLE_NAME, cv, "thing=" + '"' + affair.getThing() +'"', null);
        db.close();
        return a;
    }

    //把数据库的记录加载到对象列表中
    private List<Affair> ConvertToAffair(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;}
        List<Affair> AffairList = new ArrayList<>();
        for (int i = 0; i < resultCounts; i++){
            AffairList.add(new Affair());
            AffairList.get(i).setThing(cursor.getString(cursor.getColumnIndex("thing")));
            AffairList.get(i).setProcess(cursor.getInt(cursor.getColumnIndex("process")));
            AffairList.get(i).setStart_time(cursor.getString(cursor.getColumnIndex("start_time")));
            AffairList.get(i).setEnd_time(cursor.getString(cursor.getColumnIndex("end_time")));
            AffairList.get(i).setCategory(cursor.getInt(cursor.getColumnIndex("category")));
            AffairList.get(i).setIcon(cursor.getInt(cursor.getColumnIndex("icon")));
            AffairList.get(i).setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
            cursor.moveToNext();
        }
        return AffairList;
    }

    //查询数据库中的所有数据
    public List<Affair> getAllData() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor results = db.query(TABLE_NAME, null, null, null, null, null, null,null);
        return ConvertToAffair(results);
    }

    // 查询单个id
    public Cursor getTask(String thing) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME + " where thing = ?", new String[]{thing});
    }
}

