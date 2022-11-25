package com.example.termproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class sqlHandler extends SQLiteOpenHelper {
    private SQL_member member; // 유저 테이블 이름 저장하는 클래스 SQL_member

    public sqlHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // 유저 데이터 베이스
        String userQuery = "CREATE TABLE IF NOT EXISTS " + member.TABLE_NAME + " (" +
                member._id + " INTEGER AUTO INCREMENT PRIMARY KEY ," +
                member.USER_ID + " TEXT NOT NULL," +
                member.Password + " TEXT NOT NULL);";
        
        sqLiteDatabase.execSQL(userQuery);

        String communityQuery = "CREATE TABLE IF NOT EXISTS community (" +
                "_id INTEGER AUTO INCREMENT PRIMARY KEY , " +
                "_uid INTEGER NOT NULL, " +
                "title TEXT    NOT NULL, " +
                "content TEXT    NOT NULL, " +
                "postdate DATE    NOT NULL," +
                "FOREIGN KEY(_uid) REFERENCES user (_id));";
        sqLiteDatabase.execSQL(communityQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String drop_query = "drop table " + member.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop_query);

        onCreate(sqLiteDatabase);
    }
}
