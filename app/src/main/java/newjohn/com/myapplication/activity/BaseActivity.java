package newjohn.com.myapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Administrator on 2017/12/17.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG ="BaseActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 添加Activity到堆栈
        AtyContainer.getInstance().addActivity(this);
        Log.i(TAG, "onCreate: "+" "+this.getClass().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity&从栈中移除该Activity
        AtyContainer.getInstance().removeActivity(this);
        Log.i(TAG, "onDestroy: "+" "+this.getClass().getName());
    }




}
