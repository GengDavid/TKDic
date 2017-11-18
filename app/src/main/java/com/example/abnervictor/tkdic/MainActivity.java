package com.example.abnervictor.tkdic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationBar Navigationbar;
    private ImageView home;
    private ImageView folder;
    private ImageView privatecollect;

    private SearchBar Searchbar;
    private ImageView search_button;
    private EditText search_text;
    private View search_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        SetNavigationBarListener();
        SetSearchBarListener();

    }

    private void findView(){

        home = findViewById(R.id.navigation_bar_home);
        folder = findViewById(R.id.navigation_bar_folder);
        privatecollect = findViewById(R.id.navigation_bar_privatecollect);

        search_button = findViewById(R.id.search_button);
        search_text = findViewById(R.id.search_text);
        search_content = findViewById(R.id.search_content);

        Navigationbar = new NavigationBar(home,folder,privatecollect);
        Navigationbar.UpdateNavigationBarState(1);//初始化导航栏

        Searchbar = new SearchBar(search_button,search_text,search_content);

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

    private void SetSearchBarListener(){
        search_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Searchbar.Reversefocusing();
            }
        });
    }
}
