package littlewindow.adr0425.com.myapplication;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String share = "guanggao_share";
    public static final String switchguanggao = "guanggao";

    ImageView imageView;
    private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




//        ActivityMainBinding databinding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(databinding.getRoot());
//        Presenter presenter = new Presenter(this);
//        databinding.setPresenter(presenter);

        setContentView(R.layout.activity_main);
        findViewById(R.id.btsex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Button)view).getText().equals(MainActivity.this.getString(R.string.show_float))){
                    Intent intent = new Intent(MainActivity.this, LittleWindowService.class);
                    //启动FxService
                    startService(intent);
                    ((Button)view).setText(R.string.hide_float);
                }else{
                    Intent intent = new Intent(MainActivity.this, LittleWindowService.class);
                    //终止FxService
                        stopService(intent);
                    ((Button)view).setText(R.string.show_float);
                }
            }
        });

        checkBox = (CheckBox) findViewById(R.id.switchcb);
        SharedPreferences sharedPreferences = getSharedPreferences(share,Context.MODE_PRIVATE);
        checkBox.setChecked(sharedPreferences.getBoolean(switchguanggao,false));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(share,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(switchguanggao,isChecked);
                editor.commit();
            }
        });



//        findViewById(R.id.startweixin).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               Intent intent = new Intent();
//                intent.setClassName(MainActivity.this, "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
//                startActivity(intent);
//            }
//        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            }
        }else{
            Intent intent = new Intent(this, LittleWindowService.class);
            startService(intent);
        }

//        animateImage();


    }


//    private void animateImage() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // 获取动画效果
//            AnimatedVectorDrawable mAnimatedVectorDrawable = (AnimatedVectorDrawable)
//                    ContextCompat.getDrawable(getApplication(), R.drawable.v_heard_animation);
//            mIvImageView.setImageDrawable(mAnimatedVectorDrawable);
//            if (mAnimatedVectorDrawable != null) {
//                mAnimatedVectorDrawable.start();
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "not granted", Toast.LENGTH_SHORT);
                } else {
                    Intent intent = new Intent(this, LittleWindowService.class);
                    startService(intent);
                }
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!hasPermission()) {
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}



