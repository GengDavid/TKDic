package com.example.abnervictor.tkdic;

import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * Created by abnervictor on 2017/11/14.
 */

public class NavigationBar extends AppCompatActivity{
    private int State;//1 for home, 2 for folder, 3 for privatecollection
    private ImageView home;
    private ImageView folder;
    private ImageView privatecollect;

    public NavigationBar(ImageView home, ImageView folder, ImageView privatecollect){
        this.home = home;
        this.folder = folder;
        this.privatecollect = privatecollect;
        this.State = 1;
    }

    public int getState() {
        return State;
    }

    public void UpdateNavigationBarState(int state){
        State = state;
        UpdateNavigationBar();
    }

    private void UpdateNavigationBar(){
        if (State == 1){
            home.setImageResource(R.drawable.home_color);
            folder.setImageResource(R.drawable.folder);
            privatecollect.setImageResource(R.drawable.privatecollect);
        }
        else if (State == 2){
            home.setImageResource(R.drawable.home);
            folder.setImageResource(R.drawable.folder_color);
            privatecollect.setImageResource(R.drawable.privatecollect);
        }
        else if (State == 3){
            home.setImageResource(R.drawable.home);
            folder.setImageResource(R.drawable.folder);
            privatecollect.setImageResource(R.drawable.privatecollect_color);
        }
    }//更新导航栏图标
}
