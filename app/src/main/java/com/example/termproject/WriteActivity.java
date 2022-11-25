package com.example.termproject;

import static android.widget.Toast.LENGTH_SHORT;

import static androidx.recyclerview.widget.LinearLayoutManager.*;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WriteActivity extends AppCompatActivity {
    private String type;
    private int _uid;
    private int _auth;

    private EditText    contentArea;
    private EditText    titleArea;
    private Button      sendBtn;
    private Button      uploadBtn;
    private Spinner     spinner;

    sqlHandler handler;
    SQLiteDatabase newDb;
    ArrayList<Uri> uriList = new ArrayList<>();

    RecyclerView recyclerView;
    MultiImageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_activity);

        // db 객체 선언
        handler = new sqlHandler(this,"newDb.db",null,1);
        newDb = handler.getWritableDatabase();

        // 뷰 객체 호출
        contentArea = findViewById(R.id.contentArea);
        titleArea   = findViewById(R.id.titleArea);
        sendBtn     = findViewById(R.id.sendBtn);
        spinner     = findViewById(R.id.categorySpinner);
        uploadBtn   = findViewById(R.id.uploadBtn);
        recyclerView = findViewById(R.id.recyclerview);

        //이전 인텐트에서 유저아이디 가져옴
        //없으면 권한이 없다고 알리고 해당 인텐트 종료
        Intent intent = getIntent();
        _uid = intent.getIntExtra("_id",-1);
        _auth = intent.getIntExtra("_auth",0);
        Log.e("_uid",Integer.toString(_uid));

        //카테고리
        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("자유게시판");
        if(_auth == 2022)
            spinnerArray.add("공지 사항");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(adapterView.getItemAtPosition(i).toString().equals("공지 사항")) type = "notice";
                    else type = "community";
                    Log.e("type",type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                    type = "community";
            }
        });


        //버튼 이벤트 할당
        sendBtn.setOnClickListener(e->{
            if(_uid == -1 || _uid == 0){
                Toast.makeText(this,"해당 권한이 없습니다!", LENGTH_SHORT).show();
                finish();
            } else {
                write(newDb, titleArea.getText().toString(),contentArea.getText().toString(),_uid,type);
                getCurrentId(newDb,type);
            }
        });

        //여러장의 이미지 업로드 처리
        uploadBtn.setOnClickListener(e->{
            Intent imageIntent = new Intent(Intent.ACTION_PICK);
            imageIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            imageIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(imageIntent,2222);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2222) {
            if (data == null) {

            } else {
                if (data.getClipData() == null) {
                    Log.e("single choice", String.valueOf(data.getData()));
                    Uri imageUri = data.getData();
                    uriList.add(imageUri);

                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, HORIZONTAL, true));
                }
                else {
                    ClipData clipData = data.getClipData();
                    Log.e("clipData", String.valueOf(clipData.getItemCount()));

                    if (clipData.getItemCount() > 5) {
                        Toast.makeText(this, "사진은 5장까지 선택 가능합니다.", LENGTH_SHORT).show();
                    }
                    else {
                        Log.e("WriteActivity", "multiple choice");

                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();
                            try {
                                uriList.add(imageUri);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            adapter = new MultiImageAdapter(uriList, getApplicationContext());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(this, HORIZONTAL, true));
                        }
                    }
                }
            }
        }
    }

    private void write(SQLiteDatabase db, String title, String content, int _uid, String table){
        long CurrentTime = System.currentTimeMillis();
        Date TodayDate = new Date(CurrentTime);
        SimpleDateFormat SDFormat = new SimpleDateFormat("yy-MM-dd");
        String today = SDFormat.format(TodayDate);
        int _id = getCurrentId(db,table);

        //db에 삽입
        if(!title.equals("") && !content.equals("")){
            db.beginTransaction();
            try {
                getCurrentId(db,type);
                String query = String.format("INSERT INTO " + table + " (_uid,title,content,postdate) VALUES ('%d', %s, %s, '%s')",
                        _uid,DatabaseUtils.sqlEscapeString(title),DatabaseUtils.sqlEscapeString(content),today);
                db.execSQL(query);

                if(!uriList.isEmpty()){
                    for(int i=0; i<uriList.size(); i++){
                        Log.e("Uri",uriList.get(i).toString());
                        String imgQuery = String.format("INSERT INTO " + table + "_img (post_id,uri) VALUES('%d', %s)", _id, DatabaseUtils.sqlEscapeString(uriList.get(i).toString()));
                        db.execSQL(imgQuery);
                    }

                }

                db.setTransactionSuccessful();
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                db.endTransaction();

                Log.e("유저아이디",Integer.toString(_uid));
                Log.e("게시시간",today);
                Log.e("제목: ", title);
                Log.e("내용: ", content);
                Toast.makeText(this,"글 작성 성공", LENGTH_SHORT).show();
                finish();
            }
        }else
            Toast.makeText(this,"내용을 입력해 주세요", LENGTH_SHORT).show();
    }

    private int getCurrentId(SQLiteDatabase db, String table){
        String query = "SELECT _id FROM " + table + ";";
        Cursor c = db.rawQuery(query, null);
        c.moveToNext();

        Log.e("_id : ", Integer.toString(c.getCount()));
        return c.getCount()+1;
    }
 }
