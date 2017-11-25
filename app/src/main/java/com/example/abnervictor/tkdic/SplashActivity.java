package com.example.abnervictor.tkdic;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by abnervictor on 2017/11/21.
 */

public class SplashActivity extends AppCompatActivity {
    private String RootPath;

    private FileHelper fileHelper;

    private DataManager dataManager;
    private SQLiteDatabase db;

    private void initDataBase(){
        dataManager = new DataManager(this);
        db = dataManager.openDatabase("threekindom.db");
    }

    public static void verifyStoragePermissions(Activity activity){
        try{
            int permission_read = ActivityCompat.checkSelfPermission(activity,"android.permission.READ_EXTERNAL_STORAGE");
            int permission_write = ActivityCompat.checkSelfPermission(activity,"android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission_read != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);//请求读权限
            }
            if (permission_write != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);//请求写权限
            }
            if (permission_read != PackageManager.PERMISSION_GRANTED && permission_write != PackageManager.PERMISSION_GRANTED){
                //hasPermission = true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        setContentView(R.layout.splash_activity);
        //RootPath = Environment.getDataDirectory().getPath();
        RootPath = Environment.getExternalStorageDirectory().getPath();
        fileHelper = new FileHelper(RootPath);
        fileHelper.createFolder(RootPath,"TKDic");
        RootPath += "/TKDic";
        fileHelper.setRootPath(RootPath);
        fileHelper.createFolder(RootPath,"picture");
        initDataBase();
        {
            fileHelper.copyRawToFolder(this,R.raw.p1,"picture","1","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p2,"picture","2","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p3,"picture","3","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p4,"picture","4","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p5,"picture","5","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p6,"picture","6","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p22,"picture","22","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p23,"picture","23","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p24,"picture","24","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p25,"picture","25","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p26,"picture","26","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p27,"picture","27","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p28,"picture","28","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p29,"picture","29","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p30,"picture","30","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p31,"picture","31","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p32,"picture","32","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p33,"picture","33","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p52,"picture","52","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p53,"picture","53","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p54,"picture","54","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p55,"picture","55","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p56,"picture","56","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p57,"picture","57","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p101,"picture","101","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p102,"picture","102","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p103,"picture","103","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p104,"picture","104","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p201,"picture","201","bmp");
            fileHelper.copyRawToFolder(this,R.raw.p202,"picture","202","bmp");
        }
        //在这里复制图片和数据库
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

        Cursor countries = db.rawQuery("select * from country where countryName = \""+country_name+"\"",null);
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
