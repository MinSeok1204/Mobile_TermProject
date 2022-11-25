package com.example.termproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class sqlHandler extends SQLiteOpenHelper {
    // 유저 테이블 이름 저장하는 클래스 SQL_member

    public sqlHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // 유저 테이블
        String userQuery = "CREATE TABLE IF NOT EXISTS " + SQL_member.TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "_auth INTEGER DEFAULT '0' NOT NULL, " +
                "userid TEXT NOT NULL," +
                "password TEXT NOT NULL);";
        sqLiteDatabase.execSQL(userQuery);

        //자유 게시판 테이블
        String communityQuery = "CREATE TABLE IF NOT EXISTS community (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT  , " +
                "_uid INTEGER NOT NULL, " +
                "title TEXT    NOT NULL, " +
                "content TEXT    NOT NULL, " +
                "postdate DATE    NOT NULL," +
                "FOREIGN KEY(_uid) REFERENCES user (_id));";
        sqLiteDatabase.execSQL(communityQuery);

        //자유 게시판 - 이미지 테이블 (게시글 번호 _id, 이미지 uri)
        String communityImgQuery = "CREATE TABLE IF NOT EXISTS community_img (" +
                "_id INTEGER PRIMARY  KEY AUTOINCREMENT, " +
                "post_id INTEGER, " +
                "uri TEXT, "+
                "FOREIGN KEY(post_id) REFERENCES community(_id));";
        sqLiteDatabase.execSQL(communityImgQuery);

        //공지 테이블
        String noticeQuery = "CREATE TABLE IF NOT EXISTS notice (" +
                "_id INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                "_uid INTEGER NOT NULL, " +
                "title TEXT    NOT NULL, " +
                "content TEXT    NOT NULL, " +
                "postdate DATE    NOT NULL," +
                "FOREIGN KEY(_uid) REFERENCES user (_id));";
        sqLiteDatabase.execSQL(noticeQuery);

        //공지사항 - 이미지 테이블 (게시글 번호 _id, 이미지 uri)
        String noticeImgQuery = "CREATE TABLE IF NOT EXISTS notice_img (" +
                "_id INTEGER NOT NULL PRIMARY  KEY, " +
                "post_id INTEGER, " +
                "uri TEXT, "+
                "FOREIGN KEY(post_id) REFERENCES notice(_id));";
        sqLiteDatabase.execSQL(noticeImgQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String drop_query = "drop table " + SQL_member.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop_query);

        onCreate(sqLiteDatabase);
    }
}
