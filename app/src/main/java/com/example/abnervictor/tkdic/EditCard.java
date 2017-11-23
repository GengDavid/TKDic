package com.example.abnervictor.tkdic;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by abnervictor on 2017/11/21.
 */

//新建、编辑人物卡片
public class EditCard extends AppCompatActivity {
    private EditText loyal_to;//所属势力
    private EditText profile_name;//人物姓名
    private EditText birthday;//生卒信息
    private EditText nativeplace;//籍贯
    private EditText story;//人物事迹
    private CircularImageView profile_pic;//人物头像
    private ImageView cancel;//取消按钮
    private ImageView confirm;//确定按钮
    private characterInfo characterinfo;
    private static String[] country = {"蜀","吴","魏","它"};
    private Boolean[] checkSet;
    private boolean AnewProfileShouldBeMake;
    private Bitmap bitmap;//从相册获取到的bitmap
    private Bitmap defaultBitmap;

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
        checkLegal();
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
        //清空输入框内容
        characterinfo = null;
        checkSet = new Boolean[6];
        bitmap = null;
        profile_pic.setImageBitmap(defaultBitmap);
    }//新建人物信息的时候调用，清空所有信息

    public void InitEditCardWithInfo(characterInfo characterinfo){
        AnewProfileShouldBeMake = false;
        this.characterinfo = characterinfo;
        loyal_to.setText(characterinfo.loyal_to);
        profile_name.setText(characterinfo.profile_name);
        birthday.setText(characterinfo.birthday);
        nativeplace.setText(characterinfo.nativeplace);
        story.setText(characterinfo.story);
        profile_pic.setImageBitmap(characterinfo.profile_pic);
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
        }//需要insert人物内容
        else{
            if(!UpdateCtrInfoWithName(Name,Loyalto,Birthday,Nativeplace,Story)) return false;
        }//需要update人物内容
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

    public void getPicFromAlbum(){
        Intent getAlbum = new Intent(Intent.ACTION_PICK, null);
        getAlbum.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(getAlbum,1);//requestCode为1
    }//从相册获取图片

    private boolean UpdateCtrInfoWithName(String Name, String Loyalto, String Birthday, String Nativeplace, String Story){
        return false;
    }//根据输入的信息更新人物信息，返回一个查询成功/否的bool值

    private boolean NewCtrInfo(String Name, String Loyalto, String Birthday, String Nativeplace, String Story){
        return false;
    }//根据输入的信息新建人物，返回一个查询成功/否的bool值

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 1 && data != null){
            ContentResolver contentResolver = getContentResolver();
            try {
                Uri originalUri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver,originalUri);
                profile_pic.setImageBitmap(bitmap);
            }catch(IOException e){
                Log.e("TAG-->Error",e.toString());
            }
        }//从相册获取到图片后，转化为bitmap，替换到头像处
    }

}