package ceshi.zxf.com.utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 */
public class FileUtil {
    private Activity context;
    private static final int BUFF_SIZE = 4 * 1024 ;

    public FileUtil() {
        super();
    }

    public FileUtil(Activity context) {
        super();
        this.context = context;
    }


    public String getPathFromUri(Uri fileUri){
        String fileName = null;
        Uri filePathUri = fileUri;
        if (fileUri!=null) {
            if (fileUri.getScheme().toString().compareTo("content")==0) {
                //content://开头的uri
                Cursor cursor = context.getContentResolver().query(fileUri, null, null, null, null);
                if (cursor!=null&&cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    fileName = cursor.getString(column_index);//取出文件路径
                    if (Integer.parseInt(Build.VERSION.SDK)<14) {
                        cursor.close();
                    }
                }
            }else if(fileUri.getScheme().compareTo("file")==0){//file://开头的uri
                fileName = filePathUri.toString();
                fileName = filePathUri.toString().replace("file://", "");
                if (fileName.indexOf("/storage")==-1) {
                    int index = fileName.indexOf("/sdcard");
                    fileName = index==-1?fileName:fileName.substring(index);
                    if (!fileName.startsWith("/mnt")) {
                        fileName = "/mnt"+fileName;
                    }
                }
            }
        }

        fileName = Uri.decode(fileName);
        return fileName;
    }


    /**
     * 删除单个文件
     *
     * @param fileName
     *            被删除文件的文件名
     * @return 单个文件删除成功返回true,否则返回false
     */
    public static boolean deleteFiles(String fileName) {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        } else {
            System.out.println("删除单个文件" + fileName + "失败！");
            return false;
        }
    }

    /**
     *  [文件拷贝]
     * @param srcFile
     * @param newFile
     * @return
     */
    public static File copyFile(File srcFile, File newFile){
        BufferedInputStream inBuffer = null;
        BufferedOutputStream outBuffer = null;
        try{
            inBuffer = new BufferedInputStream(new FileInputStream(srcFile));
            outBuffer = new BufferedOutputStream(new FileOutputStream(newFile));

            byte[] b = new byte[1024*5];
            int len;
            while((len = inBuffer.read(b)) != -1){
                outBuffer.write(b, 0, len);
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(inBuffer!=null){
                try {
                    inBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outBuffer!=null){
                try {
                    outBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return newFile;
    }



}
