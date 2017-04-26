package ceshi.zxf.com.utils;

import android.os.Environment;

/**
 * Created by admin on 2016/3/7.
 */
public class FilePaths {

    public static String BaseFileDre = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.CheShi";
    public static String Model = BaseFileDre + "/model";// 实体文件夹


    public static String AUDIOPATH = "audio";//记录页存放录音的文件夹
    public static String VIDEOPATH = "video";//记录页存放录像的文件夹


    public static String DRAWACTIVITY_LUYIN_SUFFIX = ".amr";//录音文件的后缀名
    //public static String DRAWACTIVITY_SHIPIN_SUFFIX = ".3gp";//视频文件的后缀名
}
