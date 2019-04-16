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
    public static final String COL_5 = "Filename";
    public static final String COL_6 = "Message";
    private Context context;

    public dbHelper(Context context) {


        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE account_table (Uuid INTEGER PRIMARY KEY AUTOINCREMENT,Username TEXT UNIQUE,Password TEXT ,Type INTEGER ,Filename TEXT, Message BLOB) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public long addUser(String user, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Username", user);
        contentValues.put("Password", password);
        long res = db.insert("account_table", null, contentValues);
        db.close();
        return res;
    }

    public boolean checkUser(String username, String password) {
        String[] columns = {COL_1};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?" + " and " + COl_3 + "=?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if (count > 0)
            return true;
        else
            return false;
    }
    public boolean checkUserR(String username) {
        String[] columns = {COL_1};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if (count > 0)
            return true;
        else
            return false;

    }
    
    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "Uuid = ?",new String[] {id});
    }

    public long addData(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5, item);
        long res = db.insert("account_table", null, contentValues);
        db.close();
        return res;
    }

    public boolean Adam(int type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4,type);
        if(type == 1){
            return true;
        }else

        return false;
    }

    public Cursor getHis(String id){
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor res =db.query(TABLE_NAME,null,COL_1 + "=?",new String[]{id},null,null,null);
        while(res.moveToFirst()) {
            res.getInt(res.getColumnIndex("Uuid"));
            res.getString(res.getColumnIndex("Password"));
        }
            return null;

    }



    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
    public boolean updateData(String id,String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_4,type);
        db.update(TABLE_NAME, contentValues, "Uuid = ?",new String[] { id });
        return true;
    }
    public void saveImage(String item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COL_2, 1);
        cv.put(COL_5, item);//图片转为二进制
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }public boolean ChangePWD(String user, String pwd) {
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,user);
        contentValues.put(COl_3,pwd);
        db.update(TABLE_NAME, contentValues, "Username = ?",new String[] { user });
        return true;
    }    
    public boolean checkUserB(String username, String type) {
        String[] columns = {COL_1};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?" + " and " + COL_4 + "=?";
        String[] selectionArgs = {username,type};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        if (type.equals(1))
            return false;

        else
            return false;

    }


}
