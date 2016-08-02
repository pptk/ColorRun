package com.mengshitech.colorrun.fragment.lerun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mengshitech.colorrun.R;
import com.mengshitech.colorrun.activity.LoginActivity;
import com.mengshitech.colorrun.bean.LeRunEntity;
import com.mengshitech.colorrun.fragment.BaseFragment;
import com.mengshitech.colorrun.utils.HttpUtils;
import com.mengshitech.colorrun.utils.IPAddress;
import com.mengshitech.colorrun.utils.JsonTools;
import com.mengshitech.colorrun.utils.MainBackUtility;
import com.mengshitech.colorrun.utils.Utility;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kanghuicong on 2016/7/15.
 */

public class IntoLerunEvent extends BaseFragment implements OnClickListener {
    View into_lerun_view;
    TimeCount countdown;
    ImageView poster, map;
    TextView address, name, lerun_time, price, number, tx_entry, end_tiem, start_time, hold_time;
    Button ll_entry;
    String time;
    int lerun_id;
    Context context;

    @Override
    public View initView() {
        into_lerun_view = View.inflate(getActivity(), R.layout.lerun_into, null);
        MainBackUtility.MainBack(into_lerun_view, "活动详情", getFragmentManager());
        lerun_id = getArguments().getInt("lerun_id");
        Log.i("lerun_id", lerun_id + "");
        context=getActivity();

        new Thread(runnable).start();
        find();
        click();// 点击事件
//        entry_type();// 查看报名的状态
        number_type();// 查看人数状态
        return into_lerun_view;
    }


    // 人数状态
    private void number_type() {
        // TODO Auto-generated method stub
        if (Integer.valueOf(number.getText().toString()) == 0) {
            tx_entry.setText("报名人数已满");
            ll_entry.setEnabled(false);
        }
    }

    // 查看报名的状态
    private void entry_type() {
        // TODO Auto-generated method stub

        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("entry_type", Activity.MODE_PRIVATE);
        String type = sharedPreferences.getString("type", "");
        String num = sharedPreferences.getString("number", "");

        if (type == "success") {
            number.setText(num + "");
            ll_entry.setBackgroundColor(Color.parseColor("#cccccc"));
            tx_entry.setText("已报名");
            ll_entry.setEnabled(false);
        }
    }

    // 倒计时
    private void Time(String time) {
        //获取活动结束时间time
        String Countdown = time + ":00";
        Log.i("Countdown", Countdown);

        String time_finish = getTime(Countdown);
        Log.i("time_finish1111", time_finish);
        long time_now = new Date().getTime();
        Log.i("time_finish", time_finish);

        int Time_finish = Integer.valueOf(time_finish) * 1000;
        int Time_now = (int) time_now;

        Log.i("时间戳", time_now + "");
        Log.i("Time_now", Time_now + "");
        Log.i("Time_finish", Time_finish + "");

        countdown.setEndTime(System.currentTimeMillis()
                + (Time_finish - Time_now));

    }

    // 活动结束时间转成时间戳
    public static String getTime(String time) {

        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
            Log.i("时间戳", re_time + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }

    private void click() {
        ll_entry.setOnClickListener(this);//报名按钮
        map.setOnClickListener(this);//点击地图放大
    }

    private void find() {
        poster = (ImageView) into_lerun_view.findViewById(R.id.into_lerun_poster);// 活动海报
        price = (TextView) into_lerun_view.findViewById(R.id.into_lerun_price);// 活动费用
        number = (TextView) into_lerun_view.findViewById(R.id.into_lerun_number);// 剩余名额
        map = (ImageView) into_lerun_view.findViewById(R.id.into_lerun_map);// 活动路线地图
        address = (TextView) into_lerun_view.findViewById(R.id.into_lerun_address);// 活动地点
        name = (TextView) into_lerun_view.findViewById(R.id.into_lerun_title);// 活动名字
        lerun_time = (TextView) into_lerun_view.findViewById(R.id.into_lerun_time);// 活动时间
        end_tiem = (TextView) into_lerun_view.findViewById(R.id.end_time);//报名截止时间
        start_time = (TextView) into_lerun_view.findViewById(R.id.start_time);//报名开始时间
        hold_time = (TextView) into_lerun_view.findViewById(R.id.hold_time);
        countdown = (TimeCount) into_lerun_view.findViewById(R.id.into_lerun_countdown);// 剩余时间
        ll_entry = (Button) into_lerun_view.findViewById(R.id.bt_into_lerun_entry);// 报名按钮
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_into_lerun_entry:

//
//                SharedPreferences sharedPreferences = getActivity()
//                        .getSharedPreferences("entry_type", Activity.MODE_PRIVATE);
//                String type = sharedPreferences.getString("type", "");

                if (IPAddress.login_state.equals("1")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("lerun_id", lerun_id);
                    bundle.putString("title", name.getText().toString());
                    bundle.putString("time", start_time.getText().toString());
                    bundle.putString("address", address.getText().toString());

                    IntoLeRunEnroll mIntoLerunEnroll = new IntoLeRunEnroll();
                    mIntoLerunEnroll.setArguments(bundle);
                    Utility.replace2DetailFragment(getFragmentManager(), mIntoLerunEnroll);
                } else {
                    Toast.makeText(context,"您还没有登陆哦,请先登录",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }


                break;
            case R.id.into_lerun_map:
                //点击地图放大
                Utility.replace2DetailFragment(getFragmentManager(), new ShowMap());
            default:
                break;
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String path = IPAddress.PATH;
            Map<String, String> map = new HashMap<String, String>();
            map.put("flag", "lerun");
            map.put("index", "1");
            map.put("lerun_id", lerun_id + "");
            String result = HttpUtils.sendHttpClientPost(path, map, "utf-8");
            Log.i("result1", result);
            Message msg = new Message();
            msg.obj = result;
            handler.sendMessage(msg);
        }
    };

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            Log.i("result2", result);
            if (result.equals("timeout")) {
//                progressDialog.dismiss();
                Toast.makeText(getActivity(), "连接服务器超时", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    LeRunEntity leRunEntity = JsonTools.getLerunEvent("result", result);
                    Log.i("LeRunEntity", leRunEntity + "");
                    price.setText(String.valueOf(leRunEntity.getCharge_common()));
                    Log.i("Charge_common", leRunEntity.getCharge_common() + "");
                    number.setText(String.valueOf(leRunEntity.getLerun_surplus()));
                    time = leRunEntity.getLerun_endtime();
                    Log.i("Lerun_endtime", leRunEntity.getLerun_endtime());
                    address.setText(leRunEntity.getLerun_address());
                    lerun_time.setText(leRunEntity.getLerun_time());
                    name.setText(leRunEntity.getLerun_title());
                    start_time.setText(leRunEntity.getLerun_begintime());
                    end_tiem.setText(leRunEntity.getLerun_endtime());
                    hold_time.setText(leRunEntity.getLerun_time());
                    String poster_path = IPAddress.path + leRunEntity.getLerun_poster();
                    Log.i("poster_path", poster_path);
                    String map_path = IPAddress.path + leRunEntity.getLerun_map();
                    Glide.with(getActivity()).load(poster_path).into(poster);
                    Glide.with(getActivity()).load(map_path).into(map);
                    Time(time);// 倒计时
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
