package com.example.abnervictor.tkdic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by abnervictor on 2017/11/21.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterMainActivity();
            }
        },1000);
        Random rand = new Random();
        int i = rand.nextInt(4);
        InitCountryCard(i);
    }

    private void enterMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void InitCountryCard(int countryID){
        String country_name;

        switch(countryID){
            case 1: country_name = "蜀";break;
            case 2: country_name = "吴";break;
            case 3: country_name = "魏";break;
            case 4: country_name = "他";break;
            default: country_name = "蜀";break;
        }

        countryInfo countryInfo = new countryInfo(country_name);

        MyFontTextView countryName = findViewById(R.id.countryName);//国号
        TextView year = findViewById(R.id.year);//建国～亡国
        TextView leader = findViewById(R.id.leader);//国君
        TextView nativeplace = findViewById(R.id.nativeplace);//都城
        TextView knownCtr = findViewById(R.id.knownCtr);//知名人物
        TextView story = findViewById(R.id.story);
        //数据库查询函数
        countryName.setText(countryInfo.countryName);
        year.setText(countryInfo.year);
        leader.setText(countryInfo.leader);
        nativeplace.setText(countryInfo.nativeplace);
        knownCtr.setText(countryInfo.knownCtr);
        story.setText(countryInfo.story);
    }//

}
