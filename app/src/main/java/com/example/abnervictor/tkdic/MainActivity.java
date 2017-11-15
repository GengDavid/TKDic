package com.example.abnervictor.tkdic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationBar Navigationbar;
    private ImageView home;
    private ImageView folder;
    private ImageView privatecollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        SetNavigationBarListener();

    }

    private void findView(){

        home = findViewById(R.id.navigation_bar_home);
        folder = findViewById(R.id.navigation_bar_folder);
        privatecollect = findViewById(R.id.navigation_bar_privatecollect);

        Navigationbar = new NavigationBar(home,folder,privatecollect);
        Navigationbar.UpdateNavigationBarState(1);//初始化导航栏

    }

    private void SetNavigationBarListener(){
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigationbar.UpdateNavigationBarState(1);
            }
        });
        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigationbar.UpdateNavigationBarState(2);
            }
        });
        privatecollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigationbar.UpdateNavigationBarState(3);
            }
        });
    }//导航栏监听器


}
