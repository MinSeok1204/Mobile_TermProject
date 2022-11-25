package com.example.termproject;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {
    private int _id;            // 유저코드
    private int _auth;          // 권한코드 0: default user || 2022: admin

    private ImageButton noticeBtn;
    private ImageButton communityBtn;
    private ImageButton writeBtn;

    SQLiteDatabase newDB;
    sqlHandler sqlHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_activity);

        Intent intent = getIntent();
        _id = intent.getIntExtra("_id",-1);

        sqlHandler = new sqlHandler(this,"newDb.db",null,1);
        newDB = sqlHandler.getReadableDatabase();
        sqlHandler.onCreate(newDB);

        //버튼 객체 생성
        noticeBtn    = findViewById(R.id.noticeBtn);
        communityBtn = findViewById(R.id.communityBtn);
        writeBtn     = findViewById(R.id.writeBtn);

        /**버튼 이벤트 생성**/

        //글쓰기 버튼
        writeBtn.setOnClickListener(e->{
            Intent writeInt = new Intent(this,WriteActivity.class);
            writeInt.putExtra("_id",_id);
            writeInt.putExtra("_auth",_auth);

            Log.e("_auth",Integer.toString(_auth));

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
                Dialog dialog = new Dialog(MainMenuActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.setting_dialog);

                showDialog(dialog);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //설정 다이얼로그 메소드
    private void showDialog(Dialog dialog){
        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);
        EditText passwordArea = dialog.findViewById(R.id.passwordArea);
        EditText chkPasswordArea = dialog.findViewById(R.id.chkPasswordArea);
        EditText prePasswordArea = dialog.findViewById(R.id.prePasswordArea);
        TextView adminCheck = dialog.findViewById(R.id.adminCheck);


        dialog.show();

        //어드민 권한 부여
        adminCheck.setOnClickListener(e->{
            Dialog adminDialog = new Dialog(MainMenuActivity.this);
            adminDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            adminDialog.setContentView(R.layout.getauth_dialog);

            getAuth(adminDialog);
            dialog.dismiss();
        });

        confirmBtn.setOnClickListener(e->{
            //두 EditText 비어있어도 값 변경 X
            if(passwordArea.getText().toString().equals("") && chkPasswordArea.getText().toString().equals("")){

            } else {
                if(passwordArea.getText().toString().equals(chkPasswordArea.getText().toString())){
                    update(dialog, passwordArea.getText().toString(), prePasswordArea.getText().toString());
                } else
                    Toast.makeText(this,"입력한 비밀번호가 다릅니다.",Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(e->{
            dialog.dismiss();
        });
    }

    //관리자 권한 다이얼로그 메소드
    private void getAuth(Dialog dialog){
        final String adminPassword = "password";
        EditText passwordArea = dialog.findViewById(R.id.passwordArea);
        CheckBox keepAdminCheck = dialog.findViewById(R.id.keepAdminCheck);
        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

        dialog.show();

        confirmBtn.setOnClickListener(e->{
            //비밀번호 확인
            if(adminPassword.equals(passwordArea.getText().toString())){
                // 권한유지가 켜져있다면
                //TODO sql 권한부여
                if(keepAdminCheck.isChecked()){
                    Toast.makeText(this,"영구 권한 획득",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"임시 권한 획득",Toast.LENGTH_SHORT).show();
                    _auth = 2022;
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(this,"비밀번호가 틀립니다.",Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(e->{
            dialog.dismiss();
        });
    }

    // sql update 메소드
    private void update(Dialog dialog, String password, String prePassword){

        //기존 비밀번호 가져오기
        String searchQuery = "SELECT password FROM user WHERE _id = '" + _id +"';";
        Cursor c = newDB.rawQuery(searchQuery, null);
        c.moveToNext();
        String correctPassword = c.getString(c.getColumnIndexOrThrow("password"));
        Log.e("correctPassword : ", correctPassword);
        Log.e("prePassword : ", prePassword);

        //비번비교후 변경
        if(prePassword.equals(correctPassword)){
            newDB.beginTransaction();
            try {
                String updateQuery = "UPDATE user SET password = '" + password + "' WHERE _id = '" + _id +"';";
                newDB.execSQL(updateQuery);
                newDB.setTransactionSuccessful();
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                newDB.endTransaction();
                Toast.makeText(this,"비밀번호 변경 성공",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        } else
            Toast.makeText(this,"비밀번호가 틀립니다.",Toast.LENGTH_SHORT).show();
    }
}
