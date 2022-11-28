package com.example.termproject;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DetailContextActivity extends AppCompatActivity {
    private int _id;            //게시글번호
    private int _uid;           //유저번호
    private String _type;      // 0: 공지 1: 게시판
    private String userid, title, content, postdate;

    private TextView titleField;
    private TextView dateField;
    private TextView useridField;
    private TextView contextField;
    private RecyclerView imgLayout;

    sqlHandler handler;
    SQLiteDatabase newDb;
    ArrayList<Uri> uriList = new ArrayList<>();

    MultiImageAdapter adapter;
    LinearLayout layout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_context_activity);

        //게시물 정보 받아오기
        Intent intent = getIntent();
        _uid = intent.getIntExtra("_uid",-1);
        _id = intent.getIntExtra("_id",-1);
        _type = intent.getStringExtra("_type");

        Log.e("viewer: ",Integer.toString(_uid));

        titleField = findViewById(R.id.titleField);
        dateField = findViewById(R.id.dateField);
        contextField = findViewById(R.id.contextField);
        useridField = findViewById(R.id.useridField);
        imgLayout = findViewById(R.id.imgLayout);

        // db 객체 선언
        handler = new sqlHandler(this,"newDb.db",null,1);
        newDb = handler.getReadableDatabase();


    }

    @Override
    protected void onResume() {
        super.onResume();
        uriList.clear();

        getContext();
        getImages();

        adapter = new MultiImageAdapter(uriList, getApplicationContext(),0);
        imgLayout.setAdapter(adapter);
        imgLayout.setLayoutManager(new LinearLayoutManager(this, HORIZONTAL, true));

        titleField.setText(title);
        dateField.setText(postdate);
        contextField.setText(content);
        if(_type.equals("notice"))
            useridField.setText("관리자");
        else
            useridField.setText(userid);
    }

    //게시글 내용을 가져온다.
    public void getContext(){
        String query = "SELECT * FROM " + _type + " WHERE _id = ' " + _id + "';";
        Cursor c = newDb.rawQuery(query,null);

        c.moveToNext();

        userid      = c.getString(c.getColumnIndexOrThrow("userid"));
        title       = c.getString(c.getColumnIndexOrThrow("title"));
        content     = c.getString(c.getColumnIndexOrThrow("content"));
        postdate    = c.getString(c.getColumnIndexOrThrow("postdate"));
    }

    //이미지 URI를 가져온다.
    public void getImages(){
        String query = "SELECT * FROM " + _type +"_img WHERE post_id = ' " + _id + "';";
        Cursor c = newDb.rawQuery(query,null);

        while (c.moveToNext()){
            Uri url = Uri.parse(c.getString(c.getColumnIndexOrThrow("uri")));
            uriList.add(url);
        }

    }

    //작성자아이디로  같은 유저번호 값 인지 확인
    public Boolean getContextAuth(String userid,int _uid, String _type) {
        Log.e("getContextAuth",userid);
        String query = "SELECT * FROM user WHERE userid = '" + userid + "';";
        Cursor c = newDb.rawQuery(query, null);
        c.moveToNext();

        int a = c.getInt(c.getColumnIndexOrThrow("_id"));
        Log.e("getContextAuth",Integer.toString(a));
        if(a == _uid || _type.equals("notice")) return true;
        return false;
    }

    //메뉴를 생성한다

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean visible = getContextAuth(userid,_uid,_type);

        getMenuInflater().inflate(R.menu.detailcontext_menu,menu);
        menu.findItem(R.id.deleteItem).setVisible(visible);
        menu.findItem(R.id.fixItem).setVisible(visible);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteItem:
                deleteContext(_id,_type);
                finish();
                break;
            case R.id.fixItem:
                Intent intent = new Intent(this,WriteActivity.class);
                intent.putExtra("_id",_id);
                intent.putExtra("_uid",_uid);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                intent.putExtra("url",uriList);
                intent.putExtra("type",_type);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteContext(int _id, String _type){
        newDb.beginTransaction();
        try {
            String imgQuery = "DELETE FROM " + _type + "_img WHERE post_id = '"+  _id   +"'";
            newDb.execSQL(imgQuery);
            String contextQuery = "DELETE FROM " + _type + " WHERE _id = '" + _id + "';";
            newDb.execSQL(contextQuery);

            newDb.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            newDb.endTransaction();
        }
    }
}
