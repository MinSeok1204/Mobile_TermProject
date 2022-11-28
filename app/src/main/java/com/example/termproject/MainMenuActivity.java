package com.example.termproject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Timer;
import java.util.TimerTask;

public class MainMenuActivity extends AppCompatActivity {
    private int _id;            // 유저코드
    private int _auth;          // 권한코드 0: default user || 2022: admin

    private int[] imgs = {R.drawable.pic1,R.drawable.pic2,R.drawable.pic3};
    int currentPage = 0;

    Timer timer;
    final int PAGE_NUM = imgs.length;
    final long DELAY_MS = 3000; //초기 웨이팅 타임
    final long PERIOD_MS = 5000; // 5초마다 넘어가짐

    private ImageButton communityBtn;
    private ImageButton introduceBtn;
    private ImageButton gotoWebBtn;
    private ImageButton jobInfoBtn;
    private ImageButton howtocomeBtn;
    private ImageButton deptNoticeBtn;

    private ViewPager viewPager;

    SQLiteDatabase newDB;
    sqlHandler sqlHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_activity);

        //메인 엑티비티에서 값 가져오기
        Intent intent = getIntent();
        _id = intent.getIntExtra("_id",-1);
        _auth = intent.getIntExtra("_auth",0);

        sqlHandler = new sqlHandler(this,"newDb.db",null,1);
        newDB = sqlHandler.getReadableDatabase();
        sqlHandler.onCreate(newDB);

        viewPager   = findViewById(R.id.viewpager);
        communityBtn = findViewById(R.id.communityBtn);
        introduceBtn = findViewById(R.id.introduceBtn);
        gotoWebBtn = findViewById(R.id.gotoWebBtn);
        jobInfoBtn = findViewById(R.id.jobInfoBtn);
        howtocomeBtn = findViewById(R.id.howtocomeBtn);
        deptNoticeBtn = findViewById(R.id.deptNoticeBtn);

        myPagerAdapter adapter = new myPagerAdapter(this,imgs);
        viewPager.setAdapter(adapter);

        communityBtn.setOnClickListener(e->{
            Intent communityIntent = new Intent(this, CommunityActivity.class);
            communityIntent.putExtra("_id",_id);
            communityIntent.putExtra("_auth",_auth);
            startActivity(communityIntent);
        });
        gotoWebBtn.setOnClickListener(e->{
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(Uri.parse("https://www.hanbat.ac.kr/infocomm/"));
            startActivity(webIntent);
        });

        introduceBtn.setOnClickListener(e->{
            Intent introduceIntent = new Intent(this, IntroduceActivity.class);
            startActivity(introduceIntent);
        });

        howtocomeBtn.setOnClickListener(e->{
            String loc = "한밭대학교";
            Uri uri = Uri.parse("geo:0,0?q=" + loc);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(mapIntent);
        });

        jobInfoBtn.setOnClickListener(e->{
            Toast.makeText(this,"서비스 준비중입니다.",Toast.LENGTH_SHORT).show();
        });

        deptNoticeBtn.setOnClickListener(e->{
            Toast.makeText(this,"서비스 준비중입니다.",Toast.LENGTH_SHORT).show();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        final Handler handle = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                currentPage = viewPager.getCurrentItem();
                int nextPage = currentPage + 1;

                if(nextPage >= PAGE_NUM) nextPage = 0;
                viewPager.setCurrentItem(nextPage,true);
                currentPage = nextPage;
            }
        };

        timer = new Timer();    // Thread 추가
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handle.post(runnable);
            }
        },DELAY_MS,PERIOD_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //다른 액티비티 수행시 타이머 종료
        if(timer != null){
            timer.cancel();
            timer = null;
        }
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

        if(_auth == 2022) adminCheck.setVisibility(View.INVISIBLE);
        dialog.show();

        //어드민 권한 부여
        adminCheck.setOnClickListener(e->{
            Dialog adminDialog = new Dialog(MainMenuActivity.this);
            adminDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            adminDialog.setContentView(R.layout.getauth_dialog);

            showAuth(adminDialog);
            dialog.dismiss();
        });

        confirmBtn.setOnClickListener(e->{
            //두 EditText 비어있어도 값 변경 X
            if(passwordArea.getText().toString().equals("") && chkPasswordArea.getText().toString().equals("")){

            } else {
                if(passwordArea.getText().toString().equals(chkPasswordArea.getText().toString())){
                    updatePwd(dialog, passwordArea.getText().toString(), prePasswordArea.getText().toString());
                } else
                    Toast.makeText(this,"입력한 비밀번호가 다릅니다.",Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(e->{
            dialog.dismiss();
        });
    }

    //관리자 권한 다이얼로그 메소드
    private void showAuth(Dialog dialog){
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
                    getAuth();
                    dialog.dismiss();
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
    private void updatePwd(Dialog dialog, String password, String prePassword){

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

    //관리자 권한 획득
    private void getAuth(){
        newDB.beginTransaction();
        try {
            String getAuthQuery  = "UPDATE user SET _auth = 2022 WHERE _id = '" + _id +"';";
            newDB.execSQL(getAuthQuery);
            newDB.setTransactionSuccessful();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            newDB.endTransaction();
            _auth = 2022;
        }
    }

    public class myPagerAdapter extends androidx.viewpager.widget.PagerAdapter {
        Context context;
        int[] images;

        LayoutInflater layoutInflater;
        public myPagerAdapter(Context context, int[] images){
            this.context = context;
            this.images = images;
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((LinearLayout) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemview = layoutInflater.inflate(R.layout.pager_img_item,container,false);
            ImageView imageView = itemview.findViewById(R.id.pagerImage);
            imageView.setImageResource(imgs[position]);
            Objects.requireNonNull(container).addView(itemview);
            return itemview;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout)object);
        }
    }
}
