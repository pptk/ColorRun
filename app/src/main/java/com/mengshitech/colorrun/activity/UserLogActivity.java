package com.mengshitech.colorrun.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.mengshitech.colorrun.R;
import com.mengshitech.colorrun.customcontrols.ChoseImageDiaLog;
import com.mengshitech.colorrun.utils.IPAddress;

import java.io.File;
import java.io.IOException;

/**
 * 作者：wschenyongyin on 2016/8/3 14:57
 * 说明:
 */
public class UserLogActivity extends Activity implements View.OnClickListener {
    private ImageView user_image;
    private ImageView image_chose;
    private LinearLayout ll_back;
    private ChoseImageDiaLog diaLog;
    private String imageFilePath;
    private File temp;
    String ScuessImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_detail_userlog);
        init();
        Log.i("user_log", IPAddress.user_log + "ssss");
    }

    private void init() {
        image_chose = (ImageView) findViewById(R.id.image_chose);
        user_image = (ImageView) findViewById(R.id.user_image);
        ll_back = (LinearLayout) findViewById(R.id.userlog_btn_back);
        Glide.with(UserLogActivity.this).load(IPAddress.user_log).into(user_image);
        ll_back.setOnClickListener(this);
        image_chose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //后退
            case R.id.image_chose:
                diaLog = new ChoseImageDiaLog(UserLogActivity.this, R.layout.dialog_choseimage, R.style.dialog,
                        new ChoseImageDiaLog.LeaveMyDialogListener() {
                            @Override
                            public void onClick(View view) {
                                switch (view.getId()) {
                                    case R.id.btn_takephoto:
                                        imageFilePath = Environment
                                                .getExternalStorageDirectory()
                                                .getAbsolutePath()
                                                + "/filename.jpg";
                                        temp = new File(imageFilePath);
                                        Uri imageFileUri = Uri.fromFile(temp);// 获取文件的Uri
                                        Intent it = new Intent(
                                                MediaStore.ACTION_IMAGE_CAPTURE);// 跳转到相机Activity
                                        it.putExtra(
                                                android.provider.MediaStore.EXTRA_OUTPUT,
                                                imageFileUri);// 告诉相机拍摄完毕输出图片到指定的Uri
                                        startActivityForResult(it, 102);
                                        diaLog.dismiss();
                                        break;
                                    case R.id.btn_picture:
                                        Intent intent1 = new Intent(Intent.ACTION_PICK
                                        );
                                        intent1.setDataAndType(
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                        startActivityForResult(intent1, 103);
                                        diaLog.dismiss();
                                        break;
                                    case R.id.btn_cancel:
                                        diaLog.dismiss();
                                        break;

                                    default:
                                        break;
                                }
                            }
                        });

                Window window = diaLog.getWindow();
                diaLog.show();
                window.setGravity(Gravity.BOTTOM);
                window.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                break;
            //选择图片
            case R.id.userlog_btn_back:
                finish();
                break;


            default:
                break;
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 102:
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, options);

//                    Bitmap bmp = BitmapFactory.decodeFile(imageFilePath);
                    user_image.setImageBitmap(bmp);

                }
                break;
            case 103:
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
                ContentResolver resolver = getContentResolver();


                if (data != null) {
                    Uri originalUri = data.getData(); // 获得图片的uri

                    try {
                        Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // 显得到bitmap图片
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 这里开始的第二部分，获取图片的路径：

                    String[] proj = {MediaStore.Images.Media.DATA};

                    // 好像是android多媒体数据库的封装接口，具体的看Android文档
                    @SuppressWarnings("deprecation")
                    Cursor cursor = managedQuery(originalUri, proj, null, null,
                            null);
                    // 按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    // 最后根据索引值获取图片路径
                    imageFilePath = cursor.getString(column_index);
                    Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, options);
                    user_image.setImageBitmap(bmp);

                    temp = new File(imageFilePath);

                    System.out.println(imageFilePath);
                }

                break;
        }

    }
}