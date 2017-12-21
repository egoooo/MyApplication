package newjohn.com.myapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import newjohn.com.myapplication.R;
import newjohn.com.myapplication.download.DownloadUtil;
import newjohn.com.myapplication.event.ProgressEvent;


public class DownloadActivity extends BaseActivity {



    RelativeLayout relativeLayout;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        EventBus.getDefault().register(this);
        relativeLayout=findViewById(R.id.layout_doc);
        progressBar=findViewById(R.id.progress);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadUtil.get().download("http://img.my.csdn.net/uploads/201402/24/1393242467_3999.jpg", "haha");

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ProgressEvent event) {
        /* Do something */

        progressBar.setProgress(event.progress);
        if (event.progress==100){
            Toast.makeText(DownloadActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
        }

    };
}
