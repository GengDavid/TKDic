package com.example.abnervictor.tkdic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by abnervictor on 2017/11/24.
 */

public class FileHelper {
    String RootPath;
    public FileHelper(String rootPath){
        setRootPath(rootPath);
    }

    public void setRootPath(String rootPath){
        RootPath = rootPath;
        Log.v("AppRootPath",rootPath);
    }//设置文件操作的根目录

    public boolean createFolder(String Path, String foldername){
        String folderPath = Path + File.separator + foldername;
        if (!folderPath.endsWith(File.separator)) folderPath += File.separator;//为文件夹路径后面加上分隔符
        if (!Path.endsWith(File.separator)) Path += File.separator;//为上级目录路径后面加上分隔符

        File pathfile = new File(Path);
        if (pathfile.exists() && pathfile.isDirectory()){
            Log.v("createFolder","path '"+ Path + "' is a Directory");
            File folderfile = new File(folderPath);
            if (!folderfile.exists()){
                Log.v("createFolder","create folder '"+ folderPath + "' success!");
                return folderfile.mkdir();
            }//文件夹不存在，新建文件夹
            else if(folderfile.isDirectory()){
                Log.v("createFolder","folder '"+ folderPath + "' already exists!");
                return true;
            }
        }//上级目录存在，进行操作
        else Log.e("createFolder","path '"+ Path + "' doesn't Exists or is not a Directory!");
        return false;

    }//在指定路径下创建文件夹，验证成功

    public boolean deleteFolder(String Folder){
        String FolderPath = RootPath + File.separator + Folder;
//        if (!FolderPath.endsWith(File.separator)) FolderPath += File.separator;
        File folder = new File(FolderPath);
        if (folder.exists() && folder.isDirectory()){
            File files[] = folder.listFiles();
            for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                if (files[i].isFile()){
                    if(!files[i].delete()){
                        return false;
                    }
                }// 把每个文件 用这个方法进行迭代
                else if(files[i].isDirectory()){
                    return false;
                }// 文件夹内有文件夹，此处其实可以做递归调用删除
            }
            return folder.delete();
        }
        else {
            Log.e("deleteFolder","folder '"+ Folder + "' doesn't exist!");
            return false;
        }
    }//删除根目录下的文件夹

    public boolean copyRawToFolder(Context context, int resId, String Folder, String filename, String filetype){
        String FolderPath = RootPath + File.separator + Folder;
        String externalStorage = Environment.getExternalStorageState();//判断外部储存器状态
        if (externalStorage.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(FolderPath);
            if (!file.exists()) {
                file.mkdir();
            }//如果文件夹不存在，那么新建文件夹
            String FilePath = FolderPath + File.separator + filename + "." + filetype;
            InputStream inputStream = context.getResources().openRawResource(resId);
            return copyFilesFromStream(inputStream, FilePath);
        }
        else return false;
    }//在根目录的指定文件夹下保存文件,验证成功

    private boolean copyFilesFromStream(InputStream inputStream, String Path){
        File file = new File(Path);
        try{
            if (!file.exists()){
                //建立通道对象
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                //定义储存空间
                byte[] buffer = new byte[inputStream.available()];
                //开始读文件
                int length = 0;
                while ((length = inputStream.read(buffer)) != -1){
                    //将buffer重的数据写到outputStream对象中
                    fileOutputStream.write(buffer, 0, length);
                }//循环从输入流读取buffer字节
                fileOutputStream.flush();//刷新缓冲区
                fileOutputStream.close();//关闭流
                inputStream.close();
                Log.v("copyFilesFromStream","file is copied to "+Path);
            }//文件不存在时才进行复制
            else{
                Log.v("copyFilesFromStream","file " + Path + " already exists!");
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }//将文件流写入到指定路径，验证成功

    public boolean copyBitmapToFolder(Bitmap bitmap, String Folder, String filename){
        String FolderPath = RootPath + File.separator + Folder;
        String externalStorage = Environment.getExternalStorageState();//判断外部储存器状态
        if (externalStorage.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(FolderPath);
            if (!file.exists()) {
                return false;
            }//如果文件夹不存在，不继续进行写入
            String FilePath = FolderPath + File.separator + filename + ".bmp";
            File picfile = new File (FilePath);
            try {
                if (picfile.exists()){
                    file.delete();
                    Log.v("copyBitmapToFolder","bitmap '"+ FilePath + "' already exists!");
                }
                FileOutputStream fileOutputStream = new FileOutputStream(picfile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Log.v("copyBitmapToFolder","bitmap '"+ FilePath + "' copied!");
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }//保存bitmap到指定文件夹，验证成功

    public Bitmap getBitmapFromFolder(String Folder, String filename, String filetype){
        String FolderPath = RootPath + File.separator + Folder;
        String externalStorage = Environment.getExternalStorageState();//判断外部储存器状态
        if (externalStorage.equals(Environment.MEDIA_MOUNTED)) {
            File folder = new File(FolderPath);
            if (!folder.exists()) {
                Log.e("getBitmapFromFolder","picture folder '"+ FolderPath + "' doesn't Exists!");
                return null;
            }//如果文件夹不存在，返回空值
            String FilePath = FolderPath + File.separator + filename + "." + filetype;
            File file = new File(FilePath);
            if (file.exists()){
                try{
                    Log.v("getBitmapFromFolder","picture '"+ FilePath + "' Exists!");
                    Bitmap bitmap = BitmapFactory.decodeFile(FilePath);
                    return bitmap;
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
            else{
                Log.e("getBitmapFromFolder","picture '"+ FilePath + "' doesn't Exists!");
                return null;
            }//如果文件不存在，返回空值
        }
        return null;
    }//从根目录/Folder/下读取图片文件并转化为bitmap返回，为了方便，在保存图片到文件夹时最好统一使用bmp格式，先转化图像为bitmap在保存

    public SQLiteDatabase openDatabase(Context context, int resId, String databaseName){
        SQLiteDatabase db = null;
        File databasefolder = new File(RootPath +"/databases/");
        if (!databasefolder.exists()){
            Log.e("openDatabase","folder" + RootPath +"/databases is not yet created! Create this folder now...");
            createFolder(RootPath,"databases");
        }//检查数据库文件夹是否存在
        else{
            String dbfile = RootPath +"/databases/" + databaseName+".db";
            //判断数据库文件是否存在，如果不存在直接导入，否则直接打开
            if (!(new File(dbfile).exists())) {
                Log.v("openDatabase","database" +dbfile+ " not exists! Copying to " + RootPath +"/databases/ now...");
                copyRawToFolder(context, resId,"databases",databaseName,"db");//将文件复制到
            }
            else {
                Log.v("openDatabase","database" +dbfile+ " exists! Now open...");
            }
            db = SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);
        }
        return db;
    }//从根目录/databases/下读取数据库，如果文件夹不存在会自动创建，如果数据库不存在会自动复制到文件夹

    public boolean deleteFileFromFolder(String Folder, String filename, String filetype){
        String FolderPath = RootPath + File.separator + Folder;
        String externalStorage = Environment.getExternalStorageState();//判断外部储存器状态
        if (externalStorage.equals(Environment.MEDIA_MOUNTED)) {
            File folder = new File(FolderPath);
            if (folder.exists()){
                String FilePath = FolderPath + File.separator + filename + "." + filetype;
                File file = new File(FilePath);
                if (file.exists() && file.isFile()){
                    return file.delete();
                }//如果文件存在那么删除
                else{
                    Log.e("deleteFileFromFolder","file '"+ filename + "." + filetype + "' doesn't exist or is not a file!");
                    return true;
                }
            }//如果文件夹存在，那么查找文件并删除
            else{
                Log.e("deleteFileFromFolder","folder '"+ Folder + "' doesn't exist!");
                return false;
            }
        }
        else return false;//外部储存器不存在
    }//从根目录下的指定文件夹中删除文件

}
