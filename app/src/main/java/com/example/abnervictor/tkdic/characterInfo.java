package com.example.abnervictor.tkdic;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by abnervictor on 2017/11/22.
 */

public class characterInfo extends AppCompatActivity{
    public int id;//用来保存图片和获取图片
    public String loyal_to;
    public String profile_name;
    public String birthday;
    public String nativeplace;
    public String story;
    public Bitmap profile_pic;
    public boolean marked;//已收藏为true
    public boolean editable;//可编辑为true
    private DataManager dataManager;
    private SQLiteDatabase db;

    private void initDataBase(){
        dataManager = new DataManager(this);
        db = dataManager.openDatabase("threekindom.db");
    }

    public characterInfo(String profile_name,
                         String loyal_to,
                         Bitmap profile_pic,
                         boolean marked,
                         boolean editable,
                         String story,
                         String nativeplace,
                         String birthday) {
        this.profile_name = profile_name;
        this.loyal_to = loyal_to;//默认所属
        this.profile_pic = profile_pic;//默认没有头像
        this.marked = marked;//默认未收藏
        this.editable = editable;//默认不可编辑
        this.story = story;
        this.birthday = birthday;
        this.nativeplace = nativeplace;
    }
    public void setPic(Bitmap bm){
        if (profile_pic == null){
            profile_pic = bm;
        }
    }

    public void reverseMark(){
        initDataBase();
        if(marked){
            Cursor person = db.rawQuery("select * from person where 名字=\""+profile_name+"\"",null);
            if (person.moveToFirst()) {
                Integer ID = person.getInt(person.getColumnIndex("ID"));
                ContentValues values = new ContentValues();
                values.put("collected", 0);
                db.update("person", values, "ID = ?", new String[]{ID.toString()});
                person.close();
            }
        }
        else{
            Cursor person = db.rawQuery("select * from person where 名字=\""+profile_name+"\"",null);
            if (person.moveToFirst()) {
                Integer ID = person.getInt(person.getColumnIndex("ID"));
                ContentValues values = new ContentValues();
                values.put("collected", 1);
                db.update("person", values, "ID = ?", new String[]{ID.toString()});
                person.close();
            }
        }
    }//反转收藏，结合数据库操作

    public void setProfilepic(Bitmap bm){
        if(profile_pic == null){
            profile_pic = bm;
        }
        else {
            deleteBitmap();//先删除原有的头像
            profile_pic = bm;
            preserveBitmap();//将新头像保存到指定路径
        }
    }//将bitmap保存到data目录

    public void preserveBitmap(){
        //
    }//将profile_pic保存到特定路径

    public void deleteBitmap(){
        //
    }//删除特定的bitmap
}
