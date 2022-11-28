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
        String userQuery = "CREATE TABLE IF NOT EXISTS user (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "_auth INTEGER DEFAULT '0' NOT NULL, " +
                "userid TEXT NOT NULL," +
                "password TEXT NOT NULL);";
        sqLiteDatabase.execSQL(userQuery);

        //자유 게시판 테이블
        String communityQuery = "CREATE TABLE IF NOT EXISTS community (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT  , " +
                "userid TEXT NOT NULL, " +
                "title TEXT    NOT NULL, " +
                "content TEXT    NOT NULL, " +
                "postdate DATE    NOT NULL," +
                "FOREIGN KEY(userid) REFERENCES user (userid));";
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
                "userid TEXT NOT NULL, " +
                "title TEXT    NOT NULL, " +
                "content TEXT    NOT NULL, " +
                "postdate DATE    NOT NULL," +
                "FOREIGN KEY(userid) REFERENCES user (userid));";
        sqLiteDatabase.execSQL(noticeQuery);

        //공지사항 - 이미지 테이블 (게시글 번호 _id, 이미지 uri)
        String noticeImgQuery = "CREATE TABLE IF NOT EXISTS notice_img (" +
                "_id INTEGER NOT NULL PRIMARY  KEY, " +
                "post_id INTEGER, " +
                "uri TEXT, "+
                "FOREIGN KEY(post_id) REFERENCES notice(_id));";
        sqLiteDatabase.execSQL(noticeImgQuery);

        //취업정보
        String jobInfoQuery = "CREATE TABLE jobinfo (" +
                "_id INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT    NOT NULL); ";
        sqLiteDatabase.execSQL(noticeQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String drop_user_query = "drop table user;";
        sqLiteDatabase.execSQL(drop_user_query);

        String drop_community_query = "drop table community;";
        sqLiteDatabase.execSQL(drop_community_query);

        String drop_communityImg_query = "drop table community_img;";
        sqLiteDatabase.execSQL(drop_communityImg_query);

        String drop_notice_query = "drop table notice;";
        sqLiteDatabase.execSQL(drop_notice_query);

        String drop_noticeImg_query = "drop table notice_img;";
        sqLiteDatabase.execSQL(drop_noticeImg_query);

        String drop_jobinfo_query = "drop table jobinfo;";
        sqLiteDatabase.execSQL(drop_noticeImg_query);

        onCreate(sqLiteDatabase);
    }
}
