package com.mengshitech.colorrun.releaseshow;

//有问题发邮箱:wschenyongyin@qq.com

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mengshitech.colorrun.R;
import com.mengshitech.colorrun.adapter.ReleaseShowGridViewAdapter;
import com.mengshitech.colorrun.bean.CommentEntity;
import com.mengshitech.colorrun.customcontrols.ChoseImageDiaLog;
import com.mengshitech.colorrun.customcontrols.ProgressDialog;
import com.mengshitech.colorrun.fragment.lerun.ShowMap;
import com.mengshitech.colorrun.utils.CompressImage;
import com.mengshitech.colorrun.utils.ContentCommon;
import com.mengshitech.colorrun.utils.HttpUtils;
import com.mengshitech.colorrun.utils.JsonTools;
import com.mengshitech.colorrun.utils.upload;


import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import org.json.JSONException;

public class ReleaseShowActivity extends Activity implements OnClickListener {
    private ChoseImageDiaLog dialog;

    ListView listView;
    GridView gridView;
    // ArrayList<String> listfile = new ArrayList<String>();
    List<String> listfile = new ArrayList<String>();
    List<String> compressfile = new ArrayList<String>();
    Bitmap bmp;
    int count;
    LinearLayout ll_send, ll_cancel;
    EditText et_text;
    String content;
    String imageFilePath;
    File temp;
    String servletPath = "http://192.168.0.19:8080/ServletForUpload/servlet/ImageUploadServlet";
    private String evaluate_content;
    String success_imagePath;

    private FrameLayout frameLayout;
    private LinearLayout linearLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.releaseshow_layout);
        listView = (ListView) findViewById(R.id.listView1);
        ll_send = (LinearLayout) findViewById(R.id.ll_send);
        ll_cancel = (LinearLayout) findViewById(R.id.ll_cancel);
        et_text = (EditText) findViewById(R.id.et_text);
        linearLayout = (LinearLayout) findViewById(R.id.ll_reshow);
        frameLayout = (FrameLayout) findViewById(R.id.fm_reshow);

        progressDialog = ProgressDialog.show(ReleaseShowActivity.this, "正在发表");
        intDatas();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.getStringArrayList("files") != null) {
                listfile = bundle.getStringArrayList("files");
                count = listfile.size() + 1;
                try {
                    compressfile = compressImage(listfile);
                    ReleaseShowGridViewAdapter adapter = new ReleaseShowGridViewAdapter(
                            ReleaseShowActivity.this, compressfile, count, bmp);
                    gridView.setAdapter(adapter);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            if (bundle.getString("evaluate_content") != null) {
                String content = bundle.getString("evaluate_content");
                et_text.setText(content + "");
            }

        }

        ll_send.setOnClickListener(this);
        ll_cancel.setOnClickListener(this);

    }

    public void showDailog() {
        dialog = new ChoseImageDiaLog(ReleaseShowActivity.this, R.layout.dialog_choseimage,
                R.style.dialog, new ChoseImageDiaLog.LeaveMyDialogListener() {

            @Override
            public void onClick(View view) {

                System.out.println("aaaaaaaaaaaaaa");
                switch (view.getId()) {
                    case R.id.btn_takephoto:

                        imageFilePath = Environment
                                .getExternalStorageDirectory()
                                .getAbsolutePath()
                                + "/showpicture.jpg";
                        listfile.add(imageFilePath);
                        temp = new File(imageFilePath);
                        Uri imageFileUri = Uri.fromFile(temp);// 获取文件的Uri
                        Intent it = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);// 跳转到相机Activity
                        it.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                imageFileUri);// 告诉相机拍摄完毕输出图片到指定的Uri
                        startActivityForResult(it, 102);
                        dialog.dismiss();
                        break;
                    case R.id.btn_picture:
                        evaluate_content = et_text.getText().toString();
                        Intent intent = new Intent(ReleaseShowActivity.this,
                                ImgFileListActivity.class);
                        intent.putExtra("evaluate_content", evaluate_content);

                        startActivity(intent);

                        dialog.dismiss();
                        finish();

                        break;
                    case R.id.btn_cancel:

                        dialog.dismiss();
                        break;

                    default:
                        break;
                }

            }
        });
        // 设置dialog弹出框显示在底部，并且宽度和屏幕一样
        Window window = dialog.getWindow();
        dialog.show();
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    public void chise() {
        Intent intent = new Intent();
        intent.setClass(this, ImgFileListActivity.class);
        startActivity(intent);
    }

    public void intDatas() {
        Resources res = getResources();
        gridView = (GridView) findViewById(R.id.noScrollgridview);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        bmp = BitmapFactory.decodeResource(res, R.mipmap.icon_addpic_focused);
        count = 1;
        ReleaseShowGridViewAdapter adapter = new ReleaseShowGridViewAdapter(ReleaseShowActivity.this, bmp,
                count);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new GridViewItemOnClick2());

    }

    public class GridViewItemOnClick2 implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            if (position + 1 == count) {

                showDailog();
            } else {
                String image_path = compressfile.get(position);
                ShowMap show = new ShowMap(ReleaseShowActivity.this, image_path, frameLayout, linearLayout);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fm_reshow, show).addToBackStack(null).commit();
                frameLayout.setVisibility(View.VISIBLE);

            }

        }
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {

            upload load = new upload();
            try {
                String result = load.uploadListImage(compressfile, ContentCommon.ImagePath);
                Message msg = new Message();
                msg.obj = result;
                loadhandler.sendMessage(msg);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    Handler loadhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String result = (String) msg.obj;

            if (result.equals("failure") || result.equals("") || result == null) {
                Toast.makeText(ReleaseShowActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else {
                try {
                    deleteFile(compressfile);
                    success_imagePath = JsonTools.getDatas(result);
                    new Thread(ReleaseShowRunnable).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }


        }
    };


    Runnable ReleaseShowRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("flag", "show");
            map.put("index", "0");
            map.put("show_content", content);
            map.put("show_image", success_imagePath);
            map.put("user_id", ContentCommon.user_id);

            String result = HttpUtils.sendHttpClientPost(ContentCommon.PATH, map, "utf-8");

            Message msg = new Message();
            msg.obj = result;
            ReleaseShowhanlder.sendMessage(msg);


        }
    };
    Handler ReleaseShowhanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String result = (String) msg.obj;
            if (result.equals("timeout")) {
                Toast.makeText(ReleaseShowActivity.this, "连接服务器超时", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (result.equals("1")) {
                progressDialog.dismiss();
                finish();
            } else {
                progressDialog.dismiss();
                Toast.makeText(ReleaseShowActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_send:
                content = et_text.getText().toString();
                if (listfile != null && listfile.size() != 0 && content != null) {
                    progressDialog.show();
                    new Thread(runnable).start();

                } else if ((listfile == null || listfile.size() == 0) && content != null && !content.equals("")) {
                    success_imagePath = "";
                    new Thread(ReleaseShowRunnable).start();
                } else {
                    Toast.makeText(ReleaseShowActivity.this, "请输入发布的内容",
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.ll_cancel:

                finish();
                break;

            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 102) {

            // Bitmap bmp = BitmapFactory.decodeFile(imageFilePath);
            listfile.add(imageFilePath);
            count = listfile.size() + 1;
            ReleaseShowGridViewAdapter adapter = new ReleaseShowGridViewAdapter(ReleaseShowActivity.this,
                    listfile, count, bmp);
            gridView.setAdapter(adapter);

        }
    }


    //对取回来的图片进行压缩

    private List<String> compressImage(List<String> list) throws IOException {
        List<String> imageList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i));
            String compressimage = null;

            String imagepath = list.get(i);
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            long size = fis.available();

            //当图片小于1M时 不进行图片压缩
            if (size < 1048576) {
                compressimage = imagepath;
            } else {

                compressimage = CompressImage.compressBitmap(ReleaseShowActivity.this, imagepath, 300, 300, true);
            }
            imageList.add(compressimage);
        }

        return imageList;
    }

    //删除缓存图片
    public static boolean deleteFile(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i));

            if (file.exists() && file.isFile()) {
                if (file.delete()) {

                } else {
                    System.out.println("删除旧头像失败！");
                }
            } else {
            }
        }
        return false;
    }
}
