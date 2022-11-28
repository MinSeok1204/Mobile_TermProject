package com.example.termproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CommunityActivity extends AppCompatActivity {
    private int _id;            // 유저코드
    private int _auth;          // 권한코드 0: default user || 2022: admin

    private Button writeBtn;
    private RecyclerView communityView;
    private RecyclerView noticeField;
    ContextAdapter adapter;
    SQLiteDatabase newDB;
    sqlHandler sqlHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_activity);

        //메인 엑티비티에서 값 가져오기
        Intent intent = getIntent();
        _id = intent.getIntExtra("_id",-1);
        _auth = intent.getIntExtra("_auth",0);

        sqlHandler = new sqlHandler(this,"newDb.db",null,1);
        newDB = sqlHandler.getReadableDatabase();
        sqlHandler.onCreate(newDB);

        //버튼 객체 생성
        writeBtn        = findViewById(R.id.writeBtn);
        communityView   = findViewById(R.id.communityField);
        noticeField     = findViewById(R.id.noticeField);
        /**버튼 이벤트 생성**/

        //글쓰기 버튼
        writeBtn.setOnClickListener(e->{
            Intent writeInt = new Intent(this,WriteActivity.class);
            writeInt.putExtra("_id",_id);
            writeInt.putExtra("_auth",_auth);

            Log.e("_auth",Integer.toString(_auth));

            startActivity(writeInt);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContext();
    }

    private void loadContext(){
        String communityQuery = "SELECT _id,title,userid FROM community;";
        Cursor c = newDB.rawQuery(communityQuery,null);

        adapter = new ContextAdapter(c, getApplicationContext(),0,_id);
        communityView.setAdapter(adapter);
        communityView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));

        String noticeQuery = "SELECT _id,title,userid FROM notice;";
        Cursor d = newDB.rawQuery(noticeQuery,null);

        adapter = new ContextAdapter(d, getApplicationContext(),1,_id);
        noticeField.setAdapter(adapter);
        noticeField.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));
    }
}
