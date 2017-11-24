package com.example.abnervictor.tkdic;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by abnervictor on 2017/11/21.
 */

public class SplashActivity extends AppCompatActivity {
    private DataManager dataManager;
    private SQLiteDatabase db;

    private void initDataBase(){
        dataManager = new DataManager(this);
        db = dataManager.openDatabase("threekindom.db");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        initDataBase();
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


        MyFontTextView countryName = findViewById(R.id.countryName);//国号
        TextView year = findViewById(R.id.year);//建国～亡国
        TextView leader = findViewById(R.id.leader);//国君
        TextView nativeplace = findViewById(R.id.nativeplace);//都城
        TextView knownCtr = findViewById(R.id.knownCtr);//知名人物
        TextView story = findViewById(R.id.story);

        Cursor countries = db.rawQuery("select * from country where countryName = \""+countryName+"\"",null);
        if (countries.moveToFirst()) {
            countryName.setText(countries.getString(countries.getColumnIndex("countryName")));
            year.setText(countries.getString(countries.getColumnIndex("year")));
            leader.setText(countries.getString(countries.getColumnIndex("leader")));
            nativeplace.setText(countries.getString(countries.getColumnIndex("nativeplace")));
            knownCtr.setText(countries.getString(countries.getColumnIndex("knownCtr")));
            story.setText(countries.getString(countries.getColumnIndex("story")));
        }
        countries.close();


    }//

}
