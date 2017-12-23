package newjohn.com.myapplication.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import newjohn.com.myapplication.R;

/**
 * Created by Administrator on 2017/12/7.
 */

public class CustomToast {

    private static TextView mTextView;
    private static ImageView mImageView;
    private static Toast toastStart;
    private CustomToast(){};

    public static void showToast(Context context, String message) {
        if (toastStart==null){
            //加载Toast布局
            View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast, null);
            //初始化布局控件
            mTextView = (TextView) toastRoot.findViewById(R.id.message);
            mImageView = (ImageView) toastRoot.findViewById(R.id.imageView);
            //为控件设置属性
            mTextView.setText(message);
            mImageView.setImageResource(R.drawable.alert);
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //Toast的初始化
            toastStart = new Toast(context);
            //获取屏幕高度
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int height = wm.getDefaultDisplay().getHeight();
            //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
            toastStart.setGravity(Gravity.TOP, 0, height / 3);
            toastStart.setDuration(Toast.LENGTH_LONG);
            toastStart.setView(toastRoot);


        }
        /**
         * @param fromAlpha 开始的透明度，取值是0.0f~1.0f，0.0f表示完全透明， 1.0f表示和原来一样
         * @param toAlpha 结束的透明度，同上
         */
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        //设置动画持续时长
        alphaAnimation.setDuration(500);
        //设置动画结束之后的状态是否是动画的最终状态，true，表示是保持动画结束时的最终状态
        alphaAnimation.setFillAfter(true);
        //设置动画结束之后的状态是否是动画开始时的状态，true，表示是保持动画开始时的状态
        alphaAnimation.setFillBefore(true);
        //设置动画的重复模式：反转REVERSE和重新开始RESTART
        alphaAnimation.setRepeatMode(AlphaAnimation.REVERSE);
        //设置动画播放次数
        alphaAnimation.setRepeatCount(AlphaAnimation.INFINITE);
        mImageView.startAnimation(alphaAnimation);
        mTextView.setText(message);
        toastStart.show();

    }
}
