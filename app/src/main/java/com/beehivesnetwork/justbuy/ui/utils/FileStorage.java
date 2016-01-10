package com.beehivesnetwork.justbuy.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class FileStorage {

    private File fileDir;
    private Context context;
    public FileStorage(Context context) {
        // check if mobile has SDCARD
        this.context = context;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            fileDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    AppConstant.FILE_DIR);
        }

        else {
            fileDir = context.getCacheDir();
        }

        if (!fileDir.exists())
            fileDir.mkdirs();
        else {
            CommonUtils.LogD("API","MD File directory: " + fileDir.getAbsolutePath());
        }

    }


    public File getFile(String url) {
        String filename = CommonUtils.md5(url);
        File f = new File(fileDir, filename);
        return f;
    }

    public Bitmap getImageBitmap(String url) {
        try {
            return BitmapFactory.decodeFile(getFile(url).getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public File getDir() {
        return this.fileDir;
    }

    public long getCacheSize() {
        long totalSize = 0;
        File[] files = fileDir.listFiles();
        if (files == null)
            return 0;
        for (File f : files) {
            totalSize += f.length();
        }
        return totalSize;
    }



    public Object readObject(String fName) {

        FileInputStream fIn = null;
        ObjectInputStream isr = null;
        Object cachedObject = null;

        File feedFile = getFile(fName);
        if (!feedFile.exists()) {
            return null;
        }
        try {
            fIn = new FileInputStream(feedFile);
            isr = new ObjectInputStream(fIn);
            cachedObject = isr.readObject();
        }

        catch (Exception e) {
            CommonUtils.LogI("Calendar Manager","file exception: "+e.getMessage());
            CommonUtils.LogI("Calendar Manager","file exception: "+isr);
            e.printStackTrace();
        }

        finally {
            try {
                isr.close();
                fIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return cachedObject;

    }

    public void writeObject(String fName, Object object){
        ObjectOutput out = null;

        try {
            File file = getFile(fName);
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(object);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        File[] files = fileDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

    public void deleteFileByName(String name){
        String filename = CommonUtils.md5(name);
        File f = new File(fileDir, filename);
        CommonUtils.LogI("Delete Cal", "f.exists(): "+f.exists());
        if(f.exists()){
            f.delete();
        }
    }


}