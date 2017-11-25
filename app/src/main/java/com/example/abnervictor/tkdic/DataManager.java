package com.example.abnervictor.tkdic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Geng on 2017/11/23.
 */

public class DataManager {
    private final int BUFFER_SIZE = 500000;
    //保存的数据库的文件名称
    public static final String DB_NAME = "ThreeKindom";
    //该工程的包名称
    public static final String PACKAGE_NAME = "com.example.abnervictor.tkdic";
    //在手机当中存放数据库的位置
    public static final String DB_PATH = Environment.getDataDirectory().getAbsolutePath() + "/data/"
            + PACKAGE_NAME;
    private SQLiteDatabase database;
    private Context mContext;

    public DataManager(Context context) {
        mContext = context;
    }


    public SQLiteDatabase openDatabase(String dbpath) {
        SQLiteDatabase db = null;
        String dbfile = DB_PATH +"/databases/"+dbpath;
        try {
            //判断数据库文件是否存在，如果不存在直接导入，否则直接打开
            if (!(new File(dbfile).exists())) {
                String dirpath = DB_PATH +"/databases";
                File dir=new File(dirpath);
                if(!dir.exists()) {//防止databases文件夹不存在，不然，会报ENOENT (No such file or directory)的异常
                    dir.mkdirs();
                }
                InputStream is = mContext.getResources().openRawResource(R.raw.threekindom);
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            db = SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);
        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return db;
    }
}