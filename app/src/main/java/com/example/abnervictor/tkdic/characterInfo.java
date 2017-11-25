package com.example.abnervictor.tkdic;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.util.Random;

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

    private FileHelper fileHelper;
    private DataManager dataManager;
    private SQLiteDatabase db;

    private void initDataBase(){
        //fileHelper = new FileHelper(Environment.getDataDirectory()+"/data");
        dataManager = new DataManager(this);
        db = dataManager.openDatabase("threekindom.db");
    }

    public characterInfo(String profile_name){
        this.profile_name = profile_name;
        loyal_to = "蜀";//默认所属
        profile_pic = null;//默认没有头像
        marked = false;//默认未收藏
        editable = false;//默认不可编辑
        String RootPath = Environment.getExternalStorageDirectory().getPath()+"/TKDic";
        fileHelper = new FileHelper(RootPath);
        getData();
    }

    private void getData(){
        initDataBase();
        Cursor person = db.rawQuery("select * from person where 名字=\""+profile_name+"\"",null);
        if (person.moveToFirst()) {
            id = person.getInt(person.getColumnIndex("ID"));
            loyal_to = person.getString(person.getColumnIndex("主效"));
            nativeplace = person.getString(person.getColumnIndex("籍贯"));
            birthday = person.getString(person.getColumnIndex("生卒"));
            story = person.getString(person.getColumnIndex("信息"));
            String edit = person.getString(person.getColumnIndex("editable"));
            if(edit.equals("1")) editable = true;
            else editable = false;
            String mark = person.getString(person.getColumnIndex("collected"));
            if(mark.equals("1")) marked = true;
            else marked = false;
            Bitmap bm = fileHelper.getBitmapFromFolder("picture",Integer.toString(id),"bmp");
            if (bm != null){
                profile_pic = bm;
            }
            else {
                String sex = person.getString(person.getColumnIndex("性别"));
                if(sex.equals("女")){
                    Random random = new Random();
                    int p = random.nextInt(2)+1;
                    bm = fileHelper.getBitmapFromFolder("picture", "20"+Integer.toString(p),"bmp");
                    if(bm!=null) profile_pic = bm;
                }
                else if(sex.equals("男")){
                    Random random = new Random();
                    int p = random.nextInt(4)+1;
                    bm = fileHelper.getBitmapFromFolder("picture", "10"+Integer.toString(p),"bmp");
                    if(bm!=null) profile_pic = bm;
                }
            }
        }
        person.close();
    }

    public void setPic(Bitmap bm){
        if (profile_pic == null){
            profile_pic = bm;
        }
    }

    public void reverseMark(){
        initDataBase();
        Cursor person = db.rawQuery("select * from person where 名字=\""+profile_name+"\"",null);
        person.moveToFirst();
        String mark = person.getString(person.getColumnIndex("collected"));
        if(mark.equals("1")){
            if (person.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("collected", 0);
                db.update("person", values, "名字 = ?", new String[]{profile_name});
                person.close();
            }
            marked = false;
        }
        else{
            if (person.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("collected", 1);
                db.update("person", values, "名字 = ?", new String[]{profile_name});
                person.close();
            }
            marked = true;
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
