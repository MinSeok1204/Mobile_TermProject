package com.example.termproject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn;
    private EditText usernameArea;
    private EditText passwordArea;
    private EditText chkPasswordArea;

    sqlHandler handler;
    SQLiteDatabase newDb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        handler = new sqlHandler(this,"newDb.db",null,1);
        newDb = handler.getWritableDatabase();

        registerBtn     = findViewById(R.id.registerBtn);
        usernameArea    = findViewById(R.id.usernameArea);
        passwordArea    = findViewById(R.id.passwordArea);
        chkPasswordArea = findViewById(R.id.chkPasswordArea);

        registerBtn.setOnClickListener(e->{
            register(usernameArea.getText().toString(),passwordArea.getText().toString(),chkPasswordArea.getText().toString());
        });
    }

    // 가입 메소드
    private void register(String username, String password, String chkPassword){
        if(!username.equals("") && !password.equals("") && !chkPassword.equals("")){
            if(!chkPassword.equals(password)) {
                Toast.makeText(this, "입력된 비밀번호와 다릅니다.", Toast.LENGTH_SHORT).show();
            } else if(check(username, password)){
                newDb.beginTransaction();
                try{
                    String query = "INSERT INTO user (userid, password) VALUES ('" +  username + "', '" + password + "')";
                    newDb.execSQL(query);
                    newDb.setTransactionSuccessful();
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    newDb.endTransaction();
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else {
            Toast.makeText(this,"다시 입력하세요.",Toast.LENGTH_SHORT).show();
        }
    }

    // 중복체크
    private boolean check(String username, String password){

        String[] returnVal;
        String query = "select * from user where userid= '" + username + "';";
        Cursor cursor = newDb.rawQuery(query,null);

        Log.e("중복된 아이디",Integer.toString(cursor.getCount()));
        if(cursor.getCount() >= 1){
            Toast.makeText(this, "중복된 아이디입니다", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }
}
