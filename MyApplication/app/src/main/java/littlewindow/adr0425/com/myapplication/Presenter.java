package littlewindow.adr0425.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

/**
 * Created by wujf17000 on 2016/10/24.
 */

public class Presenter {

    Context mContext;

    public Presenter(Context mContext){
        this.mContext = mContext;
    }

    public void showOrHideLittleWindow(View view){
        if(((Button)view).getText().equals(mContext.getString(R.string.show_float))){
            Intent intent = new Intent(mContext, LittleWindowService.class);
            //启动FxService
            mContext.startService(intent);
            ((Button)view).setText(R.string.hide_float);
        }else{
            Intent intent = new Intent(mContext, LittleWindowService.class);
            //终止FxService
            mContext.stopService(intent);
            ((Button)view).setText(R.string.show_float);
        }
    }
}
