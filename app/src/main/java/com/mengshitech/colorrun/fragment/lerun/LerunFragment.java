package com.mengshitech.colorrun.fragment.lerun;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.mengshitech.colorrun.MainActivity;
import com.mengshitech.colorrun.R;
import com.mengshitech.colorrun.activity.LoginActivity;
import com.mengshitech.colorrun.activity.SpaceImageDetailActivity;
import com.mengshitech.colorrun.activity.VideoActivity;
import com.mengshitech.colorrun.adapter.LeRunGridViewAdapter;
import com.mengshitech.colorrun.adapter.LeRunListViewAdapter;
import com.mengshitech.colorrun.adapter.LeRunVpAdapter;
import com.mengshitech.colorrun.bean.LeRunEntity;
import com.mengshitech.colorrun.bean.LunBoEntity;
import com.mengshitech.colorrun.bean.VideoEntity;
import com.mengshitech.colorrun.customcontrols.AutoSwipeRefreshLayout;
import com.mengshitech.colorrun.customcontrols.QrcodeDialog;
import com.mengshitech.colorrun.utils.HttpUtils;
import com.mengshitech.colorrun.utils.ContentCommon;
import com.mengshitech.colorrun.utils.JsonTools;
import com.mengshitech.colorrun.utils.Utility;
import com.mengshitech.colorrun.view.MyListView;

import org.json.JSONException;


public class LerunFragment extends Fragment implements OnClickListener, SwipeRefreshLayout.OnRefreshListener, AMapLocationListener {

    //热播视频的图片
    ImageView hotImage;
    //热播视频的url
    String video_url;
    QrcodeDialog dialog;
    ImageView img_hotfire;
    View lerunView;
    ViewPager vpLeRunAd;
    // 广告首页ViewPager
    List<ImageView> imgList;
    // 广告图片
    TextView tvleRunCity, tvLeRunActivity, tvLeRunTheme, tvLeRunSignUp, tvLeRunFootPrint, tvHotActivity, tvHotVideo;
    // 城市选择按钮
    MyListView lvLerun;
    // 活动的ListView，为了避免冲突，屏蔽了ListView的滑动事件
    GridView gvHotActivity;
    List<LeRunEntity> mLeRunList;
    // 活动的数据源
    Boolean AutoRunning = true;
    FragmentManager fm;
    // 页面布局
    Activity mActivity;
    // 广告栏是否自动滑动
    List<LeRunEntity> gideviewlist = null;
    private AutoSwipeRefreshLayout mSwipeLayout;
    private int flag = 0;
//    private TextView tvTitle;

    //声明AMapLocationClient类对象
    private AMapLocationClient locationClient = null;
    //声明mLocationOption类对象
    private AMapLocationClientOption mLocationOption = null;

    Context context;
    private String lerun_province;
    private LinearLayout llContainer;

    private int mPreviousPos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //使用缓存 使fragment保持原有状态
        mActivity = getActivity();
        context = getActivity();
        fm = getFragmentManager();
        ContentCommon.into_lerun_type = 0;
        MainActivity.rgMainBottom.setVisibility(View.VISIBLE);
        if (lerunView == null) {
            lerunView = View.inflate(mActivity, R.layout.fragment_lerun, null);
            findById();
        }
//        ViewGroup parent = (ViewGroup) lerunView.getParent();
//        if (parent != null) {
//            parent.removeView(lerunView);
//        }

//        lerunView.setFocusable(true);
//        lerunView.setFocusableInTouchMode(true);
//        lerunView.setOnKeyListener(backlistener);
        Log.i("lerunView","lerunView");
        return lerunView;

    }

    private void findById() {

        mSwipeLayout = new AutoSwipeRefreshLayout(mActivity);
        mSwipeLayout = (AutoSwipeRefreshLayout) lerunView.findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setColorSchemeColors(android.graphics.Color.parseColor("#87CEFA"));
        mSwipeLayout.setOnRefreshListener(this);


        new Thread(getLunBOimageRunnable).start();
        mSwipeLayout.autoRefresh();
        new Thread(videoRunnable).start();
        if (ContentCommon.INTENT_STATE) {
//            Toast.makeText(context,"网络状态:"+ContentCommon.INTENT_STATE,Toast.LENGTH_SHORT).show();
            mSwipeLayout.autoRefresh();
        }


        //热门视频图片
        hotImage = (ImageView) lerunView.findViewById(R.id.ivHotView);
//        tvTitle = (TextView) lerunView.findViewById(R.id.tv_title);
        llContainer = (LinearLayout) lerunView.findViewById(R.id.ll_container);
        vpLeRunAd = (ViewPager) lerunView.findViewById(R.id.vpLeRunAD);
        // 顶部ViewPager滚动栏
        tvLeRunActivity = (TextView) lerunView.findViewById(R.id.tvLeRunActivity);
        // 活动按钮
        tvLeRunTheme = (TextView) lerunView.findViewById(R.id.tvLeRunTheme);
        // 主题按钮
        tvLeRunFootPrint = (TextView) lerunView.findViewById(R.id.tvLeRunFootPrint);
        // 足迹按钮
        tvLeRunSignUp = (TextView) lerunView.findViewById(R.id.tvLeRunSignUp);

        //热门活动标题
        tvHotActivity = (TextView) lerunView.findViewById(R.id.tvHotActivity);
        //热门视频标题
        tvHotVideo = (TextView) lerunView.findViewById(R.id.tvHotVideo);
        // 签到按钮
        tvleRunCity = (TextView) lerunView.findViewById(R.id.tvleRunCity);
        tvleRunCity.setText("江西");
        // 城市选择按钮
        lvLerun = (MyListView) lerunView.findViewById(R.id.lvLerun);
        gvHotActivity = (GridView) lerunView.findViewById(R.id.gvHotActivity);
        // 活动的listView
        Location();
        initView();
    }

    private void initView() {


//        fm = getFragmentManager();
        //初始化fm给ListView、GridView用
//热播图片的点击事件
        hotImage.setOnClickListener(this);
        tvleRunCity.setOnClickListener(this);
        tvLeRunActivity.setOnClickListener(this);
        tvLeRunTheme.setOnClickListener(this);
        tvLeRunFootPrint.setOnClickListener(this);
        tvLeRunSignUp.setOnClickListener(this);
        Utility.changeRightDrawableSize(tvHotActivity, R.mipmap.hot_fire, 30, 30);
        Utility.changeRightDrawableSize(tvHotVideo, R.mipmap.hot_vido, 30, 30);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLeRunActivity:
                // 活动按钮
                ContentCommon.into_lerun_type = 1;
                Utility.replace2DetailFragment(fm, new LerunEventListView());
                break;
            case R.id.tvLeRunTheme:
                //主题按钮
                Utility.replace2DetailFragment(fm, new LeRunThemePager());
                break;
            case R.id.tvLeRunFootPrint:
                // 足迹按钮
                String image_path = ContentCommon.path + "lerunposter/footmark.png";
                Intent intent1 = new Intent(context, SpaceImageDetailActivity.class);
                intent1.putExtra("image_path", image_path);
                intent1.putExtra("position", 0);
                int[] location = new int[2];
//                holder.grid_image.getLocationOnScreen(location);
                intent1.putExtra("locationX", location[0]);
                intent1.putExtra("locationY", location[1]);
                intent1.putExtra("width", 0);
                intent1.putExtra("height", 0);
                mActivity.startActivity(intent1);
                mActivity.overridePendingTransition(0, 0);
                break;
            case R.id.tvLeRunSignUp:
                if (ContentCommon.login_state.equals("1")) {
                    new Thread(getQrCodeRunnable).start();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                }

                break;
            case R.id.tvleRunCity:
                Utility.replace2DetailFragment(fm, new ChoseProvinceFragment(new ChoseProvinceFragment.GetProvince() {
                    @Override
                    public String getprovince(String provice) {

                        Log.i("provice", provice + "");
                        tvleRunCity.setText(provice);
                        lerun_province = provice;
                        handler.sendEmptyMessage(0);
                        return null;
                    }
                }));
                // 城市选择按钮
                break;
            case R.id.ivHotView:

                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("video_url", video_url);
                startActivity(intent);
                break;

            default:
                break;
        }
    }


    //获取轮播照片
    Runnable getLunBOimageRunnable = new Runnable() {
        @Override
        public void run() {
            String path = ContentCommon.PATH;
            Map<String, String> map = new HashMap<String, String>();
            map.put("flag", "lunbo");
            map.put("index", "0");

            String jsonString = HttpUtils.sendHttpClientPost(path, map, "utf-8");
//            if(jsonString.equals("timeout")){
//                mSwipeLayout.setRefreshing(false);
//            }
            try {
                List<LunBoEntity> result = JsonTools.getLunboImageInfo("datas", jsonString);
                Message msg = new Message();
                msg.obj = result;

                LunBOhandler.sendMessage(msg);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }


        }
    };
    //
    Handler LunBOhandler = new Handler() {

        public void handleMessage(Message msg) {
            List<LunBoEntity> list = (List<LunBoEntity>) msg.obj;
            imgList = new ArrayList<ImageView>();
            for (int i = 0; i < list.size(); i++) {
                LunBoEntity entity = list.get(i);
                ImageView img = new ImageView(mActivity);
                img.setScaleType(ScaleType.FIT_XY);
                Glide.with(mActivity).load(entity.getLunbo_image()).into(img);
                imgList.add(img);

            }

//            vpLeRunAd
//                    .setAdapter(new LeRunVpAdapter(context, imgList, vpLeRunAd, AutoRunning));
            initViewPager();
//            mSwipeLayout.setRefreshing(false);


        }
    };

    //获取lerun主题信息
    Runnable getLeRunRunnable = new Runnable() {
        @Override
        public void run() {
            String Path = ContentCommon.PATH;
            Map<String, String> map = new HashMap<String, String>();
            map.put("flag", "lerun");
            map.put("index", "0");
            map.put("lerun_province", lerun_province);
            String result = HttpUtils.sendHttpClientPost(Path, map, "utf-8");
            Message msg = new Message();
            msg.obj = result;
            LerunInfohandler.sendMessage(msg);


        }
    };

    Handler LerunInfohandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Log.i("主题信息:",""+result);
            if (result.equals("timeout")) {

                mSwipeLayout.setRefreshing(false);
            } else {
                try {
                    List<LeRunEntity> lerunlist = JsonTools.getLerunInfo("result", result);
                    lvLerun.setAdapter(new LeRunListViewAdapter(mActivity, lerunlist, fm,
                            lvLerun));
                    //热门活动gridview
                    gideviewlist = new ArrayList<LeRunEntity>();
                    for (int i = 0; i < 2; i++) {
                        LeRunEntity entity = lerunlist.get(i);

                        gideviewlist.add(entity);
                    }

                    gvHotActivity.setAdapter(new LeRunGridViewAdapter(mActivity, gideviewlist, fm, gvHotActivity));
                    mSwipeLayout.setRefreshing(false);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    //获取热门视频信息的线程

    Runnable videoRunnable = new Runnable() {
        @Override
        public void run() {
            String Path = ContentCommon.PATH;
            Map<String, String> map = new HashMap<String, String>();
            map.put("flag", "lunbo");
            map.put("index", "1");
            String result = HttpUtils.sendHttpClientPost(Path, map, "utf-8");
            Message msg = new Message();
            msg.obj = result;
            videoHandler.sendMessage(msg);

        }
    };

    Handler videoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;

            if (result.equals("timeout")) {
                Toast.makeText(mActivity, "连接服务器超时", Toast.LENGTH_SHORT).show();
                mSwipeLayout.setRefreshing(false);
            } else {
                try {
                    List<VideoEntity> list = JsonTools.getVideoInfo(result);
                    VideoEntity entity = list.get(0);
                    Glide.with(mActivity).load(entity.getVideo_image()).into(hotImage);
                    video_url = entity.getVideo_url();
                    Log.i("video_url", video_url + "");
//                    mSwipeLayout.setRefreshing(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };


    //获取二维码
    Runnable getQrCodeRunnable = new Runnable() {
        @Override
        public void run() {
            String user_id = ContentCommon.user_id;
            String Path = ContentCommon.PATH;
            Map<String, String> map = new HashMap<String, String>();
            map.put("flag", "lerun");
            map.put("index", "10");
            map.put("user_id", user_id);
            map.put("user_telphone", user_id);
            String result = HttpUtils.sendHttpClientPost(Path, map, "utf-8");
            Message msg = new Message();
            msg.obj = result;
            QrcodeHanler.sendMessage(msg);

        }
    };

    Handler QrcodeHanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            try {
                String qr_image = JsonTools.getDatas(result);

//                dialog = new QrcodeDialog(mActivity, R.layout.dialog_qrcode, R.style.dialog, new QrcodeDialog.QrcodeDialogListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                }, qr_image);
//                dialog.show();
                Bundle bundle = new Bundle();
                bundle.putString("qrcode_image", qr_image + "");
                bundle.putInt("type", 1);
                DisplayQRcodeFragment displayQRcodeFragment = new DisplayQRcodeFragment();

                displayQRcodeFragment.setArguments(bundle);
                Utility.replace2DetailFragment(getFragmentManager(), displayQRcodeFragment);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onRefresh() {



        new Thread(getLeRunRunnable).start();


    }


    private void Location() {
        //初始化locationManager方法
        locationClient = new AMapLocationClient(mActivity);
        //设置定位回调监听，这里要实现AMapLocationListener接口，AMapLocationListener接口只有onLocationChanged方法可以实现，用于接收异步返回的定位结果，参数是AMapLocation类型。
        locationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000000);
        //给定位客户端对象设置定位参数
        locationClient.setLocationOption(mLocationOption);
        //启动定位
        locationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        System.out.println("城市" + aMapLocation.getCity());
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
//                //定位成功回调信息，设置相关消息
//                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
//                aMapLocation.getLatitude();//获取纬度
//                aMapLocation.getLongitude();//获取经度
//                aMapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);//定位时间
//                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                aMapLocation.getCountry();//国家信息
//                aMapLocation.getProvince();//省信息
//                aMapLocation.getCity();//城市信息
//                aMapLocation.getDistrict();//城区信息
//                aMapLocation.getStreet();//街道信息
//                aMapLocation.getStreetNum();//街道门牌号信息
//                aMapLocation.getCityCode();//城市编码
//                aMapLocation.getAdCode();//地区编码
                tvleRunCity.setText(aMapLocation.getProvince());
                lerun_province = aMapLocation.getProvince();

                mSwipeLayout.autoRefresh();
                Log.e("AmapS", aMapLocation.getProvince());
            } else {
                tvleRunCity.setText("江西");
                lerun_province = "江西";
                mSwipeLayout.autoRefresh();
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());

            }
        }

    }

    //handler 更新省份后重新请求数据
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mSwipeLayout.autoRefresh();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    //轮播

    public void initViewPager() {


//        vpLeRunAd.setAdapter(new MyAdapter(context));// 给viewpager设置数据
        vpLeRunAd
                .setAdapter(new LeRunVpAdapter(context, imgList, vpLeRunAd, AutoRunning));
        vpLeRunAd.setCurrentItem(Integer.MAX_VALUE / 2);
        vpLeRunAd.setCurrentItem(imgList.size() * 10000);

        // 设置滑动监听
        vpLeRunAd.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // 某个页面被选中
            @Override
            public void onPageSelected(int position) {
                int pos = position % imgList.size();
//                tvTitle.setText("新闻");// 更新新闻标题

                // 更新小圆点
                llContainer.getChildAt(pos).setEnabled(true);// 将选中的页面的圆点设置为红色
                // 将上一个圆点变为灰色
                llContainer.getChildAt(mPreviousPos).setEnabled(false);

                // 更新上一个页面位置
                mPreviousPos = pos;
            }

            // 滑动过程中
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            // 滑动状态变化
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        tvTitle.setText("新闻");// 初始化新闻标题
        Log.i("imgList.size():", imgList.size() + "");
        // 动态添加5个小圆点
        for (int i = 0; i < imgList.size(); i++) {
            ImageView view = new ImageView(context);
            view.setImageResource(R.drawable.shape_point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i != 0) {// 从第2个圆点开始设置左边距, 保证圆点之间的间距
                params.leftMargin = 6;
                view.setEnabled(false);// 设置不可用, 变为灰色圆点
            }

            view.setLayoutParams(params);

            llContainer.addView(view);
        }

        // 延时2秒更新广告条的消息
        mHandler.sendEmptyMessageDelayed(0,3000);
        vpLeRunAd.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler.removeCallbacksAndMessages(null);// 清除所有消息和Runnable对象
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mHandler.removeCallbacksAndMessages(null);
                        break;

                    case MotionEvent.ACTION_UP:
                        // 继续轮播广告
                        mHandler.sendEmptyMessageDelayed(0, 3000);
                        break;

                    default:
                        break;
                }

                return false;// 返回false, 让viewpager原生触摸效果正常运行
            }
        });

    }


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int currentItem = vpLeRunAd.getCurrentItem();// 获取当前页面位置
            vpLeRunAd.setCurrentItem(++currentItem);// 跳到下一个页面

            // 继续发送延时2秒的消息, 形成类似递归的效果, 使广告一直循环切换
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }

        ;
    };



//    long mPressedTime = 0;
//    private View.OnKeyListener backlistener = new View.OnKeyListener() {
//        @Override
//        public boolean onKey(View view, int i, KeyEvent keyEvent) {
//            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                if (i == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
//                    long mNowTime = System.currentTimeMillis();
//                    Log.i("1backmNowTime",mNowTime+"");
//                    if ((mNowTime - mPressedTime) > 2000) {// 比较两次按键时间差
//                        Toast.makeText(getActivity(),"再按一次退出程序", Toast.LENGTH_SHORT).show();
//                        getFragmentManager().beginTransaction().addToBackStack(null).commit();
//                        mPressedTime = mNowTime;
//                        Log.i("1backmPressedTime",mNowTime+"");
//                    }
//                }
//            }
//            return false;
//        }
//    };
//
//    @Override
//    public void onResume(){
//        super.onResume();
//        lerunView.setFocusable(true);//这个和下面的这个命令必须要设置了，才能监听back事件。
//        lerunView.setFocusableInTouchMode(true);
//        lerunView.setOnKeyListener(backlistener);
//        Log.i("backlistener","backlistener");
//    }


}
