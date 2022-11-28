package com.example.termproject;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class IntroduceActivity extends AppCompatActivity {
    private Button greetingBtn;
    private Button introduceBtn;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduce_activity);

        greetingBtn = findViewById(R.id.greetingBtn);
        introduceBtn = findViewById(R.id.introduceBtn);
        textView = findViewById(R.id.text);

        greetingBtn.setOnClickListener(e->{
            setGreetingFrag();
        });
        introduceBtn.setOnClickListener(e->{
            setIntroduceFrag();
        });
    }
    public void setIntroduceFrag(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();

        fragmentTransaction.replace(R.id.introduceLayout,new IntroduceFragment(),"one");
        fragmentTransaction.commitAllowingStateLoss();

        textView.setText("");
    }

    public void setGreetingFrag(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();

        fragmentTransaction.replace(R.id.introduceLayout,new GreetingFragment(),"two");
        fragmentTransaction.commitAllowingStateLoss();

        textView.setText("");
    }
}
