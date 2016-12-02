package littlewindow.adr0425.com.myapplication;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import littlewindow.adr0425.com.myapplication.view.DragButton;
import littlewindow.adr0425.com.myapplication.view.DragLinearlayout;

/**
 * Created by wujf17000 on 2016/10/24.
 */

public class LittleWindowService extends Service {

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    Button mFloatView;


    WindowManager.LayoutParams saveWmParams;

    private boolean isAttacheToWindow=false;

    private boolean isChanged=false;

    private static final String TAG = "LittleWindowService";

    private Timer timer;


    private TextView guanggaoTxt;
    private CheckBox checkBox;

    private LinearLayout linearLayout;

    private RelativeLayout relativeLayout;

    private int lastX,lastY;
    private int offX,offY;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private WindowManager.LayoutParams initParams(WindowManager.LayoutParams wmParam){
        wmParam = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParam.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParam.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParam.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParam.x = 0;
        wmParam.y = 0;

        //设置悬浮窗口长宽数据
        wmParam.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParam.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        wmParam.width = WindowManager.LayoutParams.MATCH_PARENT;
//        wmParam.height = WindowManager.LayoutParams.MATCH_PARENT;
        return wmParam;
    }

    private void createFloatView()
    {

        wmParams = initParams(wmParams);
         /*// 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;*/

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        mFloatView = (DragButton)mFloatLayout.findViewById(R.id.float_id);

        guanggaoTxt = (TextView) mFloatLayout.findViewById(R.id.guanggaotxt);

        guanggaoTxt.setVisibility(getSharedPreferences(MainActivity.share,Context.MODE_PRIVATE).getBoolean(MainActivity.switchguanggao,false)?View.VISIBLE:View.INVISIBLE);

        relativeLayout = (RelativeLayout) mFloatLayout.findViewById(R.id.guanggaorel);

        relativeLayout.setVisibility(mFloatView.getVisibility() == View.VISIBLE?View.GONE:View.VISIBLE);

        checkBox = (CheckBox) mFloatLayout.findViewById(R.id.controlBt);

        checkBox.setChecked(mFloatView.getVisibility() == View.VISIBLE);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFloatView.setVisibility(isChecked?View.VISIBLE:View.GONE);
                relativeLayout.setVisibility(isChecked?View.GONE:View.VISIBLE);
//                Log.e(TAG,"offX::  "+offX+"  offY:"+offY +"  v.getLeft()："+mFloatView.getLeft()+" v.getTop()： "+mFloatView.getTop() );
//                mFloatView.layout(mFloatView.getLeft()+offX, mFloatView.getTop()+offY,
//                        mFloatView.getRight()+offX  , mFloatView.getBottom()+offY);
//
//                ViewGroup.MarginLayoutParams mlp =
//                        (ViewGroup.MarginLayoutParams) mFloatView.getLayoutParams();
//                mlp.leftMargin = mFloatView.getLeft()+offX;
//                mlp.topMargin = mFloatView.getTop()+offY;
//                mFloatView.setLayoutParams(mlp);
            }
        });

//        mFloatLayout.setClickable(false);

//        linearLayout = (DragLinearlayout) mFloatLayout.findViewById(R.id.float_lin);
//        linearLayout.setClickable(false);
//        linearLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//        linearLayout.onInterceptTouchEvent();

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),
                             View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth()/2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight()/2);
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth()/2;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "X" + event.getX());
                //减25为状态栏的高度
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight()/2 - 25;
                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "Y" + event.getY());
                //刷新
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;  //此处必须返回false，否则OnClickListener获取不到监听

//                //获取到手指处的横坐标和纵坐标
//                int x = (int) event.getX();
//                int y = (int) event.getY();
//                switch(event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        lastX = x;
//                        lastY = y;
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        //计算移动的距离
//                        offX = x - lastX;
//                        offY = y - lastY;
//                        //调用layout方法来重新放置它的位置
////                        v.layout(v.getLeft()+offX, v.getTop()+offY,
////                                v.getRight()+offX  , v.getBottom()+offY);
//                        ViewGroup.MarginLayoutParams mlp =
//                                (ViewGroup.MarginLayoutParams) v.getLayoutParams();
//                        mlp.leftMargin = v.getLeft()+offX;
//                        mlp.topMargin = v.getTop()+offY;
//                        v.setLayoutParams(mlp);
////                        Log.e(TAG,"ACTION_MOVE  offX::  "+offX+"  offY:"+offY +"  v.getLeft()："+v.getLeft()+" v.getTop()： "+v.getTop() );
//                        break;
//                }
//                return false;
            }
        });

        mFloatView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {

               /* if(saveWmParams==null){
                    saveWmParams = initParams(saveWmParams);
                    saveWmParams.x=wmParams.x;
                    saveWmParams.y =wmParams.y;
                }
                Toast.makeText(LittleWindowService.this, "遮挡位置信息保存成功", Toast.LENGTH_SHORT).show();*/
                return false;
            }
        });
//
//        mFloatView.setOnClickListener(new View.OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//
//                relativeLayout.setVisibility(View.VISIBLE);
//                checkBox.setChecked(false);
//                mFloatView.setVisibility(View.GONE);
////                if(!isChanged){
////                    mFloatView.animate().scaleY(0.3f).scaleX(0.3f).alpha(0.5f).start();
////                    isChanged= true;
////                }else{
////                    mFloatView.animate().scaleY(1f).scaleX(1f).alpha(1f).start();
////                    isChanged= false;
////                }
//
//            }
//        });
        mFloatView.setOnClickListener(listener);


    }

    private boolean waitDouble = true;
    private int DOUBLE_CLICK_TIME = 200;
    View.OnClickListener listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if ( waitDouble == true )
            {
                waitDouble = false;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(DOUBLE_CLICK_TIME);
                            if ( waitDouble == false ) {
                                waitDouble = true;
                                singleClick();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
            else {
                waitDouble = true;
                doubleClick();
            }
        }
    };

    // 单击响应事件
    private void singleClick(){
        Log.i(TAG, "singleClick");
    }

    // 双击响应事件
    private void doubleClick(){
        relativeLayout.setVisibility(View.VISIBLE);
        checkBox.setChecked(false);
        mFloatView.setVisibility(View.GONE);
        Log.i(TAG, "doubleClick");
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(mFloatLayout != null)
        {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
            isAttacheToWindow= false;
        }
    }

    private Handler handler = new Handler();

    class RefreshTask extends TimerTask {
        @Override
        public void run() {

            // 当前界面是微信，且没有悬浮窗显示，则创建悬浮窗。
            String nowApp = getForegroundApp();
//            getRunningActivityName();
//           String pkgName =  getCurrentPkgName(LittleWindowService.this);
            getTopApp(LittleWindowService.this);
            Log.v(TAG,"============nowApp: "+nowApp);
            Log.v(TAG,"------------mFloatLayout:"+mFloatLayout);
            if (Constans.WEIXIN_PACKAGE.equals(nowApp)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mFloatLayout==null){
                            createFloatView();
                        }else if(!isAttacheToWindow){
                            mWindowManager.addView(mFloatLayout, wmParams);
                            //mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        }
                        isAttacheToWindow=true;

                    }
                });
            }
            // 当前界面不是桌面，且有悬浮窗显示，则移除悬浮窗。
            else if (!Constans.WEIXIN_PACKAGE.equals(nowApp) && !Constans.SYSTEM_UI_PACKAGE.equals(nowApp)&& nowApp!=null && mFloatLayout!=null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isAttacheToWindow){
                            mWindowManager.removeView(mFloatLayout);
                            isAttacheToWindow= false;
                        }

                    }
                });
            }
            /*// 当前界面是桌面，且有悬浮窗显示，则更新内存数据。
            else if (isHome() && MyWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.updateUsedPercent(getApplicationContext());
                    }
                });
            }*/
        }

    }

    /*private boolean isWeiXin(){
        if(getForegroundApp()==null){
            return null;
        }
        return Constans.WEIXIN_PACKAGE.equals(getForegroundApp())?true:false;
    }*/



    private String getForegroundApp() {

        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager=(UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,ts-2000, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }
        UsageStats recentStats = null;
        for (UsageStats usageStats : queryUsageStats) {
            if(recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()){
                recentStats = usageStats;
            }
        }
        return recentStats.getPackageName();
    }



    private String getRunningActivityName(){
        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        Log.e(TAG,"============runningActivity: "+runningActivity);
        return runningActivity;
    }


    public static String getCurrentPkgName(Context context) {
        ActivityManager.RunningAppProcessInfo currentInfo = null;
        Field field = null;
        int START_TASK_TO_FRONT = 2;
        String pkgName = null;
        try {
            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
        } catch (Exception e) { e.printStackTrace(); }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo app : appList) {
            if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                Integer state = null;
                try {
                    state = field.getInt( app );
                } catch (Exception e) { e.printStackTrace(); }
                if (state != null && state == START_TASK_TO_FRONT) {
                    currentInfo = app;
                    break;
                }
            }
        }
        if (currentInfo != null) {
            pkgName = currentInfo.processName;
        }
        return pkgName;
    }


    private void getTopApp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);
//                Log.e(TAG, "Running app number in last 60 seconds : " + stats.size());

                String topActivity = "";
                String xxt= "";

                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                    }
                    topActivity = stats.get(j).getPackageName();
                    xxt = stats.get(j).toString();
                }
//                Log.e(TAG, "top running app is : "+topActivity+"  xxt:"+xxt);
            }
        }
    }

}
