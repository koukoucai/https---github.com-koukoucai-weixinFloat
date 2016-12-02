package littlewindow.adr0425.com.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by huanggang on 2016/11/29.
 */

public class DragButton extends Button{
    private int lastX;
    private int lastY;

    public DragButton(Context context) {
        super(context);
    }

    public DragButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DragButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //获取到手指处的横坐标和纵坐标
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        switch(event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                lastX = x;
//                lastY = y;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                //计算移动的距离
//                int offX = x - lastX;
//                int offY = y - lastY;
//                //调用layout方法来重新放置它的位置
//                layout(getLeft()+offX, getTop()+offY,
//                        getRight()+offX  , getBottom()+offY);
//                break;
//        }
//
////        return super.onTouchEvent(event);
//        return true;
//    }



}
