package com.example.termproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase newDB;
    private sqlHandler sqlHandler;

    private Button loginBtn;
    private EditText usernameArea;
    private EditText passwordArea;
    private TextView registerArea;

    private int      _id;       //유저 아이디
    private int      _auth;     //권한번호
    private LinkedList<String>  titles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        


        //db 호출
        sqlHandler = new sqlHandler(this,"newDb.db",null,1);
        newDB = sqlHandler.getReadableDatabase();
        sqlHandler.onCreate(newDB);

        //뷰 접근
        loginBtn     = findViewById(R.id.loginBtn);
        usernameArea = findViewById(R.id.usernameArea);
        passwordArea = findViewById(R.id.passwordArea);
        registerArea = findViewById(R.id.registerBtn);

        //패스워드, 아이디 필드값이 채워져있다면 login()호출
        loginBtn.setOnClickListener(e->{
            if(!usernameArea.getText().toString().equals("") &&
                    !passwordArea.getText().toString().equals("")) {
                login(newDB,usernameArea.getText().toString(),passwordArea.getText().toString());
            } else {
                Toast.makeText(this, "다시 입력해주세요!", Toast.LENGTH_SHORT).show();
            }
        });

        //가입 액티비티로 이동
        registerArea.setOnClickListener(e->{
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        });


    }

    //onResume 호출 될때 필드값 초기화
    @Override
    protected void onResume() {
        super.onResume();
        _id = -1;
        usernameArea.setText("");
        passwordArea.setText("");
    }

    //로그인
    private  void login(SQLiteDatabase db, String username, String password) {

        //질의문, 커서 객체 선언
        String query = "select _id, _auth from user where userid = '" + username + "'AND password = '" + password + "';";
        Cursor c = db.rawQuery(query, null);

        Log.e("Total : ", Integer.toString(c.getCount()));

        if(c.getCount() == 1) {
            c.moveToNext();

            //_id값 추출
            _id = c.getInt(c.getColumnIndexOrThrow("_id"));
            _auth = c.getInt(c.getColumnIndexOrThrow("_auth"));
            Log.e("_id : ", Integer.toString(_id));
            Toast.makeText(this,"로그인 성공",Toast.LENGTH_SHORT).show();

            //_id값 MainMenu 액티비티로 넘기기
            Intent  intent = new Intent(MainActivity.this,MainMenuActivity.class);
            intent.putExtra("_id",_id);
            intent.putExtra("_auth",_auth);
            startActivity(intent);
        } else
            Toast.makeText(this,"로그인 실패",Toast.LENGTH_SHORT).show();
    }


}