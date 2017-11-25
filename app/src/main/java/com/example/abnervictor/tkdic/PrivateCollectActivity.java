package com.example.abnervictor.tkdic;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by abnervictor on 2017/11/22.
 */

public class PrivateCollectActivity extends AppCompatActivity {

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

    //for list_card
    private List<characterInfo> Sdata = new ArrayList<>();//后面不需要这种东西
    private List<Map<String,Object>> ctr2Listitems = new ArrayList<>();

    //for list_card_with_delete
    private SwipeMenuRecyclerView ctr2RecyclerView;
    private RecyclerViewAdapter<Map<String, Object>> ctr2Adapter;
    private float density;
    private int width;
    private int height;//获取屏幕宽高

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

    //for character_edit_card
    private View ctrEditCard;
    private EditText edit_loyal_to;//所属势力
    private EditText edit_name;//人物姓名
    private EditText edit_birthday;//生卒信息
    private EditText edit_nativeplace;//籍贯
    private EditText edit_story;//人物事迹
    private CircularImageView edit_pic;//人物头像
    private ImageView edit_cancel;//取消按钮
    private ImageView edit_confirm;//确定按钮
    private EditCard editCard;

    //for privatecollect_menu
    private View privatecollect_menu;
    private ImageView add_profile_button;

    private FileHelper fileHelper;
    private DataManager dataManager;
    private SQLiteDatabase db;

    private int requestcode = 0;

    private void initDataBase(){
        dataManager = new DataManager(this);
        db = dataManager.openDatabase("threekindom.db");
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
            android.transition.Transition slidel = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            android.transition.Transition slider = TransitionInflater.from(this).inflateTransition(R.transition.slide1);
            getWindow().setEnterTransition(slidel);
            getWindow().setReenterTransition(slidel);
        }


        requestcode = (int) getIntent().getExtras().get("requestcode");
        String character =  (String) getIntent().getExtras().get("name");

        String path = Environment.getExternalStorageDirectory().getPath()+ File.separator+"TKDic";
        fileHelper = new FileHelper(path);
        initDataBase();

        setContentView(R.layout.acitivity_privatecollect);
        if ( requestcode == 1) {
            findView();
            SetListCardWithDeleteRecyclerView();//初始化RecyclerView
            SetNavigationBarListener();
            SetSearchBarListener();
            SetEditCardListener();
            SetProfileCardListener();
            SetMenuListener();
            characterInfo ctrInfo = new characterInfo(character);
            editCard.InitEditCardWithInfo(ctrInfo);
            setVisibilty(3);
        }
        else {
            findView();
            SetListCardWithDeleteRecyclerView();//初始化RecyclerView
            SetNavigationBarListener();
            SetSearchBarListener();
            SetEditCardListener();
            SetProfileCardListener();
            SetMenuListener();
            setVisibilty(1);
        }


        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        density = dm.density;
        width   = dm.widthPixels;
        height  = dm.heightPixels;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 2 && data != null){
            ContentResolver contentResolver = getContentResolver();
            try {
                Uri originalUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver,originalUri);
                editCard.setBitmap(bitmap);
            }catch(IOException e){
                Log.e("TAG-->Error",e.toString());
            }
        }//从相册获取到图片后，转化为bitmap，替换到头像处
    }

    private void findView(){

        defaultPic = BitmapFactory.decodeResource(this.getApplicationContext().getResources(),R.drawable.default_profile_pic);

        home = findViewById(R.id.navigation_bar_home);
        folder = findViewById(R.id.navigation_bar_folder);
        privatecollect = findViewById(R.id.navigation_bar_privatecollect);

        search_button = findViewById(R.id.search_button);
        search_text = findViewById(R.id.search_text);
        search_content = findViewById(R.id.search_content);

        Navigationbar = new NavigationBar(home,folder,privatecollect);
        Navigationbar.UpdateNavigationBarState(3);//初始化导航栏

        Searchbar = new SearchBar(search_button,search_text,search_content);

        //人物列表
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

        //人物编辑卡片
        ctrEditCard = findViewById(R.id.character_edit_card);
        edit_loyal_to = findViewById(R.id.edit_loyal_to);
        edit_name = findViewById(R.id.edit_profile_name);
        edit_birthday = findViewById(R.id.edit_birthday);
        edit_nativeplace = findViewById(R.id.edit_nativeplace);
        edit_story = findViewById(R.id.edit_story);
        edit_pic = findViewById(R.id.edit_profile_pic);
        edit_cancel = findViewById(R.id.cancel);
        edit_confirm = findViewById(R.id.confirm);
        editCard = new EditCard(edit_loyal_to,edit_name,edit_birthday,edit_nativeplace,edit_story,edit_pic,edit_cancel,edit_confirm,defaultPic);

        //菜单栏
        privatecollect_menu = findViewById(R.id.privatecollect_menu);
        add_profile_button = findViewById(R.id.add_profile_button);
    }

    private void refreshListCart(){
        ctr2Listitems.clear();
        Cursor person = db.rawQuery("select * from person where editable = \"1\"",null);
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
                ctr2Listitems.add(listitem);
            } while (person.moveToNext());
        }
        person.close();
        ctr2Adapter.notifyDataSetChanged();
    }

    private void SetListCardWithDeleteRecyclerView(){

        //初始化列表
        ctr2Listitems.clear();
        Cursor person = db.rawQuery("select * from person where editable = \"1\"",null);
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
                ctr2Listitems.add(listitem);
            } while (person.moveToNext());
        }
        person.close();

        //设置recyclerview布局
        ctr2RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //人物recyclerview适配器初始化，绑定数据，以及根据点击的人物跳转到PersonInformation（第二个Activity）中
        ctr2Adapter = new RecyclerViewAdapter<Map<String,Object>>(this, R.layout.list_card_delete, ctr2Listitems) {
            @Override
            public void convert(ViewHolder holder, Map<String, Object> M) {
                CircularImageView pic = holder.getView(R.id.profile_pic);
                pic.setImageBitmap((Bitmap) M.get("pic"));
                MyFontTextView name = holder.getView(R.id.profile_name);
                name.setText(M.get("name").toString());
                MyFontTextView country = holder.getView(R.id.nativeplace);
                country.setText(M.get("loyal_to").toString());

                ConstraintLayout layout = holder.getView(R.id.list_card_with_delete);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width,FrameLayout.LayoutParams.WRAP_CONTENT);
                layout.setLayoutParams(lp);
                //根据屏幕宽度动态设置layout宽度
            }
        };

        //侧滑菜单初始化效果设置
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                int width = getResources().getDimensionPixelSize(R.dimen.dp_80);
                {
                    SwipeMenuItem deleteItem = new SwipeMenuItem(PrivateCollectActivity.this)
                            .setImage(R.drawable.delete)
                            .setWidth(width)
                            .setHeight(height);
                    swipeRightMenu.addMenuItem(deleteItem);
                }
            }
        };
        ctr2RecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);

        //侧滑菜单中监听点击处理，具体为删除指定的人物，需同时在显示列表和人物列表中删除，即再次打开人物列表时不会再次出现
        SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                int position = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position
                String characterName = (String) ctr2Listitems.get(position).get("name");
                db.delete("person", "名字 = ?", new String[] { characterName });
                ctr2Listitems.remove(position);
                ctr2Adapter.notifyDataSetChanged();
            }
        };
        ctr2RecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);

        SetListCardWithDeleteListener();

        //设置人物recyclerview动画、适配器
        AlphaInAnimationAdapter animationAdapter1 = new AlphaInAnimationAdapter(ctr2Adapter);
        animationAdapter1.setDuration(1000);
        animationAdapter1.setFirstOnly(false);
        ctr2RecyclerView.setItemAnimator(new FadeInAnimator());
        ctr2RecyclerView.setAdapter(new ScaleInAnimationAdapter(animationAdapter1));
    }//初始化人物列表视图，带滑动删除功能

    private void SetListCardWithDeleteListener(){
        ctr2Adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                ViewGroup ItemView = (ViewGroup) ctr2RecyclerView.getLayoutManager().findViewByPosition(position);
                //MyFontTextView ctrName = (MyFontTextView) ItemView.getChildAt( 0);//获取到人物卡片中的人物名称View
                //String characterName = ctrName.getText().toString();
                String characterName = (String) ctr2Listitems.get(position).get("name");
                Toast.makeText(getApplicationContext(),characterName,Toast.LENGTH_SHORT).show();
                characterInfo ctrInfo = new characterInfo(characterName);
                characterCard.initCharacterProfileCard(ctrInfo,defaultPic);//初始化卡片
                setVisibilty(2);//显示人物详情卡片
            }
            @Override
            public void onLongClick(int positon) {

            }
        });
    }//人物列表点击监听器

    private int nowVisibilty;//当前可视情况
    private void setVisibilty(int Case){
        switch (Case){
            case 1:
                nowVisibilty = 1;
                ctr2RecyclerView.setVisibility(View.VISIBLE);
                privatecollect_menu.setVisibility(View.VISIBLE);
                ctrProfileCard.setVisibility(View.GONE);
                ctrEditCard.setVisibility(View.GONE);
                break;
            case 2:
                nowVisibilty = 2;
                ctr2RecyclerView.setVisibility(View.GONE);
                privatecollect_menu.setVisibility(View.GONE);
                ctrProfileCard.setVisibility(View.VISIBLE);
                ctrEditCard.setVisibility(View.GONE);
                break;
            case 3:
                nowVisibilty = 3;
                ctr2RecyclerView.setVisibility(View.GONE);
                privatecollect_menu.setVisibility(View.GONE);
                ctrProfileCard.setVisibility(View.GONE);
                ctrEditCard.setVisibility(View.VISIBLE);
                break;
            default:
                nowVisibilty = 0;
                break;
        }
    }//切换各视图的可见情况

    private void SetNavigationBarListener(){
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigationbar.UpdateNavigationBarState(1);
                Intent intent = new Intent(PrivateCollectActivity.this,MainActivity.class);
                intent.putExtra("navigationState",1);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(PrivateCollectActivity.this).toBundle());
            }
        });
        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigationbar.UpdateNavigationBarState(2);
                Intent intent = new Intent(PrivateCollectActivity.this,MainActivity.class);
                intent.putExtra("navigationState",2);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(PrivateCollectActivity.this).toBundle());
            }

        });//点击跳转到markActivity
        privatecollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigationbar.UpdateNavigationBarState(3);
                setVisibilty(1);
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
                if(nowVisibilty != 1)setVisibilty(1);//开始输入后，切换到显示内容
                String search = Searchbar.getSearchText();//获取搜索框内的文字
                //根据字符串Update列表
                //下面使用关键字人物名称生成列表
                String sql = "select * from person where 名字 like '%"+search+"%' and editable = 1";
                Cursor person = db.rawQuery(sql,null);
                ctr2Listitems.clear();
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
                        ctr2Listitems.add(listitem);
                    } while (person.moveToNext());
                }
                person.close();
                ctr2Adapter.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });//监听搜索框内的文本
    }//搜索框监听器，生成列表，待接入db

    private void SetEditCardListener(){
        edit_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editCard.checkLegal()){
                    edit_confirm.setImageResource(R.drawable.confirm_color);
                    editCard.setErrorMessages();//去除错误信息
                    if(editCard.saveCharacterProfile()){
                        characterCard.initCharacterProfileCard(editCard.getCharacterinfo(),defaultPic);
                        Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                        refreshListCart();
                        if (requestcode == 1) {
                            Intent intent = new Intent(PrivateCollectActivity.this,MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("name",(String) getIntent().getExtras().get("name"));
                            intent.putExtras(bundle);
                            setResult(2,intent);
                            finish();
                        }
                        else setVisibilty(1);//显示人物信息
                    }//保存人物信息成功
                    else{
                        Toast.makeText(getApplicationContext(),"保存失败",Toast.LENGTH_SHORT).show();
                    }//保存失败
                }//检查合法性通过
                else{
                    edit_confirm.setImageResource(R.drawable.confirm);
                    editCard.setErrorMessages();//显示错误信息
                }//检查合法性不通过
            }
        });//点击确定按钮
        edit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCard.InitEditCardWithNull();//清空editCard
                setVisibilty(1);//
            }
        });//点击取消按钮
        edit_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFromAlbum();//从相册获取图片
            }
        });//点击头像
    }//编辑人物卡片的监听器

    private void SetProfileCardListener(){
        characterCard.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(characterCard.getCharacterinfo().editable){
                    editCard.InitEditCardWithInfo(characterCard.getCharacterinfo());
                    setVisibilty(3);
                }
            }
        });//点击编辑按钮做出响应

    }//点击ProfileCard按钮，做出响应

    private void SetMenuListener(){
        add_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCard.InitEditCardWithNull();
                setVisibilty(3);
            }
        });
    }

    public void getPicFromAlbum(){
        Intent getAlbum = new Intent(Intent.ACTION_PICK, null);
        getAlbum.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(getAlbum,2);//requestCode为1
    }//从相册获取图片

}
