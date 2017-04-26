package ceshi.zxf.com.cheshi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import ceshi.zxf.com.audio.AttachmentModel;
import ceshi.zxf.com.audio.AudioRecordView;
import ceshi.zxf.com.audio.RecordDialog;
import ceshi.zxf.com.camera.CropImageIntentBuilder;
import ceshi.zxf.com.utils.FilePaths;
import ceshi.zxf.com.utils.FileUtil;

public class MainActivity extends AppCompatActivity {

    Button btn_sys;
    Button btn_img;
    Button btn_luyin;
    Button btn_luxiang ;

    //------------------传图片----------------------------//
    // 获取图像
    protected static final int GET_HEAD_VIA_CAMERA = 10000;
    protected static final int GET_HEAD_VIA_FILE = 10001;
    // 裁剪图像
    private static final int CROP_HEAD_IMG = 20000;
    //文件名
    protected static final String HEAD_TMP_FILENAME = "head.tmp.jpg";
    private static final String HEAD_IMG_THUMBNAIL = "head.jpg";
    // 图像宽高
    public static final int HEAD_IMG_WIDTH = 400;//300
    // 裁剪框的颜色
    private static final int CROP_OUTLINE_COLOR = 0xFF03A9F4;
    //------------------------------------------------------//

    //------------------录音--------------------------------//
    private AudioRecordView recordview;
    private RecordDialog addRecord;
    private TextView AudioRecord_MinView;
    private TextView AudioRecord_SecondsView;
    private String audiopath;// 录音路径
    private String audiochachename ="";//缓存名字
    //-----------------------------------------------------//

    //-----------------------------------------------------//
    private static final int MENU_TOP_SHIPIN = 2;// 2：添加视频附件
    private String videopath;// 录像路径
    //-----------------------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_sys = (Button) this.findViewById(R.id.btn_sys);
        btn_sys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaoYiSao();
            }
        });

        btn_img = (Button) this.findViewById(R.id.btn_img);
        btn_img.setVisibility(View.VISIBLE);
        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeImg();
            }
        });


        btn_luyin = (Button) this.findViewById(R.id.btn_luyin);
        btn_luyin.setVisibility(View.VISIBLE);
        btn_luyin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Audio();
            }
        });

        btn_luxiang = (Button) this.findViewById(R.id.btn_luxiang);
        btn_luxiang.setVisibility(View.VISIBLE);
        btn_luxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Video();
            }
        });

    }

    //1.扫码跳转
    protected void SaoYiSao(){
        startActivity(new Intent(this, SaoMaActivity.class));
    }
    //2.上传图片：提示框
    protected void ChangeImg(){
        ChuanTu();
    }
    //3.录音
    protected void Audio(){
        RecordAudio();
    }
    //4.录像
    protected void Video(){
        RecordVideo();
    }



    //录像
    protected void RecordVideo(){
        //-----.CheShi/model/video/
        videopath = FilePaths.Model + File.separator + FilePaths.VIDEOPATH + File.separator;

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);//比低质量0高
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 70000);//秒
        startActivityForResult(intent, 2);
    }
    /**
     * 把选择的附件拷贝到对应目录下
     * @param srcpath 源文件路径
     * @param despath 要保存到的路径
     */
    private boolean copyAttachment(String srcpath, String despath, String type) {
        File srcpathFile = new File(srcpath);
        boolean flag = false;
        if (srcpathFile.exists()) {
            File desfile = new File(despath);
            if (!desfile.exists()) {
                desfile.mkdirs();
            }
            File newfile = new File(despath + File.separator + srcpathFile.getName());
            if (!newfile.exists()) {
                FileUtil.copyFile(srcpathFile, newfile);
                flag = true;
            } else {
                Toast.makeText(this, "该附件已存在，请勿重复添加！", Toast.LENGTH_SHORT).show();
                flag = false;
            }
        }
        return flag;
    }




    //录音
    protected void RecordAudio(){
        //-----.CheShi/model/audio/
        audiopath = FilePaths.Model + File.separator + FilePaths.AUDIOPATH + File.separator;

        if (addRecord == null) {
            recordview = new AudioRecordView(this, audiopath, true);
            AudioRecord_MinView = (TextView) recordview.findViewById(R.id.AudioRecord_MinView);
            AudioRecord_SecondsView = (TextView) recordview.findViewById(R.id.AudioRecord_SecondsView);
            recordview.showRecord();
            recordview.addListener(audiolistener);
            addRecord = new RecordDialog(this, "录制音频", generalHandler, recordview, dialoglistener);
        }
        //createaudio();
        addRecord.setIsshowing(true);
        addRecord.showDialog();
    }
    /**
     * 录音返回handler
     */
    Handler generalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bd = null;
            String type;
            boolean flag;

            switch (msg.what) {
                case 40: //录音记录
                    bd = msg.getData();
                    type = bd.getString("type");
                    if (type.equals("1")) {// 录音完成
                        String audioname = bd.getString("audioname");
                        audiochachename = audioname;
                        if(recordview.isalert == 1 && audiochachename!=null && !audiochachename.equals("")){
                            showalert();
                            recordview.isalert = 0;
                        }
                        addRecord = null;
                        //asas();
                        //cancelTimerAudio();
                    } else if (type.equals("2")) {// 录音未完成
                        //createaudio();
                        //startTimerAudio();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    DialogInterface.OnKeyListener dialoglistener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_UP) {
                if (addRecord.isshowing && addRecord != null) {
                    recordview.jieshu();
                    return false;
                }
            }
            return false;
        }
    };
    public AudioRecordView.addMyTextChange audiolistener = new AudioRecordView.addMyTextChange() {
        @Override
        public void change(String time) {
            // audio_window_time.setText(time);
            try {
            } catch (NullPointerException e) {
                // TODO: handle exception
            }
        }
    };

    public void showalert(){
        String con = "录音结束，保存该录音文件吗？";
        new AlertDialog.Builder(MainActivity.this).setTitle("确认信息")//设置对话框标题
                .setMessage(con)//设置显示的内容
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //提示加入附件列表
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAudio();
                    }
                })
                .setCancelable(false)
                .show();
    }
    public void deleteAudio(){
        if(!audiochachename.equals("")){
            String filepath = audiopath + audiochachename;
            File file = new File(filepath);
            if (file.exists()) {
                file.delete();
                audiochachename = "";
            }
        }
    }



    //上传图片
    protected void ChuanTu(){
        //展示框
        AlertDialog.Builder headDlg = new AlertDialog.Builder(this);
        headDlg.setTitle("上传图片");
        headDlg.setItems(getResources().getStringArray(R.array.insert_images), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    //拍照
                    case 0:
                        String status= Environment.getExternalStorageState();
                        if(status.equals(Environment.MEDIA_MOUNTED)){
                            try {
                                Intent intent =new Intent();
                                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTmpFile(MainActivity.this, HEAD_TMP_FILENAME)));
                                startActivityForResult(intent, GET_HEAD_VIA_CAMERA);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "没有存储目录", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "没有存储卡",Toast.LENGTH_LONG ).show();
                        }
                        dialog.dismiss();
                        break;
                    //SD卡图片
                    case 1:
                        Intent i3=new Intent(Intent.ACTION_GET_CONTENT);
                        i3.setType("image/*");
                        startActivityForResult(i3, GET_HEAD_VIA_FILE);
                        dialog.dismiss();
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });
        headDlg.show();
    }
    /**
     * 获取临时文件
     * @param context
     * @param fileName
     * @return
     */
    private File getTmpFile(Context context, String fileName) {
        //String userId = isLogin() ? loginUser.getUserID() : "";
        String name =  FilePaths.BaseFileDre + File.separator + "cache";
        File dir = new File(name);
        //路径不存在创建
        if (!dir.exists())
            dir.mkdirs();
        //返回该路径下的创建好的临时文件
        return new File(dir, fileName);
    }
    /**
     * 返回Activity 裁剪处理
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case MENU_TOP_SHIPIN://录像
                    try {
                        if (data != null) {
                            String videoPath = new FileUtil(this).getPathFromUri(data.getData());
                            copyAttachment(videoPath, videopath, "1");
                            FileUtil.deleteFiles(videoPath);
                        }
                    } catch (Exception io_e) {
                        io_e.printStackTrace();
                        Toast.makeText(this, "录制视频失败!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GET_HEAD_VIA_CAMERA:
                case GET_HEAD_VIA_FILE:

                    Uri headSrcImg = null;

                    if(requestCode == GET_HEAD_VIA_CAMERA){
                        //拍照后为——：临时文件路径数据
                        // file:///storage/sdcard0/.ElectronMember/cache/head.tmp.jpg
                        headSrcImg = Uri.fromFile(getTmpFile(this, HEAD_TMP_FILENAME));
                    }else{
                        //得到选中的图片数据
                        //uri: content://media/external/images/media/11820
                        Uri uri = data.getData();
                        //查询后 结果包装在一个 Cursor 对象中返回
                        Cursor cursor = managedQuery(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                        //访问 Cursor 的下标获得其中的数据
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();

                        //选取图片后为——:选取文件路径数据
                        // file:///storage/sdcard0/aoyiplat/xxx.png
                        headSrcImg = Uri.fromFile(new File(cursor.getString(column_index)));
                    }

                    Uri headCroppedImg = Uri.fromFile(getTmpFile(this, HEAD_IMG_THUMBNAIL));
                    CropImageIntentBuilder headCropImg = new CropImageIntentBuilder(HEAD_IMG_WIDTH, HEAD_IMG_WIDTH, headCroppedImg);
//				不裁剪为圆形的图片，避免某些地方需要用到方形的头像。
//				headCropImg.setOutlineCircleColor(CROP_OUTLINE_COLOR);
//				headCropImg.setCircleCrop(true);
                    headCropImg.setOutlineColor(CROP_OUTLINE_COLOR);
                    headCropImg.setSourceImage(headSrcImg);

                    startActivityForResult(headCropImg.getIntent(this), CROP_HEAD_IMG);

                    break;
                case CROP_HEAD_IMG:
                    // 裁剪完成后删除临时文件，并保存到服务器
                    getTmpFile(this, HEAD_TMP_FILENAME).delete();
                    //图像上传到 服务端
                    //saveInfoServer();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
