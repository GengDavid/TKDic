package com.example.abnervictor.tkdic;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

public class MainActivity extends AppCompatActivity {

    private class countryInfo{
        public String countryName;//国号
        public String year;//建国～亡国
        public String leader;//国君
        public String nativeplace;//都城
        public String knownCtr;//知名人物
        public String story;
        public countryInfo(String countryName){
            this.countryName = countryName;
            //从数据库中获取剩下的内容, 初始化国家数据
        }
    }

    //for Navigation Bar
    private NavigationBar Navigationbar;
    private ImageView home;
    private ImageView folder;
    private ImageView privatecollect;

    //for Search Bar
    private SearchBar Searchbar;
    private ImageView search_button;
    private EditText search_text;
    private View search_content;

    private List<String> characterID;//用于储存RecyclerView中

    //for country_card
    private List<Map<String,Object>> countryListitems = new ArrayList<>();
    private RecyclerView countryRecyclerView;
    private RecyclerViewAdapter<Map<String, Object>> countryAdapter;

    //for list_card
    private List<characterInfo> Sdata = new ArrayList<>();//后面不需要这种东西
    private List<Map<String,Object>> ctrListitems = new ArrayList<>();
    private RecyclerView ctrRecyclerView;
    private RecyclerViewAdapter<Map<String, Object>> ctrAdapter;

    //for list_card_with_delete
    private SwipeMenuRecyclerView ctr2RecyclerView;
    private RecyclerViewAdapter<Map<String, Object>> ctr2Adapter;

    //for character_profile_card
    private View ctrProfileCard;
    private CircularImageView profile_pic;
    private MyFontTextView profile_loyal_to;
    private MyFontTextView profile_name;
    private TextView profile_birthday;
    private TextView profile_nativeplace;
    private TextView profile_story;
    private ImageView profile_edit;
    private ImageView profile_mark;
    private CharacterCard characterCard;

    private Bitmap defaultPic;

    private FileHelper fileHelper;
    private DataManager dataManager;
    private SQLiteDatabase db;

    private Animation mshowAction1;
    private Animation mshowAction2;
    private Animation mshowAction3;
    private Animation mhiddenAction;

    private void initDataBase(){
        dataManager = new DataManager(this);
        db = dataManager.openDatabase("threekindom.db");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1) {                 //修改人物后
            if (resultCode==2) {
                String character = (String) data.getExtras().getSerializable("name");
                characterInfo c = new characterInfo(character);
                characterCard.initCharacterProfileCard(c,defaultPic);//初始化卡片
                setVisibilty(3);
            }
        }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            android.transition.Transition slidel = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            android.transition.Transition slider = TransitionInflater.from(this).inflateTransition(R.transition.slide1);
            getWindow().setEnterTransition(slidel);
            getWindow().setReenterTransition(slidel);
        }

        setContentView(R.layout.activity_main);

        String path = Environment.getExternalStorageDirectory().getPath()+ File.separator+"TKDic";
        fileHelper = new FileHelper(path);
        initDataBase();

        findView();
        InitRecyclerView();
        SetNavigationBarListener();
        SetSearchBarListener();//搜索框的监听器
        SetProfileCardListener();//人物详情卡片的监听器
        initAnimations();
        setVisibilty(1);

    }

    private void findView(){

        defaultPic = BitmapFactory.decodeResource(this.getApplicationContext().getResources(),R.drawable.zhuge);

        home = findViewById(R.id.navigation_bar_home);
        folder = findViewById(R.id.navigation_bar_folder);
        privatecollect = findViewById(R.id.navigation_bar_privatecollect);

        search_button = findViewById(R.id.search_button);
        search_text = findViewById(R.id.search_text);
        search_content = findViewById(R.id.search_content);

        Navigationbar = new NavigationBar(home,folder,privatecollect);
        Navigationbar.UpdateNavigationBarState(1);//初始化导航栏

        Searchbar = new SearchBar(search_button,search_text,search_content);

        //国家列表及人物列表
        countryRecyclerView = findViewById(R.id.country_recycler);
        ctrRecyclerView = findViewById(R.id.list_card_recycler);
        ctr2RecyclerView = findViewById(R.id.list_card_recycler_with_delete);

        //人物详情卡片
        ctrProfileCard = findViewById(R.id.character_card);
        profile_pic = findViewById(R.id.profile_pic);
        profile_loyal_to = findViewById(R.id.profile_loyal_to);
        profile_name = findViewById(R.id.profile_name);
        profile_nativeplace = findViewById(R.id.profile_nativeplace);
        profile_birthday = findViewById(R.id.profile_birthday);
        profile_story = findViewById(R.id.profile_story);
        profile_edit = findViewById(R.id.profile_edit);
        profile_mark = findViewById(R.id.profile_mark);
        characterCard = new CharacterCard(profile_pic,profile_loyal_to,profile_name,profile_birthday,profile_nativeplace,profile_story,profile_mark,profile_edit);
    }

    private void InitRecyclerView(){
        SetCountryCardRecyclerView();
        SetListCardRecyclerView();
    }

    private int nowVisibilty;//当前可视情况
    private void setVisibilty(int Case){
        switch (Case){
            case 1:
                nowVisibilty = 1;
                countryRecyclerView.setAnimation(mshowAction1);
                countryRecyclerView.setVisibility(View.VISIBLE);
                ctrRecyclerView.setVisibility(View.GONE);
                ctrProfileCard.setVisibility(View.GONE);

                break;
            case 2:
                nowVisibilty = 2;
                countryRecyclerView.setVisibility(View.GONE);
                ctrRecyclerView.setAnimation(mshowAction2);
                ctrRecyclerView.setVisibility(View.VISIBLE);
                ctrProfileCard.setVisibility(View.GONE);
                break;
            case 3:
                nowVisibilty = 3;
                countryRecyclerView.setVisibility(View.GONE);
                ctrRecyclerView.setVisibility(View.GONE);
                ctrProfileCard.setAnimation(mshowAction3);
                ctrProfileCard.setVisibility(View.VISIBLE);

                break;
            default:
                nowVisibilty = 0;
                break;
        }
    }//切换各视图的可见情况

    private void SetCountryCardRecyclerView(){
        Cursor countries = db.rawQuery("select * from country",null);
        if (countries.moveToFirst()) {
            do {
                Map<String, Object> listitem = new LinkedHashMap<>();
                listitem.put("countryName", countries.getString(countries.getColumnIndex("countryName")));
                listitem.put("year", countries.getString(countries.getColumnIndex("year")));
                listitem.put("leader", countries.getString(countries.getColumnIndex("leader")));
                listitem.put("nativeplace", countries.getString(countries.getColumnIndex("nativeplace")));
                listitem.put("knownCtr", countries.getString(countries.getColumnIndex("knownCtr")));
                listitem.put("story", countries.getString(countries.getColumnIndex("story")));
                countryListitems.add(listitem);
            } while (countries.moveToNext());
        }
        countries.close();
        countryRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        //国家recyclerview适配器初始化，绑定数据以及设置点击监听，点击监听具体为跳转显示人物recyclerview，并根据点击国家给其列表刷新添加相应的人物
        countryAdapter = new RecyclerViewAdapter<Map<String, Object>>(this, R.layout.country_card, countryListitems) {
            @Override
            public void convert(ViewHolder holder, Map<String, Object> Map) {
                MyFontTextView countryName = holder.getView(R.id.countryName);
                TextView year = holder.getView(R.id.year);//建国～亡国
                TextView leader = holder.getView(R.id.leader);//国君
                TextView nativeplace = holder.getView(R.id.nativeplace);//都城
                TextView knownCtr = holder.getView(R.id.knownCtr);//知名人物
                TextView story = holder.getView(R.id.story);//历史
                //findView
                countryName.setText(Map.get("countryName").toString());
                year.setText(Map.get("year").toString());
                leader.setText(Map.get("leader").toString());
                nativeplace.setText(Map.get("nativeplace").toString());
                knownCtr.setText(Map.get("knownCtr").toString());
                story.setText(Map.get("story").toString());
            }
        };
        SetCountryCardListener();//设置监听器
        //国家recyclerview动画、适配器设置
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(countryAdapter);
        animationAdapter.setDuration(1200);
        countryRecyclerView.setAdapter(countryAdapter);
        countryRecyclerView.setItemAnimator(new SlideInRightAnimator());
    }//初始化国家卡片列表视图

    private void SetListCardRecyclerView(){
        Cursor person = db.rawQuery("select * from person",null);
        if (person.moveToFirst()) {
            do {
                Map<String,Object> listitem = new LinkedHashMap<>();
                listitem.put("name", person.getString(person.getColumnIndex("名字")));
                listitem.put("loyal_to",person.getString(person.getColumnIndex("主效")));
                listitem.put("nativeplace",person.getString(person.getColumnIndex("籍贯")));
                listitem.put("birthday",person.getString(person.getColumnIndex("生卒")));
                listitem.put("story",person.getString(person.getColumnIndex("信息")));
                listitem.put("edit",person.getString(person.getColumnIndex("editable")));
                listitem.put("mark",person.getString(person.getColumnIndex("collected")));
                String ID = person.getString(person.getColumnIndex("ID"));
                Bitmap bm = fileHelper.getBitmapFromFolder("picture", ID,"bmp");
                if(bm!=null){
                    listitem.put("pic",bm);
                }
                else {
                    String sex = person.getString(person.getColumnIndex("性别"));
                    if(sex.equals("女")){
                        Random random = new Random();
                        int p = random.nextInt(2)+1;
                        bm = fileHelper.getBitmapFromFolder("picture", "20"+Integer.toString(p),"bmp");
                        if(bm!=null) listitem.put("pic",bm);
                        else listitem.put("pic", defaultPic);
                    }
                    else if(sex.equals("男")){
                        Random random = new Random();
                        int p = random.nextInt(4)+1;
                        bm = fileHelper.getBitmapFromFolder("picture", "10"+Integer.toString(p),"bmp");
                        if(bm!=null) listitem.put("pic",bm);
                        else listitem.put("pic", defaultPic);
                    }
                }
                ctrListitems.add(listitem);
            } while (person.moveToNext());
        }
        person.close();

        //设置recyclerview布局
        ctrRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //人物recyclerview适配器初始化，绑定数据，以及根据点击的人物跳转到PersonInformation（第二个Activity）中
        ctrAdapter = new RecyclerViewAdapter<Map<String,Object>>(this, R.layout.list_card, ctrListitems) {
            @Override
            public void convert(ViewHolder holder, Map<String, Object> M) {
                CircularImageView pic = holder.getView(R.id.profile_pic);
                pic.setImageBitmap((Bitmap) M.get("pic"));
                MyFontTextView name = holder.getView(R.id.profile_name);
                name.setText(M.get("name").toString());
                MyFontTextView country = holder.getView(R.id.nativeplace);
                country.setText(M.get("loyal_to").toString());
            }
        };
        SetListCardListener();//监听器
        //设置人物recyclerview动画、适配器
        AlphaInAnimationAdapter animationAdapter1 = new AlphaInAnimationAdapter(ctrAdapter);
        animationAdapter1.setDuration(1000);
        animationAdapter1.setFirstOnly(false);
        ctrRecyclerView.setItemAnimator(new FadeInAnimator());
        ctrRecyclerView.setAdapter(new ScaleInAnimationAdapter(animationAdapter1));
    }//初始化人物列表视图

    private void SetNavigationBarListener(){
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigationbar.UpdateNavigationBarState(1);
                setVisibilty(1);
            }
        });
        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigationbar.UpdateNavigationBarState(2);
                ctrListitems.clear();
                Cursor person = db.rawQuery("select * from person where collected = \"1\"",null);
                if (person.moveToFirst()) {
                    do {
                        Map<String,Object> listitem = new LinkedHashMap<>();
                        listitem.put("name", person.getString(person.getColumnIndex("名字")));
                        listitem.put("loyal_to",person.getString(person.getColumnIndex("主效")));
                        listitem.put("nativeplace",person.getString(person.getColumnIndex("籍贯")));
                        listitem.put("birthday",person.getString(person.getColumnIndex("生卒")));
                        listitem.put("story",person.getString(person.getColumnIndex("信息")));
                        listitem.put("edit",person.getString(person.getColumnIndex("editable")));
                        listitem.put("mark",person.getString(person.getColumnIndex("collected")));
                        Bitmap bm = fileHelper.getBitmapFromFolder("picture", person.getString(person.getColumnIndex("ID")),"bmp");
                        if(bm!=null){
                            listitem.put("pic",bm);
                        }
                        else {
                            String sex = person.getString(person.getColumnIndex("性别"));
                            if(sex.equals("女")){
                                Random random = new Random();
                                int p = random.nextInt(2)+1;
                                bm = fileHelper.getBitmapFromFolder("picture", "20"+Integer.toString(p),"bmp");
                                if(bm!=null) listitem.put("pic",bm);
                                else listitem.put("pic", defaultPic);
                            }
                            else if(sex.equals("男")){
                                Random random = new Random();
                                int p = random.nextInt(4)+1;
                                bm = fileHelper.getBitmapFromFolder("picture", "10"+Integer.toString(p),"bmp");
                                if(bm!=null) listitem.put("pic",bm);
                                else listitem.put("pic", defaultPic);
                            }
                        }
                        ctrListitems.add(listitem);
                    } while (person.moveToNext());
                }
                person.close();
                ctrAdapter.notifyDataSetChanged();
                setVisibilty(2);
            }
        });//点击跳转到markActivity
        privatecollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigationbar.UpdateNavigationBarState(3);
                Intent intent = new Intent(MainActivity.this,PrivateCollectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("name","");
                bundle.putSerializable("requestcode",0);
                intent.putExtras(bundle);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });//点击跳转到privatecollectActivity
    }//导航栏监听器

    private void SetSearchBarListener(){
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onSearchBarChanged();
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });//监听搜索框内的文本
    }//搜索框监听器，生成列表

    private void onSearchBarChanged(){
        if(nowVisibilty != 2)setVisibilty(2);//开始输入后，切换到显示内容
        String search = Searchbar.getSearchText();//获取搜索框内的文字
        int state = Navigationbar.getState();
        String sql = "select * from person where 名字 like '%"+search+"%'";
        if(state == 2){
            sql = "select * from person where 名字 like '%"+search+"%' and collected = 1";
        }

        Cursor person = db.rawQuery(sql,null);
        ctrListitems.clear();
        if (person.moveToFirst()) {
            do {
                Map<String,Object> listitem = new LinkedHashMap<>();
                listitem.put("name", person.getString(person.getColumnIndex("名字")));
                listitem.put("loyal_to",person.getString(person.getColumnIndex("主效")));
                listitem.put("nativeplace",person.getString(person.getColumnIndex("籍贯")));
                listitem.put("birthday",person.getString(person.getColumnIndex("生卒")));
                listitem.put("story",person.getString(person.getColumnIndex("信息")));
                listitem.put("edit",person.getString(person.getColumnIndex("editable")));
                listitem.put("mark",person.getString(person.getColumnIndex("collected")));
                Bitmap bm = fileHelper.getBitmapFromFolder("picture", person.getString(person.getColumnIndex("ID")),"bmp");
                if(bm!=null){
                    listitem.put("pic",bm);
                }
                else {
                    String sex = person.getString(person.getColumnIndex("性别"));
                    if(sex.equals("女")){
                        Random random = new Random();
                        int p = random.nextInt(2)+1;
                        bm = fileHelper.getBitmapFromFolder("picture", "20"+Integer.toString(p),"bmp");
                        if(bm!=null) listitem.put("pic",bm);
                        else listitem.put("pic", defaultPic);
                    }
                    else if(sex.equals("男")){
                        Random random = new Random();
                        int p = random.nextInt(4)+1;
                        bm = fileHelper.getBitmapFromFolder("picture", "10"+Integer.toString(p),"bmp");
                        if(bm!=null) listitem.put("pic",bm);
                        else listitem.put("pic", defaultPic);
                    }
                }
                ctrListitems.add(listitem);
            } while (person.moveToNext());
        }
        person.close();
        ctrAdapter.notifyDataSetChanged();
        //
    }

    private void SetCountryCardListener(){
        countryAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                ViewGroup ItemView = (ViewGroup) countryRecyclerView.getLayoutManager().findViewByPosition(position);
                MyFontTextView country = (MyFontTextView) ItemView.getChildAt(0);//获取到国家卡片中的国家名称View
                String countryName = country.getText().toString();
                Toast.makeText(getApplicationContext(),countryName,Toast.LENGTH_SHORT).show();
                //下面使用关键字国家名称生成列表
                ctrListitems.clear();
                Cursor person = db.rawQuery("select * from person where 主效 = \""+countryName+"\"",null);
                if (person.moveToFirst()) {
                    do {
                        Map<String,Object> listitem = new LinkedHashMap<>();
                        listitem.put("name", person.getString(person.getColumnIndex("名字")));
                        listitem.put("loyal_to",person.getString(person.getColumnIndex("主效")));
                        listitem.put("nativeplace",person.getString(person.getColumnIndex("籍贯")));
                        listitem.put("birthday",person.getString(person.getColumnIndex("生卒")));
                        listitem.put("story",person.getString(person.getColumnIndex("信息")));
                        listitem.put("edit",person.getString(person.getColumnIndex("editable")));
                        listitem.put("mark",person.getString(person.getColumnIndex("collected")));
                        Bitmap bm = fileHelper.getBitmapFromFolder("picture", person.getString(person.getColumnIndex("ID")),"bmp");
                        if(bm!=null){
                            listitem.put("pic",bm);
                        }
                        else {
                            String sex = person.getString(person.getColumnIndex("性别"));
                            if(sex.equals("女")){
                                Random random = new Random();
                                int p = random.nextInt(2)+1;
                                bm = fileHelper.getBitmapFromFolder("picture", "20"+Integer.toString(p),"bmp");
                                if(bm!=null) listitem.put("pic",bm);
                                else listitem.put("pic", defaultPic);
                            }
                            else if(sex.equals("男")){
                                Random random = new Random();
                                int p = random.nextInt(4)+1;
                                bm = fileHelper.getBitmapFromFolder("picture", "10"+Integer.toString(p),"bmp");
                                if(bm!=null) listitem.put("pic",bm);
                                else listitem.put("pic", defaultPic);
                            }
                        }
                        ctrListitems.add(listitem);
                    } while (person.moveToNext());
                }
                person.close();
                ctrAdapter.notifyDataSetChanged();//修改列表内容
                setVisibilty(2);
            }
            @Override
            public void onLongClick(int positon) {

            }
        });
    }//点击CountryCard，生成列表，待接入数据库

    private void SetListCardListener(){
        ctrAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                ViewGroup ItemView = (ViewGroup) ctrRecyclerView.getLayoutManager().findViewByPosition(position);
                MyFontTextView ctrName = (MyFontTextView) ItemView.getChildAt(0  );//获取到人物卡片中的人物名称View
                String characterName = ctrName.getText().toString();
                Toast.makeText(getApplicationContext(),characterName,Toast.LENGTH_SHORT).show();
                characterInfo ctrInfo;
                ctrInfo = new characterInfo(characterName);
                characterCard.initCharacterProfileCard(ctrInfo,defaultPic);//初始化卡片
                setVisibilty(3);
            }

            @Override
            public void onLongClick(int positon) {

            }
        });
    }//点击ListCard，做出响应

    private void SetProfileCardListener(){
        characterCard.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(characterCard.getCharacterinfo().editable){
                    //可编辑，跳转
                    Intent intent = new Intent(MainActivity.this,PrivateCollectActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("name",characterCard.getCharacterinfo().profile_name);
                    bundle.putSerializable("requestcode",1);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,1);
                }
                else{
                    Toast.makeText(MainActivity.this, "该人物不可编辑", Toast.LENGTH_SHORT).show();
                }
            }
        });//点击编辑按钮做出响应
    }//点击ProfileCard按钮，做出响应

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x1 = 0;
        float x2 = 0;
        float y1 = 0;
        float y2 = 0;
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if (nowVisibilty == 3) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {

                //当手指按下的时候
                x1 = event.getX();
                y1 = event.getY();
            }
            if(event.getAction() == MotionEvent.ACTION_UP) {
                //当手指离开的时候
                x2 = event.getX();
                y2 = event.getY();
                if(x2 - x1 > 1000) {
                    if (Navigationbar.getState() == 2) {
                        ctrListitems.clear();
                        Cursor person = db.rawQuery("select * from person where collected = \"1\"",null);
                        if (person.moveToFirst()) {
                            do {
                                Map<String,Object> listitem = new LinkedHashMap<>();
                                listitem.put("name", person.getString(person.getColumnIndex("名字")));
                                listitem.put("loyal_to",person.getString(person.getColumnIndex("主效")));
                                listitem.put("nativeplace",person.getString(person.getColumnIndex("籍贯")));
                                listitem.put("birthday",person.getString(person.getColumnIndex("生卒")));
                                listitem.put("story",person.getString(person.getColumnIndex("信息")));
                                listitem.put("edit",person.getString(person.getColumnIndex("editable")));
                                listitem.put("mark",person.getString(person.getColumnIndex("collected")));
                                Bitmap bm = fileHelper.getBitmapFromFolder("picture", person.getString(person.getColumnIndex("ID")),"bmp");
                                if(bm!=null){
                                    listitem.put("pic",bm);
                                }
                                else {
                                    String sex = person.getString(person.getColumnIndex("性别"));
                                    if(sex.equals("女")){
                                        Random random = new Random();
                                        int p = random.nextInt(2)+1;
                                        bm = fileHelper.getBitmapFromFolder("picture", "20"+Integer.toString(p),"bmp");
                                        if(bm!=null) listitem.put("pic",bm);
                                        else listitem.put("pic", defaultPic);
                                    }
                                    else if(sex.equals("男")){
                                        Random random = new Random();
                                        int p = random.nextInt(4)+1;
                                        bm = fileHelper.getBitmapFromFolder("picture", "10"+Integer.toString(p),"bmp");
                                        if(bm!=null) listitem.put("pic",bm);
                                        else listitem.put("pic", defaultPic);
                                    }
                                }
                                ctrListitems.add(listitem);
                            } while (person.moveToNext());
                        }
                    }
                    ctrAdapter.notifyDataSetChanged();
                    setVisibilty(2);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void initAnimations() {
        mshowAction1 = AnimationUtils.loadAnimation(this,R.anim.slidein);
        mshowAction1.setRepeatMode(Animation.RESTART);
        mshowAction1.setRepeatCount(Animation.INFINITE);
        mshowAction2 = AnimationUtils.loadAnimation(this,R.anim.slidein);
        mshowAction3 = AnimationUtils.loadAnimation(this,R.anim.slidein);
        mhiddenAction = AnimationUtils.loadAnimation(this,R.anim.slideout);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        intent = getIntent();
        int navigationState = intent.getIntExtra("navigationState",1);
        if(navigationState == 1){
            Navigationbar.UpdateNavigationBarState(1);
            setVisibilty(1);
        }
        else if(navigationState == 2){
            Navigationbar.UpdateNavigationBarState(2);
            ctrListitems.clear();
            Cursor person = db.rawQuery("select * from person where collected = \"1\"",null);
            if (person.moveToFirst()) {
                do {
                    Map<String,Object> listitem = new LinkedHashMap<>();
                    listitem.put("name", person.getString(person.getColumnIndex("名字")));
                    listitem.put("loyal_to",person.getString(person.getColumnIndex("主效")));
                    listitem.put("nativeplace",person.getString(person.getColumnIndex("籍贯")));
                    listitem.put("birthday",person.getString(person.getColumnIndex("生卒")));
                    listitem.put("story",person.getString(person.getColumnIndex("信息")));
                    listitem.put("edit",person.getString(person.getColumnIndex("editable")));
                    listitem.put("mark",person.getString(person.getColumnIndex("collected")));
                    Bitmap bm = fileHelper.getBitmapFromFolder("picture", person.getString(person.getColumnIndex("ID")),"bmp");
                    if(bm!=null){
                        listitem.put("pic",bm);
                    }
                    else {
                        String sex = person.getString(person.getColumnIndex("性别"));
                        if(sex.equals("女")){
                            Random random = new Random();
                            int p = random.nextInt(2)+1;
                            bm = fileHelper.getBitmapFromFolder("picture", "20"+Integer.toString(p),"bmp");
                            if(bm!=null) listitem.put("pic",bm);
                            else listitem.put("pic", defaultPic);
                        }
                        else if(sex.equals("男")){
                            Random random = new Random();
                            int p = random.nextInt(4)+1;
                            bm = fileHelper.getBitmapFromFolder("picture", "10"+Integer.toString(p),"bmp");
                            if(bm!=null) listitem.put("pic",bm);
                            else listitem.put("pic", defaultPic);
                        }
                    }
                    ctrListitems.add(listitem);
                } while (person.moveToNext());
            }
            person.close();
            ctrAdapter.notifyDataSetChanged();
            setVisibilty(2);
        }
    }

}
