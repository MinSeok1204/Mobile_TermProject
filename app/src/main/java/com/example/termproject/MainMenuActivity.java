package com.example.termproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {
    private int _id;

    private ImageButton noticeBtn;
    private ImageButton communityBtn;
    private ImageButton writeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_activity);

        Intent intent = getIntent();
        _id = intent.getIntExtra("_id",-1);

        //버튼 객체 생성
        noticeBtn    = findViewById(R.id.noticeBtn);
        communityBtn = findViewById(R.id.communityBtn);
        writeBtn     = findViewById(R.id.writeBtn);

        /**버튼 이벤트 생성**/

        //글쓰기 버튼
        writeBtn.setOnClickListener(e->{
            Intent writeInt = new Intent(this,WriteActivity.class);
            writeInt.putExtra("_id",_id);
            startActivity(writeInt);
        });

        // 게시판 버튼
        communityBtn.setOnClickListener(e->{
            Intent communityInt = new Intent();
            communityInt.putExtra("_id",_id);
            startActivity(communityInt);
        });

        //공지사항 버튼
        noticeBtn.setOnClickListener(e->{
            Intent noticeInt = new Intent();
            noticeInt.putExtra("_id",_id);
            startActivity(noticeInt);
        });
    }

    /** 메뉴 설정 **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutItem:
                finish();
                break;
            case R.id.settingItem:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
