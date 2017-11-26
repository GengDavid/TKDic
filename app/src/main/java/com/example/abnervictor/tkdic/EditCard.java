package com.example.abnervictor.tkdic;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.Arrays;

/**
 * Created by abnervictor on 2017/11/21.
 */

//新建、编辑人物卡片
public class EditCard extends AppCompatActivity {
    private int ID;
    private EditText loyal_to;//所属势力
    private EditText profile_name;//人物姓名
    private EditText birthday;//生卒信息
    private EditText nativeplace;//籍贯
    private EditText story;//人物事迹
    private CircularImageView profile_pic;//人物头像
    private ImageView cancel;//取消按钮
    private ImageView confirm;//确定按钮
    private characterInfo characterinfo;
    private static String[] country = {"蜀","吴","魏","它","t"};
    private Boolean[] checkSet;
    private boolean AnewProfileShouldBeMake;
    private Bitmap bitmap;//从相册获取到的bitmap
    private Bitmap defaultBitmap;

    private FileHelper fileHelper;
    private DataManager dataManager;
    private SQLiteDatabase db;

    private void initDataBase(){
        dataManager = new DataManager(this);
        db = dataManager.openDatabase("threekindom.db");
    }

    public EditCard(EditText loyal_to, EditText profile_name, EditText birthday, EditText nativeplace, EditText story, CircularImageView profile_pic, ImageView cancel, ImageView confirm, Bitmap defaultBitmap){
        this.loyal_to = loyal_to;
        this.profile_name = profile_name;
        this.birthday = birthday;
        this.nativeplace = nativeplace;
        this.story = story;
        this.profile_pic = profile_pic;
        this.cancel = cancel;
        this.confirm = confirm;
        this.defaultBitmap = defaultBitmap;
        characterinfo = null;
        checkSet = new Boolean[6];
        AnewProfileShouldBeMake = true;
        bitmap = null;
        String path = Environment.getExternalStorageDirectory().getPath()+ File.separator+"TKDic";
        fileHelper = new FileHelper(path);
        checkLegal();
        initDataBase();
    }

    public void setBitmap(Bitmap bm){
        bitmap = bm;
        profile_pic.setImageBitmap(bitmap);
    }

    public void InitEditCardWithNull(){
        AnewProfileShouldBeMake = true;
        //清空输入框内容
        loyal_to.setText("");
        profile_name.setText("");
        birthday.setText("");
        nativeplace.setText("");
        story.setText("");
        loyal_to.setError(null);
        profile_name.setError(null);
        birthday.setError(null);
        nativeplace.setError(null);
        confirm.setImageResource(R.drawable.confirm);
        //清空输入框内容
        characterinfo = null;
        checkSet = new Boolean[6];
        bitmap = null;
        ID = -1;
        profile_pic.setImageBitmap(defaultBitmap);
    }//新建人物信息的时候调用，清空所有信息

    public void InitEditCardWithInfo(characterInfo characterinfo){
        this.ID = characterinfo.id;
        AnewProfileShouldBeMake = false;
        this.characterinfo = characterinfo;
        loyal_to.setText(characterinfo.loyal_to);
        profile_name.setText(characterinfo.profile_name);
        birthday.setText(characterinfo.birthday);
        nativeplace.setText(characterinfo.nativeplace);
        story.setText(characterinfo.story);
        profile_pic.setImageBitmap(characterinfo.profile_pic);
        bitmap = characterinfo.profile_pic;
        checkLegal();
    }//编辑已有的人物卡片时调用

    public boolean checkLegal(){
        checkSet[1] = Arrays.asList(country).contains(loyal_to.getText().toString());//country_check,国家输入正确
        checkSet[2] = (profile_name.getText().toString().length() > 0);//name_check,名字不为空
        checkSet[3] = (birthday.getText().toString().length() > 0);//birthday_check,生卒不为空
        checkSet[4] = (nativeplace.getText().toString().length() > 0);//nativeplace_check,籍贯不为空

        if (characterinfo == null){
            checkSet[5] = false;
        }
        else if(characterinfo.profile_pic == null && bitmap == null){
            checkSet[5] = false;
        }//检测是否设置了人物头像

        if (checkSet[1] && checkSet[2] && checkSet[3] && checkSet[4]){
            checkSet[0] = true;//passCheck
            confirm.setImageResource(R.drawable.confirm_color);
        }
        else {
            checkSet[0] = false;
            confirm.setImageResource(R.drawable.confirm);
        }
        return checkSet[0];
    }//检查输入合法性

    public characterInfo getCharacterinfo(){
        if (characterinfo != null) return characterinfo;
        else return null;
    }//获取

    public boolean saveCharacterProfile(){
        //在数据库建立或修改人物信息
        String Name = profile_name.getText().toString();
        String Loyalto = loyal_to.getText().toString();
        String Birthday = birthday.getText().toString();
        String Nativeplace = nativeplace.getText().toString();
        String Story = " ";
        if (story.getText().toString().length() > 0) Story = story.getText().toString();
        if (AnewProfileShouldBeMake){
            if(!NewCtrInfo(Name,Loyalto,Birthday,Nativeplace,Story)) return false;
        }
        else{
            if(!UpdateCtrInfoWithName(Name,Loyalto,Birthday,Nativeplace,Story)) return false;
        }
        if (bitmap!=null && ID != -1){
            fileHelper.copyBitmapToFolder(bitmap,"picture",Integer.toString(ID));//根据人物ID保存图片
        }
        else if (ID != -1){
            fileHelper.copyBitmapToFolder(defaultBitmap,"picture",Integer.toString(ID));//根据人物ID保存图片
        }
        //在数据库建立或修改人物信息
        characterinfo = new characterInfo(Name);
        if(bitmap != null)characterinfo.setProfilepic(bitmap);//如果设置了人物头像，那么保存人物头像到指定路径
        else if(profile_pic == null)characterinfo.setProfilepic(defaultBitmap);//如果没设置人物头像，人物也没有设置过头像，那么将人物头像设置为默认头像
        return true;
    }//checkPass且保存按钮被点击时调用，有数据库操作

    public void setErrorMessages(){
        if (!checkSet[0]){
            for (int i = 1; i < checkSet.length; i++){
                if (!checkSet[i]){
                    switch (i){
                        case 1:
                            loyal_to.setError("所属势力只能是\"蜀、吴、魏、它\"");
                            break;
                        case 2:
                            profile_name.setError("姓名不能为空");
                            break;
                        case 3:
                            birthday.setError("生卒年月不能为空");
                            break;
                        case 4:
                            nativeplace.setError("籍贯不能为空");
                            break;
                    }
                }
                else{
                    switch (i){
                        case 1:
                            loyal_to.setError(null);
                            break;
                        case 2:
                            profile_name.setError(null);
                            break;
                        case 3:
                            birthday.setError(null);
                            break;
                        case 4:
                            nativeplace.setError(null);
                            break;
                    }
                }
            }
        }
    }//check不通过，点击保存按钮时输出错误信息

    private boolean UpdateCtrInfoWithName(String Name, String Loyalto, String Birthday, String Nativeplace, String Story){
        Cursor original_person = db.rawQuery("select * from person where ID = \""+ID+"\"",null);
        if(original_person.moveToFirst()){
            ContentValues values = new ContentValues();
            values.put("名字", Name);
            values.put("拼音", " ");
            values.put("性别", " ");
            values.put("字", " ");
            values.put("生卒", Birthday);
            values.put("籍贯", Nativeplace);
            values.put("主效", Loyalto);
            values.put("信息", Story);
            values.put("editable", 1);
            values.put("collected", original_person.getString(original_person.getColumnIndex("collected")));
            db.update("person",values, "ID = ?", new String[] {Integer.toString(ID)});
        }
        original_person.close();
        return true;
    }//根据输入的信息更新人物信息，返回一个查询成功/否的bool值

    private boolean NewCtrInfo(String Name, String Loyalto, String Birthday, String Nativeplace, String Story){
        Cursor person = db.rawQuery("select ID from person where 名字 = \""+Name+"\"",null);
        //db.execSQL("insert into person values(null,\""+Name+"\",\"pin\",\"男\",\"字\",\""+Birthday+"\",\""+Nativeplace+"\",\""+loyal_to+"\",\""+story+"\",\"1\",\"0\")");
        ContentValues values = new ContentValues();
        values.put("名字", Name);
        values.put("拼音", " ");
        values.put("性别", " ");
        values.put("字", " ");
        values.put("生卒", Birthday);
        values.put("籍贯", Nativeplace);
        values.put("主效", Loyalto);
        values.put("信息", Story);
        values.put("editable", 1);
        values.put("collected", 0);
        db.insert("person",null,values);

        person = db.rawQuery("select ID from person where 名字 = \""+Name+"\"",null);
        if (person.moveToFirst()) {
            ID = person.getInt(person.getColumnIndex("ID"));
        }
        person.close();


        return true;
    }//根据输入的信息新建人物，返回一个更新成功/否的bool值


}