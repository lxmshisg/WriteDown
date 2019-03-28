package com.writedown.writedown;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "WriteDown.db";
    public static final String TABLE_NAME = "account_table";
    public static final String COL_1 = "Uuid";
    public static final String COL_2 = "Username";
    public static final String COl_3 = "Password";
    public static final String COL_4 = "Type";
    public static final String COL_5 = "Message";

    public dbHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE account_table (Uuid INTEGER PRIMARY KEY AUTOINCREMENT,Username TEXT UNIQUE,Password TEXT ,Type INTEGER ,Message REAL) ");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+ TABLE_NAME );
        onCreate(db);
    }

    public long addUser(String user,String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Username",user);
        contentValues.put("Password",password);
        long res = db.insert("account_table",null,contentValues);
        db.close();
        return  res;
    }
    public boolean checkUser(String username,String password){
        String[] columns = {COL_1};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?" + "and" + COl_3+ "=?";
        String[] selectionArgs = { username,password };
        Cursor cursor = db.query(TABLE_NAME,columns,selection,selectionArgs,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if(count>0)
            return true;
        else
            return false;
    }

}