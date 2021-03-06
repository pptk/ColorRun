package com.mengshitech.colorrun.fragment.lerun;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mengshitech.colorrun.R;
import com.mengshitech.colorrun.customcontrols.ProgressDialog;
import com.mengshitech.colorrun.fragment.BaseFragment;
import com.mengshitech.colorrun.fragment.me.myLeRunFragment;
import com.mengshitech.colorrun.utils.ContentCommon;
import com.mengshitech.colorrun.utils.Utility;

/**
 * 作者：wschenyongyin on 2016/8/24 11:46
 * 说明:
 */
public class DisplayQRcodeFragment extends BaseFragment {

    View view;
    Context context;
    private TextView title;
    private TextView tv_state;
    private TextView tv_countdown;
    private ImageView qrcodeImage;
    private int type;
    private String codeimage;
    private int time = 3;
    private ImageView btn_back;
    RelativeLayout rl_title;
    FragmentManager fm;

    @Override
    public View initView() {
        context = getContext();
        fm = getFragmentManager();
        view = View.inflate(context, R.layout.activity_displayqrcode, null);
        rl_title = (RelativeLayout) view.findViewById(R.id.title_back_ll);
        title = (TextView) view.findViewById(R.id.title_barr);
        tv_state = (TextView) view.findViewById(R.id.tv_state);
        tv_countdown = (TextView) view.findViewById(R.id.tv_countdown);
        qrcodeImage = (ImageView) view.findViewById(R.id.qrcodeImage);
        btn_back = (ImageView) view.findViewById(R.id.title_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.popBackStack();
            }
        });
        getDatas();
        return view;
    }

    private void getDatas() {
        type = getArguments().getInt("type");
        codeimage = getArguments().getString("qrcode_image");
        Glide.with(context).load(ContentCommon.path + codeimage).error(R.mipmap.defaut_error_square).into(qrcodeImage);
        switch (type) {
            //主页查看二维码

            case 1:
                Log.i("codeimage", codeimage + "");
                tv_state.setText("凭此二维码进行签到");
                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fm.popBackStack();
                    }
                });
                break;
            case 2:
//                title.setText("报名结果");
                rl_title.setVisibility(View.GONE);
                tv_state.setText("报名成功");
                getFocus();
                handler.sendEmptyMessageDelayed(0, 1000);
                break;
            case 3:
                tv_state.setText("正在审核中，请耐心等待！");
                break;
            case 4:
                tv_state.setText("凭此二维码到现场签到！");
                break;
            case 5:
                handler.sendEmptyMessageDelayed(0, 1000);
                tv_state.setText("凭此二维码到现场签到！");
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (time > 0) {
                tv_countdown.setText(time + "秒后关闭页面");
                time--;
                handler.sendEmptyMessageDelayed(0, 1000);
            } else {
                fm.popBackStack();
                int n = 0;
                if (ContentCommon.into_lerun_type == 0) {
                    switch (type) {
                        case 2:
                            n = 2;
                            break;
                        case 5:
                            n = 3;
                            break;
                    }
                    for (int i = 0; i < n; i++) {
                        getActivity().getSupportFragmentManager().popBackStack(null, 0);
                    }
                } else if (ContentCommon.into_lerun_type == 1) {
                    switch (type) {
                        case 2:
                            n = 3;
                            break;
                        case 5:
                            n = 4;
                            break;
                    }
                    for (int i = 0; i < n; i++) {
                        getActivity().getSupportFragmentManager().popBackStack(null, 0);
                    }
                }
                Utility.replace2DetailFragment(fm, new myLeRunFragment());
            }
        }
    };

    //主界面获取焦点
    private void getFocus() {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // 监听到返回按钮点击事件
                    return true;
                }
                return false;
            }
        });
    }
}
