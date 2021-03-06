package com.mengshitech.colorrun.activity;

import android.app.Activity;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mengshitech.colorrun.R;
import com.mengshitech.colorrun.utils.EncryptMD5;
import com.mengshitech.colorrun.utils.HttpUtils;
import com.mengshitech.colorrun.utils.ContentCommon;
import com.mengshitech.colorrun.utils.MainBackUtility;

/**
 * Created by kanghuicong on 2016/7/26  12:25.
 * 515849594@qq.com
 */
public class RegisterPwd extends Activity {
    private EditText pwd, repwd;
    private Button commit;
    private TextView tvnumber,tv_text;
    private String password, repassword, number,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pwd);
        // 获取从上一个activity传过来的数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        number = bundle.getString("number");
        type = bundle.getString("type");
        // 获取布局控件
        tvnumber = (TextView) findViewById(R.id.tv_registered_3_number);
        tv_text = (TextView)findViewById(R.id.tv_register_pwd_text) ;
        pwd = (EditText) findViewById(R.id.et_registered3_pwd);
        repwd = (EditText) findViewById(R.id.et_registered3_repwd);
        commit = (Button) findViewById(R.id.btn_registered3_commit);
        tvnumber.setText(bundle.getString("number"));

        if ("find_pwd".equals(type)){
            tv_text.setText("您的账号为:");
            MainBackUtility.MainBackActivity(RegisterPwd.this, "修改密码");
        }else if ("register".equals(type)){
            MainBackUtility.MainBackActivity(RegisterPwd.this, "填写密码");
        }

        pwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

        // 为提交按钮设置监听事件
        commit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                password = pwd.getText().toString();
                repassword = repwd.getText().toString();
                // 先判断密码是否符合要求
                if (ispwd(password)) {
                    if (password.equals(repassword)) {
                        if (password.length()>=6&&password.length()<=16) {
                            if ("register".equals(type)) {
                                new Thread(runnable_register).start();
                            }else if ("find_pwd".equals(type)){
                                new Thread(runnable_find_pwd).start();
                            }

                        }else {
                            Toast.makeText(RegisterPwd.this, "请输入6~16位密码", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterPwd.this,
                                "两次输入的密码不相同，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterPwd.this, "您输入的密码不符合规范", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    Runnable runnable_register = new Runnable() {

        @Override
        public void run() {
            String path = ContentCommon.PATH;
            //加密
//            String pwd= EncryptMD5.md5(repassword);
            Map<String, String> map = new HashMap<String, String>();

            map.put("user_id", number);
            map.put("user_pwd",repassword);
            map.put("index", "0");
            map.put("flag","user");
            String result = HttpUtils.sendHttpClientPost(path, map,
                    "utf-8");
            Message msg = new Message();
            msg.obj = result;
            handler.sendMessage(msg);
        }
    };

    // 执行runnable1
    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            String result = (String) msg.obj;

            // 判断从服务器端返回来的值是否为1，如果为1则注册成功，同时将用户信息存入sqlite数据库并跳转到注册成功页面
            if (result.equals("timeout")){
                Toast.makeText(RegisterPwd.this, "连接服务器失败！",
                        Toast.LENGTH_SHORT).show();
            }else if(result.equals("0")) {
                Toast.makeText(RegisterPwd.this, "注册失败！",
                        Toast.LENGTH_SHORT).show();
            }else if(result.equals("1")) {
                Intent intent = new Intent(RegisterPwd.this,RegisterSuccess.class);
                Bundle bundle = new Bundle();
                bundle.putString("type",type);
                bundle.putString("userid",number);
                bundle.putString("userpwd",repassword);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }else if(result.equals("2")) {
                Toast.makeText(RegisterPwd.this, "用户已经存在！",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

        // ʵ实现密码必须由数字 大写字母 小写字母 特殊字符 至少两种或两种以上组成
        public static boolean ispwd(String pwd) {
            String pwdPattern = "^(?![A-Z]*$)(?![a-z]*$)(?![0-9]*$)(?![^a-zA-Z0-9]*$)\\S+$";

            boolean result = Pattern.matches(pwdPattern, pwd);
            return result;
        }

    Runnable
            runnable_find_pwd = new Runnable() {

        @Override
        public void run() {
            String path = ContentCommon.PATH;
//            String pwd= EncryptMD5.md5(repassword);
            Map<String, String> map = new HashMap<String, String>();
            map.put("user_id", number);
            map.put("update_values",repassword);
            map.put("update_type","user_pwd");
            map.put("index", "3");
            map.put("flag","user");
            String result = HttpUtils.sendHttpClientPost(path, map,
                    "utf-8");
            Message msg = new Message();
            msg.obj = result;
            handler_find_pwd.sendMessage(msg);
        }
    };

    Handler handler_find_pwd = new Handler() {

        public void handleMessage(Message msg) {
            String result = (String) msg.obj;

            // 判断从服务器端返回来的值是否为1，如果为1则注册成功，同时将用户信息存入sqlite数据库并跳转到注册成功页面
            if (result.equals("timeout")){
                Toast.makeText(RegisterPwd.this, "连接服务器失败！",
                        Toast.LENGTH_SHORT).show();
            }else if(result.equals("0")) {
                Toast.makeText(RegisterPwd.this, "修改失败！",
                        Toast.LENGTH_SHORT).show();
            }else if(result.equals("1")) {
                Intent intent = new Intent(RegisterPwd.this,RegisterSuccess.class);
                Bundle bundle = new Bundle();
                bundle.putString("type",type);
                bundle.putString("userid",number);
                bundle.putString("userpwd",repassword);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }else if(result.equals("2")) {
                Toast.makeText(RegisterPwd.this, "用户已经存在！",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
}
