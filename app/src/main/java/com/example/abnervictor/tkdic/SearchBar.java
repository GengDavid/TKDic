package com.example.abnervictor.tkdic;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by abnervictor on 2017/11/18.
 */

public class SearchBar extends AppCompatActivity {
    private boolean foucusing;
    private ImageView search_button;
    private EditText search_text;
    private View search_content;

    public SearchBar(ImageView search_button, EditText search_text, View search_content){
        this.search_button = search_button;
        this.search_text = search_text;
        this.search_content = search_content;
        SetFocusChangeListener();//设置改变搜索框激活状态的监听器
        foucusing = false;
        DisableSearchBar();
    }
    private void Reversefocusing(){
        if (foucusing){
            setFocusing(false);
        }
        else {
            setFocusing(true);
        }
    }
    private void setFocusing(boolean bool){
        foucusing = bool;
        if (bool){
            EnableSearchBar();
        }
        else {
            DisableSearchBar();
        }
    }
    public String getSearchText(){
        return search_text.getText().toString();
    }
    private void DisableSearchBar(){
        search_button.setImageResource(R.drawable.search);
        search_text.setHint("点击搜索");
        search_text.clearFocus();
        search_content.setElevation(0);
        search_content.setAlpha((float) 0.5);
        Keyboard(false);
    }
    private void EnableSearchBar(){
        search_button.setImageResource(R.drawable.search_color);
        search_text.setHint("输入武将姓名");
        search_text.requestFocus();
        search_content.setElevation(5);
        search_content.setAlpha((float) 0.8);
        Keyboard(true);
    }

    private void Keyboard(boolean show){
        InputMethodManager imm = (InputMethodManager) search_text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show){
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        }//显示键盘
        else {
            imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0) ;
        }//收起键盘
    }

    private boolean isFocusing(){
        return foucusing;
    }

    private void SetFocusChangeListener(){
        search_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Reversefocusing();
            }
        });//点击搜索按钮改变搜索框激活状态
        search_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus&&!foucusing) {
                    setFocusing(true);
                }
            }
        });
    }

}
