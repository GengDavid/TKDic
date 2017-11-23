package com.example.abnervictor.tkdic;

import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by abnervictor on 2017/11/21.
 */

//人物详情卡片
public class CharacterCard {
    private ConstraintLayout character_card;
    private CircularImageView profile_pic;
    private MyFontTextView loyal_to;
    private MyFontTextView profile_name;
    private TextView nativeplace;
    private TextView birthday;
    private TextView story;
    public ImageView mark;
    public ImageView edit;
    private characterInfo characterinfo;

    public CharacterCard (CircularImageView profile_pic, MyFontTextView loyal_to, MyFontTextView profile_name, TextView birthday, TextView nativeplace, TextView story, ImageView mark, ImageView edit){
        this.loyal_to = loyal_to;
        this.profile_name = profile_name;
        this.profile_pic = profile_pic;
        this.nativeplace = nativeplace;
        this.birthday = birthday;
        this.story = story;
        this.edit = edit;
        this.mark = mark;
        characterinfo = null;
        SetOnClickListener();
    }

    public void initCharacterProfileCard(characterInfo characterinfo, Bitmap defaultPic){
        this.characterinfo = characterinfo;
        loyal_to.setText(characterinfo.loyal_to);
        profile_name.setText(characterinfo.profile_name);
        birthday.setText(characterinfo.birthday);
        nativeplace.setText(characterinfo.nativeplace);
        story.setText(characterinfo.story);

        characterinfo.setPic(defaultPic);
        profile_pic.setImageBitmap(characterinfo.profile_pic);

        if (characterinfo.marked){mark.setImageResource(R.drawable.mark_color);}//已收藏
        else{mark.setImageResource(R.drawable.mark);}//未收藏

        if (characterinfo.editable){edit.setImageResource(R.drawable.edit_color);}//可编辑
        else{edit.setImageResource(R.drawable.edit);}//不可编辑
    }//用于修改人物详情卡片的数据，修改时传入一个characterInfo

    public characterInfo getCharacterinfo(){
        return characterinfo;
    }

    private void SetOnClickListener(){
        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (characterinfo != null){
                    characterinfo.reverseMark();//修改数据库，反转收藏情况
                    if (characterinfo.marked){mark.setImageResource(R.drawable.mark_color);}//已收藏
                    else{mark.setImageResource(R.drawable.mark);}//未收藏
                }
            }
        });//点击收藏按钮做出响应
        //编辑按钮的监听器写到了activity中
    }

}
